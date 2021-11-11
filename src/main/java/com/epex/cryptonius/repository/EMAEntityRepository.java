package com.epex.cryptonius.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EMAEntityRepository extends MongoRepository<EMAEntity, String> {
}
