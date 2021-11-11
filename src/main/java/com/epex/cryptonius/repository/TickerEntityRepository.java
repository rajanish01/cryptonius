package com.epex.cryptonius.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TickerEntityRepository extends MongoRepository<TickerEntity, String> {

    @Query(value = "{'base_unit': ?0}",sort = "{'at':-1}")
    List<TickerEntity> findByBaseUnit(String token, int limit);

}