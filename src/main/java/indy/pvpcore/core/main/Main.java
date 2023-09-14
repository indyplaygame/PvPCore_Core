package indy.pvpcore.core.main;

import indy.pvpcore.core.commands.*;
import indy.pvpcore.core.events.Events;
import indy.pvpcore.core.gui.ModeGUI;
import indy.pvpcore.core.gui.StatsGUI;
import indy.pvpcore.core.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

import static indy.pvpcore.core.utils.Utils.getString;

public class Main extends JavaPlugin {

    public MySQL SQL;

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI (required). Make sure that you have this plugin installed. Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        } else if(Bukkit.getPluginManager().getPlugin("NoteBlockAPI") == null) {
            getLogger().warning("Could not find NoteBlockAPI (required). Make sure that you have this plugin installed. Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            getConfig().options().copyDefaults(true);

            Events.setupMusic();

            getServer().getPluginManager().registerEvents(new Events(), this);
            getServer().getPluginManager().registerEvents(new ModeGUI(), this);
            getServer().getPluginManager().registerEvents(new StatsGUI(), this);

            getCommand("modes").setExecutor(new modesCommand());
            getCommand("stats").setExecutor(new statsCommand());
            getCommand("playershide").setExecutor(new playershideCommand());
            getCommand("playersshow").setExecutor(new playersshowCommand());
            getCommand("editkit").setExecutor(new editkitCommand());
            getCommand("spawn").setExecutor(new spawnCommand());
            getCommand("music").setExecutor(new musicCommand());

            saveConfig();
            reloadConfig();

            this.SQL = new MySQL();
            try {
                SQL.connect();
            } catch(ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                Bukkit.getLogger().info(getString("Messages.database-not-connected"));
            }

            if(SQL.isConnected()) {
                Bukkit.getLogger().info(getString("Messages.database-connected"));
                SQL.createTable();
            }
        }
    }

    @Override
    public void onDisable() {
        SQL.disconnect();
    }
}
