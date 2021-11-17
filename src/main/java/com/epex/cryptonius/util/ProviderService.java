package com.epex.cryptonius.util;

import com.epex.cryptonius.repository.TickerEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private static final String CURRENCY_CODE = "inr";

    @Value("${tokens}")
    private String tokens;

    public Set<String> fetchDedicatedTokenList() {
        return Arrays
                .stream(tokens.split("&"))
                .map(token -> token.concat(CURRENCY_CODE))
                .collect(Collectors.toSet());
    }

    public TickerEntity filterTickerForTokenFromResponse(List<TickerEntity> tickers, String token) throws Exception {
        return tickers
                .stream()
                .filter(x -> x.getBase_unit().equals(token)).findFirst()
                .orElseThrow(() -> new Exception("No Ticker Found For Token " + token));
    }

}
