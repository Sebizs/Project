// ElasticsearchStorage.java

import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class ElasticsearchStorage implements StorageBackend {
    private final String endpoint;
    private final String index;
    private final int bulkSize;
    private final HttpClient http;

    private final List<String> bulkBuffer = new ArrayList<>();
    private final Object lock = new Object();

    public ElasticsearchStorage(ServerConfig.ElasticsearchConfig cfg) {
        this.endpoint = "http://" + cfg.host + ":" + cfg.port;
        this.index = cfg.index;
        this.bulkSize = cfg.bulkSize > 0 ? cfg.bulkSize : 100;
        this.http = HttpClient.newHttpClient();
    }

    @Override
    public void saveMatch(int patternId, String matchedText) {
        String actionMeta = String.format("{\"index\":{}}");
        String jsonData = String.format(
            "{\"timestamp\":\"%s\",\"matched_text\":%s,\"pattern_id\":%d}",
            Instant.now().toString(),
            escapeJson(matchedText),
            patternId
        );

        synchronized (lock) {
            bulkBuffer.add(actionMeta);
            bulkBuffer.add(jsonData);

            if (bulkBuffer.size() / 2 >= bulkSize) { // minden dokumentum 2 sorból áll (action + data)
                flushBulk();
            }
        }
    }

    private void flushBulk() {
        String body = String.join("\n", bulkBuffer) + "\n"; // bulk végén kell newline

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(endpoint + "/" + index + "/_bulk"))
            .header("Content-Type", "application/x-ndjson")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        // Aszinkron küldés
        http.sendAsync(req, HttpResponse.BodyHandlers.discarding())
            .exceptionally(e -> {
                System.err.println("Elasticsearch bulk hiba: " + e.getMessage());
                return null;
            });

        bulkBuffer.clear();
    }

    private String escapeJson(String s) {
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }

    @Override
    public void close() {
        synchronized (lock) {
            if (!bulkBuffer.isEmpty()) {
                flushBulk();
            }
        }
    }
}
