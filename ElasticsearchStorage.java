// ElasticsearchStorage.java

import java.net.URI;
import java.net.http.*;
import java.time.Instant;

public class ElasticsearchStorage implements StorageBackend {
    private final String endpoint;
    private final String index;
    private final HttpClient http;

    public ElasticsearchStorage(ServerConfig.ElasticsearchConfig cfg) {
        this.endpoint = "http://" + cfg.host + ":" + cfg.port;
        this.index = cfg.index;
        this.http = HttpClient.newHttpClient();
    }

    @Override
    public void saveMatch(int patternId, String matchedText) {
        try {
            String json = String.format(
                "{ \"timestamp\": \"%s\", \"matched_text\": %s, \"pattern_id\": %d }",
                Instant.now().toString(),
                escapeJson(matchedText),
                patternId
            );

            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/" + index + "/_doc"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            http.sendAsync(req, HttpResponse.BodyHandlers.discarding());

        } catch (Exception e) {
            System.err.println("Elasticsearch mentési hiba: " + e.getMessage());
        }
    }

    private String escapeJson(String s) {
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }

    @Override
    public void close() {
        // Nincs zárandó erőforrás
    }
}
