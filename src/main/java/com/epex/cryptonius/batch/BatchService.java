package com.epex.cryptonius.batch;

import com.epex.cryptonius.prototype.APIFactory;
import com.epex.cryptonius.rest.service.EMACalculatorService;
import com.epex.cryptonius.rest.service.TickerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class BatchService {

    private static final Logger log = LoggerFactory.getLogger(BatchService.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private static final int MILLIS = 1000;
    private static final int TICKER_DELAY = 1;    //seconds
    private static final int EMA50_15_MIN_DELAY = 1;

    private final APIFactory apiFactory;
    private final TickerService tickerService;
    private final EMACalculatorService emaCalculatorService;

    @Autowired
    public BatchService(final APIFactory apiFactory,
                        final TickerService tickerService,
                        final EMACalculatorService emaCalculatorService) {
        this.apiFactory = apiFactory;
        this.tickerService = tickerService;
        this.emaCalculatorService = emaCalculatorService;
    }

    @Scheduled(fixedRate = TICKER_DELAY * MILLIS)
    public void tickerScheduler() {
        log.debug("Starting Ticker Recording !");
        if (tickerService.persist(apiFactory.getLatestTicker())) {
            log.info("Ticker Recording Successful For Timestamp {} !", dateFormat.format(new Date()));
        } else {
            log.error("Something Went Wrong, While Ticker Recording !");
        }
    }

    /**
     * EMA50 & 15 MIN
     */
    @Scheduled(fixedRate = EMA50_15_MIN_DELAY * MILLIS, initialDelay = MILLIS)
    //@Scheduled(fixedRate = 1000)      //Uncomment For Testing
    public void ema50Scheduler() {
        try {
            log.debug("Calculating EMA50 With 15 MIN Width !");
            if (emaCalculatorService.calculateLatestEMA("btc", 15, 50)
                    .compareTo(BigDecimal.ZERO) > 0) {
                log.info("EMA50 With 15 MIN Width Generated !");
            }
        } catch (Exception error) {
            log.error("EMA Calculation Error !");
        }
    }
}
