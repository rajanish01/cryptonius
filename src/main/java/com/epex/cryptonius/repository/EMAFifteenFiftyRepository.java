package com.epex.cryptonius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EMAFifteenFiftyRepository extends JpaRepository<EMAFifteenFiftyEntity, Long> {

    Optional<EMAFifteenFiftyEntity> findTopByOrderByIdDescAndBaseUnit(String token);

}
