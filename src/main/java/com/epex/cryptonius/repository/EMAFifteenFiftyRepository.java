package com.epex.cryptonius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EMAFifteenFiftyRepository extends JpaRepository<EMAFifteenFiftyEntity, Long> {

    @Query(value = "select * from ema_15_50_db where baseUnit = ?1 order by id desc limit 1", nativeQuery = true)
    Optional<EMAFifteenFiftyEntity> findLastEntry(String token);

}
