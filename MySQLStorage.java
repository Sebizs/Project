// MySQLStorage.java

import java.sql.*;

public class MySQLStorage implements StorageBackend {
    private final Connection conn;
    private final PreparedStatement insertStmt;

    public MySQLStorage(ServerConfig.MySQLConfig cfg) throws SQLException {
        String url = "jdbc:mysql://" + cfg.host + ":" + cfg.port + "/" + cfg.database + "?useSSL=false";
        conn = DriverManager.getConnection(url, cfg.user, cfg.password);
        insertStmt = conn.prepareStatement(
            "INSERT INTO regex_matches (timestamp, matched_text, pattern_id) VALUES (NOW(), ?, ?)"
        );
    }

    @Override
    public void saveMatch(int patternId, String matchedText) {
        try {
            insertStmt.setString(1, matchedText);
            insertStmt.setInt(2, patternId);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("MySQL mentési hiba: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            insertStmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("MySQL zárási hiba: " + e.getMessage());
        }
    }
}
