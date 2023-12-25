package com.tgoblin.parser;

import java.util.Map;

public record AuctionatorPrices(String serverName, String factionName, long scannedTime, Map<Integer, Integer> prices) {
}
