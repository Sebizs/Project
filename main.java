import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;
import javax.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;
import com.google.gson.*;

public class Main {

    public static void main(String[] args) {
        ServerConfig config = loadConfig("config.json");
        ExecutorService executor = Executors.newFixedThreadPool(config.threadPoolSize);

        try (ServerSocket serverSocket = createServerSocket(config)) {
            System.out.println("Szerver figyel a porton: " + config.listenPort + (config.sslEnabled ? " (SSL)" : ""));

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(config.socketTimeoutMs);

                    System.out.println("Új kapcsolat: " + clientSocket.getRemoteSocketAddress());

                    executor.execute(() -> handleClient(clientSocket));

                } catch (IOException e) {
                    System.err.println("Hiba a kapcsolat elfogadásánál: " + e.getMessage());
                }
            }

        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Nem lehet elindítani a szervert: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    System.out.println("[" + clientSocket.getRemoteSocketAddress() + "] " + line);
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Kapcsolat timeout: " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("Hiba az olvasás során: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Kapcsolat lezárva: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.err.println("Hiba a socket lezárásakor: " + e.getMessage());
            }
        }
    }

    private static ServerConfig loadConfig(String path) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(path)));
            return new Gson().fromJson(json, ServerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Nem sikerült beolvasni a konfigurációt: " + e.getMessage());
        }
    }

    private static ServerSocket createServerSocket(ServerConfig config) throws IOException, GeneralSecurityException {
        if (!config.sslEnabled) {
            return new ServerSocket(config.listenPort);
        }

        // SSL inicializálás
        char[] passphrase = config.sslKeystorePassword.toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(config.sslKeystorePath), passphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) ssf.createServerSocket(config.listenPort);
        return sslServerSocket;
    }

    static class ServerConfig {
        int listenPort;
        boolean sslEnabled;
        String sslKeystorePath;
        String sslKeystorePassword;
        int socketTimeoutMs;
        int threadPoolSize;
    }
}
