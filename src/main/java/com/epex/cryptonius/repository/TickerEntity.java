package com.epex.cryptonius.repository;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ticker_db")
public class TickerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String base_unit;

    private BigDecimal last;    //Last traded price in current market

    private BigDecimal open;    //Market Open price 24hrs ago

    private BigDecimal sell;    //Top ask order price
    private BigDecimal buy;     //Top bid order price

    private Long at;

}
