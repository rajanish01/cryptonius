package com.epex.cryptonius.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandleStick {

    private int range;

    private BigDecimal open;
    private BigDecimal close;

    private Long openTimestamp;
    private Long closeTimeStamp;

    public CandleStick(int range) {
        this.range = range;
    }

}
