// ServerConfig.java

public class ServerConfig {
    public int listenPort;
    public boolean sslEnabled;
    public String sslKeystorePath;
    public String sslKeystorePassword;
    public int socketTimeoutMs;
    public int threadPoolSize;
    public String storageBackend;
    public MySQLConfig mysql;
    public ElasticsearchConfig elasticsearch;
    public ForwardingConfig forwarding;

    public static class MySQLConfig {
        public String host;
        public int port;
        public String database;
        public String user;
        public String password;
    }

    public static class ElasticsearchConfig {
        public String host;
        public int port;
        public String index;
        public int bulkSize = 100;
    }

    public static class ForwardingConfig {
        public boolean enabled;
        public String host;
        public int port;
        public boolean useTls;
    }
} 
