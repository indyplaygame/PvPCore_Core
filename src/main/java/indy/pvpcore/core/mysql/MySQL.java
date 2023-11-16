package indy.pvpcore.core.mysql;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.Date;

import static indy.pvpcore.core.utils.Utils.*;

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

    public void createTables() {
        try {
            PreparedStatement settingsTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix + "settings " +
                    "(ID INT AUTO_INCREMENT, NAME VARCHAR(20), UUID VARCHAR(150), MUSIC_ENABLED BOOLEAN, STREAK INT, PRIMARY KEY (ID));");
            PreparedStatement joinsTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + prefix + "rewards " +
                    "(ID INT AUTO_INCREMENT, NAME VARCHAR(20), UUID VARCHAR(150), DATE DATE, PRIMARY KEY (ID));");

            settingsTable.executeUpdate();
            joinsTable.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerJoin(Player player) {
        try {
            String name = player.getName();
            UUID uuid = player.getUniqueId();

            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO " + prefix + "settings (NAME, UUID, MUSIC_ENABLED, STREAK) VALUES " +
                    "('" + name + "', '" + uuid + "', " + true + ", 0)");
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

    public void setStreak(Player player, int streak) {
        try {
            UUID uuid = player.getUniqueId();

            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE " + prefix + "settings SET streak = " + streak + " WHERE uuid = '" + uuid + "'");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getStreak(Player player) {
        try {
            UUID uuid = player.getUniqueId();

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT streak FROM " + prefix + "settings WHERE uuid = '" + uuid + "';");

            return result;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getRewards(Player player, Date date) {
        UUID uuid = player.getUniqueId();

        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + prefix + "rewards WHERE uuid = '" + uuid + "' AND date = '" + formatDate(date) + "';");

            return result;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registerReward(Player player, Date date) {
        String name = player.getName();
        UUID uuid = player.getUniqueId();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO " + prefix + "rewards (NAME, UUID, DATE) VALUES" +
                    "('" + name + "', '" + uuid + "', '" + formatDate(date) +"');");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
