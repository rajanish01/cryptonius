package com.epex.cryptonius.repository;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "ticker_db")
public class TickerEntity {

    private String id;

    private String base_unit;

    private BigDecimal low;     //24 hrs lowest price of base asset
    private BigDecimal high;    //24 hrs highest price of base asset
    private BigDecimal last;    //Last traded price in current market

    private BigDecimal open;    //Market Open price 24hrs ago
    private BigDecimal volume;  //Last 24hrs traded volume

    private BigDecimal sell;    //Top ask order price
    private BigDecimal buy;     //Top bid order price

    private Long at;

}
