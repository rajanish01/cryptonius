package com.epex.cryptonius.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ema_15_50_db")
@EqualsAndHashCode(callSuper = true)
public class EMAFifteenFiftyEntity extends EMAEntity {

    public EMAFifteenFiftyEntity(String baseUnit, BigDecimal value) {
        super(baseUnit, value);
    }
}
