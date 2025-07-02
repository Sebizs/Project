// ForwarderFactory.java

public class ForwarderFactory {
    public static Forwarder create(ServerConfig config) {
        if (config.forwarding != null && config.forwarding.enabled) {
            try {
                return new TcpForwarder(config.forwarding);
            } catch (Exception e) {
                System.err.println("Nem sikerült csatlakozni a forwarderhez: " + e.getMessage());
            }
        }
        return null;
    }
}
