package com.epex.cryptonius.repository;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class EMAEntity {

    private String baseUnit;

    private BigDecimal value = BigDecimal.ZERO;

    private Long at = new Date().getTime();

    public EMAEntity(String baseUnit, BigDecimal value) {
        this.baseUnit = baseUnit;
        this.value = value;
    }

}
