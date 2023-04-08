package com.tastik.cycal.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Flag {
    private static final Map<String, String> dictionary = loadFlags();

    private static Map<String, String> loadFlags() {
        final var flagsMap = new HashMap<String, String>();
        flagsMap.put("ALG", "🇩🇿");
        flagsMap.put("AUS", "🇦🇺");
        flagsMap.put("BEL", "🇧🇪");
        flagsMap.put("CHI", "🇨🇱");
        flagsMap.put("CRO", "🇭🇷");
        flagsMap.put("ESP", "🇪🇸");
        flagsMap.put("FRA", "🇫🇷");
        flagsMap.put("GBR", "🇬🇧");
        flagsMap.put("GER", "🇩🇪");
        flagsMap.put("GRE", "🇬🇷");
        flagsMap.put("GUA", "🇬🇹");
        flagsMap.put("ITA", "🇮🇹");
        flagsMap.put("MAS", "🇲🇾");
        flagsMap.put("MLI", "🇲🇱");
        flagsMap.put("NED", "🇳🇱");
        flagsMap.put("PAN", "🇵🇦");
        flagsMap.put("POR", "🇵🇹");
        flagsMap.put("SLO", "🇸🇮");
        flagsMap.put("SUI", "🇨🇭");
        flagsMap.put("THA", "🇹🇭");
        flagsMap.put("TPE", "🇹🇼");
        flagsMap.put("TUR", "🇹🇷");
        flagsMap.put("UAE", "🇦🇪");
        flagsMap.put("URU", "🇺🇾");
        return flagsMap;
    }

    public static String get(String id) {
        return Optional.ofNullable(dictionary.get(id)).orElse(DEFAULT);
    }

    private static final String DEFAULT = "🏳";
//    private static final String RED_DOT = "%F0%9F%94%B4";
}
