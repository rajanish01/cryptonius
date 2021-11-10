package com.epex.cryptonius.domain;

import com.epex.cryptonius.repository.TickerEntity;
import lombok.Data;

@Data
public class TickerResponse {

    private String key;

    private TickerEntity data;

}
