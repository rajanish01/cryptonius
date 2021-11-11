package com.epex.cryptonius.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "ema_db")
public class EMAEntity {

    private String id;

    private String baseUnit;

    private BigDecimal emaFifty = BigDecimal.ZERO;
    private BigDecimal emaTwoHundred = BigDecimal.ZERO;

    private boolean emaFiftyFlag = false;
    private boolean emaTwoHundredFlag = false;

    private Long at = new Date().getTime();

    public EMAEntity(String baseUnit) {
        this.baseUnit = baseUnit;
    }

}
