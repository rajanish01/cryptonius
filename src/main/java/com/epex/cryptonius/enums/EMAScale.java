package com.epex.cryptonius.enums;

import java.util.Arrays;

public enum EMAScale {

    FIFTY(50), TWO_HUNDRED(200);

    EMAScale(int width) {
        this.width = width;
    }

    private final int width;

    public int getWidth() {
        return width;
    }

    public static EMAScale findScaleForWidth(int width) throws Exception {
        return Arrays
                .stream(EMAScale.values())
                .filter(x -> x.getWidth() == width)
                .findFirst()
                .orElseThrow(() -> new Exception("EMA Scale For Width " + width + " Not Present !"));
    }
}
