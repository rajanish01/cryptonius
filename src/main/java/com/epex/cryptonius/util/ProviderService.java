package com.epex.cryptonius.util;

import com.epex.cryptonius.repository.TickerEntity;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class ProviderService {

    @Value("${tokens}")
    private String tokens;

    public Set<String> fetchDedicatedTokenList() {
        return Sets.newHashSet(Arrays.asList(tokens.split("&")));
    }

    public TickerEntity filterTickerForTokenFromResponse(List<TickerEntity> tickers, String token) throws Exception {
        return tickers
                .stream()
                .filter(x -> x.getBase_unit().equals(token)).findFirst()
                .orElseThrow(() -> new Exception("No Ticker Found For Token " + token));
    }

}
