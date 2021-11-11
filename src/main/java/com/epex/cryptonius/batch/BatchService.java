package com.epex.cryptonius.batch;

import com.epex.cryptonius.prototype.APIFactory;
import com.epex.cryptonius.rest.service.EMACalculatorService;
import com.epex.cryptonius.rest.service.TickerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class BatchService {

    private static final Logger log = LoggerFactory.getLogger(BatchService.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private static final int TICKER_DELAY = 60;    //seconds

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

    @Scheduled(fixedRate = TICKER_DELAY * 1000)
    public void tickerScheduler() {
        log.info("Starting Ticker Recording !");
        if (tickerService.persist(apiFactory.getLatestTicker())) {
            log.info("Ticker Recording Successful !");
        } else {
            log.error("Something Went Wrong, While Ticker Recording !");
        }
    }

    @Scheduled(fixedRate = 5 * 1000)
    public void ema50Scheduler() {
        emaCalculatorService.calculateLatestEMA("btc", 15, 50);
    }
}
