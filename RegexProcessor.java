import java.io.*;
import java.net.*;

public class RegexProcessor {

    private final RegexpMatcher matcher;
    private final StorageBackend storage;
    private final Forwarder forwarder;

    public RegexProcessor(RegexpMatcher matcher, StorageBackend storage, Forwarder forwarder) {
        this.matcher = matcher;
        this.storage = storage;
        this.forwarder = forwarder;
    }

    public void handleClient(Socket clientSocket) {
        String ip = clientSocket.getRemoteSocketAddress().toString();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    System.out.println("[" + ip + "] " + line);
                    RegexpMatcher.MatchedResult result = matcher.matchBest(line);
                    if (result != null) {
                        storage.saveMatch(result.patternId, result.matchedText);
                    }
                    if (forwarder != null) {
                        forwarder.forward(line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Olvasási hiba: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }
}
