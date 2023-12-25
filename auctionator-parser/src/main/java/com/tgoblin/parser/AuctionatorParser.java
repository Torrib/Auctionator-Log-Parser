package com.tgoblin.parser;

import org.apache.commons.lang3.StringUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class AuctionatorParser {

    private final List<String> dateFormatsToSkip = Arrays.asList("[cc]", "[sc]", "[id]");

    //Auctionator's date storage is based on days from this date for some reason
    private final LocalDate startDate = LocalDate.of(2010, 11, 15);

    public static AuctionatorPrices parse(String logPath) {
        String file = readFile(logPath);

        long scannedTime = getScannedTime(file);

        Map<Integer, Integer> items = parsePrices(file);

        Map<Integer, Integer> history = parseHistory(file);

        for (Map.Entry<Integer, Integer> entry : history.entrySet()) {
            if (!items.containsKey(entry.getKey())) {
                items.put(entry.getKey(), entry.getValue());
            }
        }

        //TODO fix
        String server = "Server";
        String faction = "Faction";

        return new AuctionatorPrices(server, faction, scannedTime, items);
    }

    private static Map<Integer, Integer> parsePrices(String file) {
        int startIndex = file.indexOf("AUCTIONATOR_PRICE_DATABASE");
        int endIndex = file.indexOf("AUCTIONATOR_POSTING_HISTORY");
        String data = file.substring(startIndex, endIndex);
        data = data.replaceAll("\"", "");
        data = removeLines(data, 2);
        data = data.replace("[__dbversion] = 6,", "");

        String server = getServer(data);
        String faction = getFaction(data);

        data = data.replace("[" + server + " " + faction + "] = {", "").trim();
        data = data.replaceAll("\\s", "");

        String[] split = data.split("},},");

        Map<Integer, Integer> items = new HashMap<>();

        for (int i = 0; i < split.length - 2; i++) {

            String newData = split[i];
            int id = getItemId(newData);
            int price = getPrice(newData);
            items.put(id, price);
        }

        return items;
    }

    private static Map<Integer, Integer> parseHistory(String file) {
        int startIndex = file.indexOf("AUCTIONATOR_POSTING_HISTORY");
        int endIndex = file.indexOf("AUCTIONATOR_VENDOR_PRICE_CACHE");
        String data = file.substring(startIndex, endIndex);
        data = data.replaceAll("\"", "");
        data = removeLines(data, 1);
        data = data.replaceAll("-- \\[[0-9]+\\]", "");

        data = data.replaceAll("\\s", "");

        String[] split = data.split("},},");

        Map<Integer, Integer> items = new HashMap<>();

        for (int i = 0; i < split.length - 2; i++) {

            String newData = split[i];
            if (newData.contains("__dbversion")) {
                continue;
            }
            String[] prices = StringUtils.substringsBetween(newData, "[price]=", ",[quantity]=");
            int price = Integer.parseInt(prices[prices.length - 1]);
            int id = getItemId(newData);
            items.put(id, price);
        }

        return items;
    }

    private static long getScannedTime(String data) {
        String lastScanString = "[\"TimeOfLastGetAllScan\"] = ";
        int lastScanIndex = data.indexOf(lastScanString) + lastScanString.length();
        return Long.parseLong(data.substring(lastScanIndex, lastScanIndex + 10));
    }

    private static String getServer(String data) {
        return getServerFactionString(data).split(" ")[0];
    }

    private static String getFaction(String data) {
        return getServerFactionString(data).split(" ")[1];
    }

    private static String getServerFactionString(String data) {
        return data.substring(data.indexOf("[") + 1, data.indexOf("]"));
    }

    private static int getItemId(String data) {
        if (data.contains(":")) {
            return Integer.parseInt(data.split(":")[1]);
        }
        return Integer.parseInt(data.substring(data.indexOf("[") + 1, data.indexOf("]")));
    }

    private static int getPrice(String data) {
        return Integer.parseInt(StringUtils.substringBetween(data, "[m]=", ","));
    }

    private static String removeLines(String data, int lines) {

        String[] split = data.split("\n");

        for (int i = 0; i < lines; i++) {
            data = data.replace(split[i], "");
        }
        return data;
    }

    private static String readFile(String logPath) {
        try {
            Path path = Paths.get(logPath);
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            Logger.error("Unable to read log file", e);
            throw new RuntimeException("Unable to read log file", e);
        }
    }

    private LocalDateTime getScannedDate(int days) {
        return startDate.plusDays(days).atTime(0, 0);
    }

    private String printGoldValue(int value) {
        int gold = value / 10000;
        value = value - (gold * 10000);

        int silver = value / 100;

        int copper = value - (silver * 100);

        String string = "";

        if (gold >= 1) {
            string += gold + "g";
        }
        if (silver > 0 || (gold > 0 && copper > 0)) {
            string += silver + "s";
        }
        if (copper > 0) {
            string += copper + "c";
        }

        return string;
    }

    private Date getStartDate() {
        try {
            String string = "15/11/2010";
            return new SimpleDateFormat("dd/MM/yyyy").parse(string);
        } catch (ParseException e) {
            Logger.error("Unable to parse date", e);
            throw new RuntimeException("Unable to parse date", e);
        }
    }

}
