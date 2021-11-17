package com.epex.cryptonius.repository;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@MappedSuperclass
public class EMAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String baseUnit;

    private BigDecimal value = BigDecimal.ZERO;

    private Long at = new Date().getTime();

    public EMAEntity(String baseUnit, BigDecimal value) {
        this.baseUnit = baseUnit;
        this.value = value;
    }

}
