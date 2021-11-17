package com.epex.cryptonius.enums;

import java.util.Arrays;

public enum EMAScale {

    FIFTEEN_FIFTY(15, 50), FIFTEEN_TWO_HUNDRED(15, 200);

    EMAScale(int range, int width) {
        this.width = width;
        this.range = range;
    }

    private final int width;
    private final int range;

    public int getWidth() {
        return width;
    }

    public int getRange() {
        return range;
    }

    public static EMAScale findScaleForWidth(int range, int width) throws Exception {
        return Arrays
                .stream(EMAScale.values())
                .filter(x -> x.getWidth() == width && x.getRange() == range)
                .findFirst()
                .orElseThrow(() -> new Exception("EMA Scale For Width " + width
                        + " And Range " + range + " Not Present !"));
    }
}
