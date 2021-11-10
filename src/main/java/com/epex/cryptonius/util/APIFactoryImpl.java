package com.epex.cryptonius.util;

import com.epex.cryptonius.prototype.APIFactory;
import com.epex.cryptonius.repository.TickerEntity;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class APIFactoryImpl implements APIFactory {

    private final HttpService httpService;
    private final Gson gson;
    private final ProviderService providerService;


    private static final Logger log = LoggerFactory.getLogger(APIFactoryImpl.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private static final String TICKER_API = "https://api.wazirx.com/api/v2/tickers";


    @Autowired
    public APIFactoryImpl(final HttpService httpService,
                          final Gson gson,
                          final ProviderService providerService) {
        this.httpService = httpService;
        this.gson = gson;
        this.providerService = providerService;
    }

    @Override
    public List<TickerEntity> getLatestTicker() {
        List<TickerEntity> tickerEntities = Lists.newArrayList();
        try {
            log.info("Fetching Latest Ticker Data For Timestamp : {}", dateFormat.format(new Date()));
            String response = httpService.get(TICKER_API);
            log.info("Parsing Ticker Data...");
            try {
                Type responseType = new TypeToken<Map<String, TickerEntity>>() {
                }.getType();
                Set<String> dedicatedTokens = providerService.fetchDedicatedTokenList();
                Map<String, TickerEntity> parsedData = gson.fromJson(response, responseType);
                parsedData.forEach((k, v) -> {
                    if (dedicatedTokens.contains(k)) {
                        tickerEntities.add(v);
                    }
                });
            } catch (Exception parseException) {
                log.error("Error While Ticker Parsing : {}", parseException.getMessage());
            }
        } catch (Exception httpException) {
            log.error("Error While Ticker Get : {}", httpException.getMessage());
        }
        return tickerEntities;
    }
}
