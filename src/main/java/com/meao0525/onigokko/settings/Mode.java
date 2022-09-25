package com.meao0525.onigokko.settings;

public enum  Mode {
    ONIGOKKO("鬼ごっこ"),
    KEIDORO("ケイドロ"),
    FUEONI("増え鬼"),
    KOORIONI("氷り鬼");


    private String name;

    private Mode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
