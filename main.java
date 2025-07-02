// Main.java
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import javax.net.ssl.*;
import java.security.*;
import com.google.gson.*;

public class Main {
    public static void main(String[] args) {
        ServerConfig config = ConfigLoader.load("config.json");
        ExecutorService executor = Executors.newFixedThreadPool(config.threadPoolSize);

        try (ServerSocket serverSocket = ServerInitializer.createServerSocket(config)) {
            System.out.println("Szerver fut: " + config.listenPort + (config.sslEnabled ? " (SSL)" : ""));

            StorageBackend storage = StorageFactory.create(config);
            Forwarder forwarder = ForwarderFactory.create(config);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(config.socketTimeoutMs);
                    executor.execute(() -> handleClient(clientSocket, storage, forwarder));
                } catch (IOException e) {
                    System.err.println("Kapcsolódási hiba: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Szerverindítási hiba: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private static void handleClient(Socket socket, StorageBackend storage, Forwarder forwarder) {
        String ip = socket.getRemoteSocketAddress().toString();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    System.out.println("[" + ip + "] " + line);
                    storage.save(ip, line);
                    if (forwarder != null) forwarder.forward(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Olvasási hiba: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
