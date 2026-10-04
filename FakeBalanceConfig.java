package com.fakebalance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Simple JSON config stored at .minecraft/config/fakebalance.json */
public class FakeBalanceConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("fakebalance.json");

    // What the number on the balance line should be shown as.
    public String displayedBalance = "16B";
    // Regex matching the label in front of the number on your scoreboard line.
    public String labelRegex = "money|balance|bal|cash|coins";
    public boolean enabled = true;

    private transient Pattern compiled;

    public static FakeBalanceConfig INSTANCE = new FakeBalanceConfig();

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                FakeBalanceConfig loaded = GSON.fromJson(Files.readString(FILE), FakeBalanceConfig.class);
                if (loaded != null) INSTANCE = loaded;
            }
        } catch (Exception e) {
            FakeBalanceMod.LOGGER.warn("Could not read config, using defaults", e);
        }
        save();
    }

    public static void save() {
        try {
            Files.writeString(FILE, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            FakeBalanceMod.LOGGER.warn("Could not save config", e);
        }
    }

    /** Pattern: label, separator (symbols/spaces/$), then the number like 1,234.5 or 12.3K. */
    public Pattern pattern() {
        if (compiled == null) {
            try {
                compiled = Pattern.compile("(?i)(" + labelRegex + ")(\\W{0,8}?)(\\d[\\d,.]*\\s?[KMBT]?)");
            } catch (PatternSyntaxException e) {
                compiled = Pattern.compile("(?!)"); // matches nothing
            }
        }
        return compiled;
    }

    public void invalidate() {
        compiled = null;
    }
}
