import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final int LISTEN_PORT = 5514;
    private static final int SOCKET_TIMEOUT_MS = 5000; // 5 másodperc timeout
    private static final int THREAD_POOL_SIZE = 10; // Max 10 párhuzamos kliens

    public static void main(String[] args) {
        System.out.println("Hello world!");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(LISTEN_PORT)) {
            System.out.println("Szerver figyel a porton: " + LISTEN_PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(SOCKET_TIMEOUT_MS);

                    System.out.println("Új kapcsolat: " + clientSocket.getRemoteSocketAddress());

                    executor.execute(() -> handleClient(clientSocket));

                } catch (IOException e) {
                    System.err.println("Hiba a kapcsolat elfogadásánál: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Nem lehet elindítani a szervert: " + e.getMessage());
        } finally {
            executor.shutdown(); // Leállítja a szálpoolt, ha a szerver bezár
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                // Egyszerű validálás: csak nem üres sorokat írunk ki
                if (!line.trim().isEmpty()) {
                    System.out.println("[" + clientSocket.getRemoteSocketAddress() + "] " + line);
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Kapcsolat timeout: " + clientSocket.getRemoteSocketAdd
