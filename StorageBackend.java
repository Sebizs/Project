// StorageBackend.java

public interface StorageBackend {
    void save(String ip, String message);
    void close();
}
