package com.epex.cryptonius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TickerEntityRepository extends JpaRepository<TickerEntity, Long> {

    @Query(value = "select * from ticker_db tdb where tdb.base_unit = ?1 order by id desc limit ?2", nativeQuery = true)
    List<TickerEntity> fetchDataSetForEMA(String token, int count);

}