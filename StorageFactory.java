// StorageFactory.java

public class StorageFactory {
    public static StorageBackend create(ServerConfig config) {
        try {
            return switch (config.storageBackend.toLowerCase()) {
                case "mysql" -> new MySQLStorage(config.mysql);
                case "elasticsearch" -> new ElasticsearchStorage(config.elasticsearch);
                default -> throw new IllegalArgumentException("Ismeretlen tároló: " + config.storageBackend);
            };
        } catch (Exception e) {
            throw new RuntimeException("Tároló inicializálási hiba: " + e.getMessage());
        }
    }
}
