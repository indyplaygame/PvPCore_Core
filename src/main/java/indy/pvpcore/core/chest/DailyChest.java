package indy.pvpcore.core.chest;

import indy.pvpcore.core.mysql.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static indy.pvpcore.core.utils.Utils.getBoolean;
import static indy.pvpcore.core.utils.Utils.getString;

public class DailyChest implements Listener {

    MySQL SQL;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        this.SQL = new MySQL();
        Date date = new Date(System.currentTimeMillis());
        Player player = e.getPlayer();

        if(!getBoolean("DailyReward.reminder")) return;

        try {
            SQL.connect();

            ResultSet rewards = SQL.getRewards(player, date);
            rewards.next();

            if(rewards.getInt(1) == 0) player.sendMessage(getString("DailyReward.reminder-message"));
        } catch(ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }
}
