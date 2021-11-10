package com.epex.cryptonius.util;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Service
public class ProviderService {

    @Value("${tokens}")
    private String tokens;

    public Set<String> fetchDedicatedTokenList() {
        return Sets.newHashSet(Arrays.asList(tokens.split("&")));
    }

}
