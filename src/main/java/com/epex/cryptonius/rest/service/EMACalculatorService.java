package com.epex.cryptonius.rest.service;

import com.epex.cryptonius.domain.CandleStick;
import com.epex.cryptonius.enums.EMAScale;
import com.epex.cryptonius.prototype.APIFactory;
import com.epex.cryptonius.repository.*;
import com.epex.cryptonius.util.ProviderService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class EMACalculatorService {

    private static final int SMOOTHNESS_FACTOR = 2;

    private static final Logger log = LoggerFactory.getLogger(EMACalculatorService.class);

    private final APIFactory apiFactory;
    private final ProviderService providerService;
    private final TickerEntityRepository tickerRepository;
    private final EMAFifteenFiftyRepository emaFifteenFiftyRepository;

    @Autowired
    public EMACalculatorService(final APIFactory apiFactory,
                                final EMAFifteenFiftyRepository emaFifteenFiftyRepository,
                                final ProviderService providerService,
                                final TickerEntityRepository tickerRepository) {
        this.apiFactory = apiFactory;
        this.emaFifteenFiftyRepository = emaFifteenFiftyRepository;
        this.providerService = providerService;
        this.tickerRepository = tickerRepository;
    }

    /**
     * EMA50 15
     * Time Range => 15     width => 50
     *
     * @param token
     * @param timeRange
     * @param width
     * @return
     */
    public BigDecimal calculateEMA(String token, int timeRange, int width) {
        BigDecimal emaVal = BigDecimal.ZERO;
        try {
            int dataSetCount = timeRange * width;
            List<TickerEntity> tickers = tickerRepository.fetchDataSetForEMA(token, dataSetCount);
            if (tickers.isEmpty() || tickers.size() < dataSetCount) {
                log.warn("Not Enough Tickers, Skipping EMA{} Calculation In Range {} !", width, timeRange);
                return emaVal;
            }

            List<CandleStick> sticks = generateCandlesForRange(timeRange, tickers);
            if (sticks.isEmpty()) {
                log.warn("Not Enough Candlesticks, Skipping EMA{} Calculation In Range {} !", width, timeRange);
                return emaVal;
            }

            EMAEntity lastEMA = fetchLastEMAEntity(token, timeRange, width);
            BigDecimal lastEMAVal = Objects.isNull(lastEMA) ? calculateLatestSMA(sticks) : lastEMA.getValue();

            if (Objects.isNull(lastEMA)) {
                createEMAEntry(lastEMAVal, token, timeRange, width);
                return lastEMAVal;
            }

            int period = tickers.size() / timeRange;
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



    private void createEMAEntry(BigDecimal lastEMAVal, String token, int timeRange, int width) throws Exception {
        EMAScale scale = EMAScale.findScaleForWidth(timeRange, width);

        switch (scale){
            case FIFTEEN_FIFTY:
                EMAFifteenFiftyEntity e = new EMAFifteenFiftyEntity(token,lastEMAVal);
                emaFifteenFiftyRepository.save(e);
                break;
        }
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

    private EMAEntity fetchLastEMAEntity(String token, int timeRange, int width) throws Exception {
        EMAScale scale = EMAScale.findScaleForWidth(timeRange, width);

        switch (scale) {
            case FIFTEEN_FIFTY:
                return emaFifteenFiftyRepository.findTopByOrderByIdDescAndBaseUnit(token).orElse(null);
        }
        return null;
    }

    private BigDecimal calculateSmoothnessMultiplier(int timePeriod) {
        //Multiplier: (2 / (Time periods + 1) )
        return BigDecimal.valueOf((double) SMOOTHNESS_FACTOR / (timePeriod + 1));
    }
}

