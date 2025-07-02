// ConfigLoader.java

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;

public class ConfigLoader {
    public static ServerConfig load(String path) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(path)));
            return new Gson().fromJson(json, ServerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Nem sikerült betölteni a konfigurációt: " + e.getMessage());
        }
    }
}
