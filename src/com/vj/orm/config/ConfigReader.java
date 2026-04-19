package com.vj.orm.config;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;

public class ConfigReader {
    public static DBConfig readConfig(String filePath) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, DBConfig.class);
        }
    }
}
