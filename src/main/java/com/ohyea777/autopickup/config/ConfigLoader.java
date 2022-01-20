package com.ohyea777.autopickup.config;

import com.ohyea777.autopickup.AutoPickup;
import com.ohyea777.libs.com.google.gson.Gson;
import com.ohyea777.libs.com.google.gson.GsonBuilder;
import com.ohyea777.libs.org.apache.commons.io.FileUtils;

import java.io.File;

public class ConfigLoader {

    private static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();

        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();

        gson = builder.create();
    }

    private static AutoPickup getPlugin() {
        return AutoPickup.getInstance();
    }

    public static AutoPickupConfig reload() {
        File file = new File(getPlugin().getDataFolder(), "config.json");

        file.getParentFile().mkdirs();

        if (!file.exists()) {
            AutoPickupConfig config = new AutoPickupConfig();

            try {
                String json = gson.toJson(config);

                FileUtils.writeStringToFile(file, json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return config;
        }

        try {
            String json = FileUtils.readFileToString(file);

            return gson.fromJson(json, AutoPickupConfig.class);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
