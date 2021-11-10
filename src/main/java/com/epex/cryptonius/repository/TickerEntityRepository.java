package com.epex.cryptonius.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TickerEntityRepository extends MongoRepository<TickerEntity, String> {
}
