package com.onewhohears.onewholibs.util;

import java.util.function.Consumer;

public class UtilScreen {

    public static Consumer<String> getIntResponder(Consumer<Integer> responder) {
        return string -> {
            int num;
            try {
                num = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                return;
            }
            responder.accept(num);
        };
    }

    public static Consumer<String> getDoubleResponder(Consumer<Double> responder) {
        return string -> {
            double num;
            try {
                num = Double.parseDouble(string);
            } catch (NumberFormatException e) {
                return;
            }
            responder.accept(num);
        };
    }

}
