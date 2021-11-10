package com.epex.cryptonius.rest.service;

import com.epex.cryptonius.repository.TickerEntity;
import com.epex.cryptonius.repository.TickerEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TickerService {

    private static final Logger log = LoggerFactory.getLogger(TickerService.class);

    private final TickerEntityRepository tickerRepository;

    @Autowired
    public TickerService(final TickerEntityRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    @Transactional
    public boolean persist(TickerEntity tickerEntity) {
        try {
            TickerEntity persistedEntity = tickerRepository.save(tickerEntity);
            log.info("Ticker Persisted Successfully ! Ref Id : {}", persistedEntity.getId());
            return true;
        } catch (Exception persistError) {
            log.error("Error While Persisting Ticker : {}", persistError.getMessage());
        }
        return false;
    }

    @Transactional
    public boolean persist(List<TickerEntity> tickerEntities) {
        try {
            tickerRepository.saveAll(tickerEntities);
            log.info("Tickers Persisted Successfully !");
            return true;
        } catch (Exception persistError) {
            log.error("Error While Persisting Tickers : {}", persistError.getMessage());
        }
        return false;
    }

}
