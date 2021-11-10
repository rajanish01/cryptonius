package com.epex.cryptonius.repository;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ticker_db")
public class TickerEntity {

    private String id;


    private String name;

    private String base_unit;
    private String quote_unit;

    private Double low;     //24 hrs lowest price of base asset
    private Double high;    //24 hrs highest price of base asset
    private Double last;    //Last traded price in current market

    private String type;

    private Double open;    //Market Open price 24hrs ago
    private Double volume;  //Last 24hrs traded volume

    private Double sell;    //Top ask order price
    private Double buy;     //Top bid order price

    private Long at;

}
