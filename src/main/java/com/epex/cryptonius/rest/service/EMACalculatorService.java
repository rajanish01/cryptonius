package com.epex.cryptonius.rest.service;

import com.epex.cryptonius.domain.CandleStick;
import com.epex.cryptonius.enums.EMAScale;
import com.epex.cryptonius.prototype.APIFactory;
import com.epex.cryptonius.repository.EMAEntity;
import com.epex.cryptonius.repository.EMAEntityRepository;
import com.epex.cryptonius.repository.TickerEntity;
import com.epex.cryptonius.util.ProviderService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EMACalculatorService {

    private static final int SMOOTHNESS_FACTOR = 2;

    private static final Logger log = LoggerFactory.getLogger(EMACalculatorService.class);

    private final EMAEntityRepository emaRepository;
    private final APIFactory apiFactory;
    private final ProviderService providerService;

    @Autowired
    @SuppressWarnings("unused")
    private MongoTemplate mongoTemplate;

    @Autowired
    public EMACalculatorService(final APIFactory apiFactory,
                                final EMAEntityRepository emaRepository,
                                final ProviderService providerService) {
        this.apiFactory = apiFactory;
        this.emaRepository = emaRepository;
        this.providerService = providerService;
    }

    public BigDecimal calculateLatestEMA(String token, int range, int width) {
        BigDecimal emaVal = BigDecimal.ZERO;
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("base_unit").is(token));
            query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, "at"))).limit(range * width);

            List<TickerEntity> tickers = mongoTemplate.find(query, TickerEntity.class);
            if (tickers.isEmpty() || tickers.size() < range * width) {
                log.warn("Not Enough Tickers, Skipping EMA{} Calculation In Range {} !", width, range);
                return emaVal;
            }

            List<CandleStick> sticks = generateCandlesForRange(range, tickers);
            if (sticks.isEmpty()) {
                log.warn("Not Enough Candlesticks, Skipping EMA{} Calculation In Range {} !", width, range);
                return emaVal;
            }

            Optional<EMAEntity> lastEMA = fetchLastEMAEntity(token, width);
            BigDecimal lastEMAVal = lastEMA.isEmpty() ?
                    calculateLatestSMA(sticks) : filterEMAValueFromDB(width, lastEMA.get());

            if (lastEMA.isEmpty()) {
                createEMAEntry(lastEMAVal, token, width);
                return lastEMAVal;
            }

            int period = tickers.size() / range;
            BigDecimal multiplier = calculateSmoothnessMultiplier(period);
            BigDecimal closingPrice = providerService.filterTickerForTokenFromResponse(
                    apiFactory.getLatestTicker(), token).getLast();

            //EMA: {Close - EMA(previous day)} x multiplier + EMA(previous day)
            emaVal = (closingPrice.subtract(emaVal)).multiply(multiplier).add(emaVal);

        } catch (Exception emaCalculationError) {
            log.error("Error While EMA Calculation : " + emaCalculationError.getMessage());
        }
        return emaVal;
    }

    private List<CandleStick> generateCandlesForRange(int range, List<TickerEntity> tickers) {
        List<CandleStick> sticks = Lists.newArrayList();
        int buckets = tickers.size() / range;

        int idx = 0;
        for (int i = 0; i < buckets; i++) {
            CandleStick c = new CandleStick(range);
            c.setOpen(tickers.get(idx).getOpen());
            c.setOpenTimestamp(tickers.get(idx).getAt());
            c.setClose(tickers.get(idx + range - 1).getLast());
            c.setCloseTimeStamp(tickers.get(idx + range - 1).getAt());
            sticks.add(c);
            idx += range;
        }
        return sticks;
    }

    private BigDecimal calculateLatestSMA(List<CandleStick> sticks) {
        BigDecimal sum = sticks
                .stream()
                .map(CandleStick::getClose)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(sticks.size()), RoundingMode.CEILING);
    }

    private Optional<EMAEntity> fetchLastEMAEntity(String token, int width) {
        Query query = new Query();
        query.addCriteria(criteriaBuilder(width).and("base_unit").is(token));
        query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, "at"))).limit(1);

        EMAEntity lastEMA = mongoTemplate.findOne(query, EMAEntity.class);

        return Objects.isNull(lastEMA) ? Optional.empty() : Optional.of(lastEMA);
    }

    private BigDecimal calculateSmoothnessMultiplier(int timePeriod) {
        //Multiplier: (2 / (Time periods + 1) )
        return BigDecimal.valueOf((double) SMOOTHNESS_FACTOR / (timePeriod + 1));
    }

    private BigDecimal filterEMAValueFromDB(int width, EMAEntity lastEMA) {
        try {
            EMAScale scale = EMAScale.findScaleForWidth(width);

            switch (scale) {
                case FIFTY:
                    return lastEMA.getEmaFifty();
                case TWO_HUNDRED:
                    return lastEMA.getEmaTwoHundred();
            }
        } catch (Exception filterException) {
            log.error("Error While EMA Filtering : {}", filterException.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private Criteria criteriaBuilder(int width) {
        try {
            EMAScale scale = EMAScale.findScaleForWidth(width);

            switch (scale) {
                case FIFTY:
                    return Criteria.where("emaFiftyFlag").is(true);
                case TWO_HUNDRED:
                    return Criteria.where("emaTwoHundredFlag").is(true);
            }
        } catch (Exception filterException) {
            log.error("Error While EMA Filtering : {}", filterException.getMessage());
        }
        return new Criteria();
    }

    private void createEMAEntry(BigDecimal emaV, String token, int width) throws Exception {
        EMAEntity ema = new EMAEntity(token);
        EMAScale scale = EMAScale.findScaleForWidth(width);
        switch (scale) {
            case FIFTY:
                ema.setEmaFifty(emaV);
                ema.setEmaFiftyFlag(true);
                break;
            case TWO_HUNDRED:
                ema.setEmaTwoHundred(emaV);
                ema.setEmaTwoHundredFlag(true);
                break;
        }
        emaRepository.save(ema);
    }
}

