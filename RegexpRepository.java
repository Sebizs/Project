import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegexpRepository {
    private final Connection conn;

    public RegexpRepository(ServerConfig.MySQLConfig cfg) throws SQLException {
        String url = "jdbc:mysql://" + cfg.host + ":" + cfg.port + "/" + cfg.database + "?useSSL=false";
        this.conn = DriverManager.getConnection(url, cfg.user, cfg.password);
    }

    public List<RegexpPattern> loadPatterns() {
        List<RegexpPattern> patterns = new ArrayList<>();
        String sql = "SELECT id, name, pattern FROM regex_patterns";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patterns.add(new RegexpPattern(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("pattern")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Hiba regexp minták betöltésekor: " + e.getMessage());
        }

        return patterns;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Adatbázis kapcsolat zárási hiba: " + e.getMessage());
        }
    }
}
