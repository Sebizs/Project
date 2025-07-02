// MySQLStorage.java

import java.sql.*;

public class MySQLStorage implements StorageBackend {
    private final Connection conn;
    private final PreparedStatement insertStmt;

    public MySQLStorage(ServerConfig.MySQLConfig cfg) throws SQLException {
        String url = "jdbc:mysql://" + cfg.host + ":" + cfg.port + "/" + cfg.database + "?useSSL=false";
        conn = DriverManager.getConnection(url, cfg.user, cfg.password);
        insertStmt = conn.prepareStatement(
            "INSERT INTO log_entries (timestamp, ip, message) VALUES (NOW(), ?, ?)"
        );
    }

    @Override
    public void save(String ip, String message) {
        try {
            insertStmt.setString(1, ip);
            insertStmt.setString(2, message);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("MySQL hiba: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            insertStmt.close();
            conn.close();n        } catch (SQLException e) {
            System.err.println("MySQL zárási hiba: " + e.getMessage());
        }
    }
}
