package indy.pvpcore.core.mysql;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

import static indy.pvpcore.core.utils.Utils.getConfig;
import static indy.pvpcore.core.utils.Utils.getString;

public class MySQL {
    private final String host = getString("Database.host");
    private final String port = getString("Database.port");
    private final String database = getString("Database.database");
    private final String user = getString("Database.user");
    private final String password = getConfig().getString("Database.password");
    private final String prefix = getString("Database.prefix");

    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if(!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", user, password);
        }
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                connection.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable() {
        try {
            PreparedStatement settingsTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix + "settings " +
                    "(ID INT AUTO_INCREMENT, NAME VARCHAR(20), UUID VARCHAR(150), MUSIC_ENABLED BOOLEAN, PRIMARY KEY (ID));");

            settingsTable.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerJoin(Player player) {
        try {
            String name = player.getName();
            UUID uuid = player.getUniqueId();

            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO " + prefix + "settings (NAME, UUID, MUSIC_ENABLED) VALUES " +
                    "('" + name + "', '" + uuid + "', " + true + ")");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getSettings(Player player) {
        try {
            UUID uuid = player.getUniqueId();

            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "settings WHERE uuid = '" + uuid + "'");
            return results;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void toggleMusic(Player player, boolean music_enabled) {
        try {
            UUID uuid = player.getUniqueId();

            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE " + prefix + "settings SET music_enabled = " + music_enabled + " WHERE uuid = '" + uuid + "'");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
