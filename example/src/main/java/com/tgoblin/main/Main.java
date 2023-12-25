package com.tgoblin.main;

import com.google.gson.Gson;
import com.tgoblin.parser.AuctionatorParser;
import com.tgoblin.parser.AuctionatorPrices;
import io.github.cdimascio.dotenv.Dotenv;
import org.tinylog.Logger;

import java.io.PrintWriter;

public class Main {

    private static Dotenv DOTENV = Dotenv.configure().load();
    private static final Gson GSON = new Gson();

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        String path = dotenv.get("AUCTIONATOR_LOG_PATH");
        Logger.info("Parsing prices from {}", path);
        AuctionatorPrices parsedPrices = AuctionatorParser.parse(path);
        Logger.info("Storing prices");
        store(parsedPrices);
    }

    private static void store(AuctionatorPrices parsedPrices) {
        try {
            String outputPath = DOTENV.get("OUTPUT_PATH");
            String json = GSON.toJson(parsedPrices);
            PrintWriter out = new PrintWriter(outputPath);
            out.println(json);
            out.close();
            Logger.info("Stored {} prices to {}", parsedPrices.prices().size(), outputPath);
        } catch (Exception e) {
            Logger.error("Unable to store auctionator prices", e);
        }
    }
}