package com.epex.cryptonius.prototype;

import com.epex.cryptonius.repository.TickerEntity;

import java.util.List;

public interface APIFactory {

    public List<TickerEntity> getLatestTicker();

}
