package indy.pvpcore.core.commands;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import indy.pvpcore.core.events.Events;
import indy.pvpcore.core.mysql.MySQL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static indy.pvpcore.core.utils.Utils.*;

public class musicCommand implements CommandExecutor {

    RadioSongPlayer song_player = Events.song_player;

    MySQL SQL;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(getString("Messages.wrong-executor"));
            return false;
        }

        this.SQL = new MySQL();
        Player player = (Player) sender;

        try {
            SQL.connect();

            if (song_player.getPlayerUUIDs().contains(player.getUniqueId())) {
                player.sendMessage(getString("Messages.music-disable"));
                song_player.removePlayer(player);
                SQL.toggleMusic(player, false);
            } else {
                player.sendMessage(getString("Messages.music-enable"));
                song_player.addPlayer(player);
                SQL.toggleMusic(player, true);
            }
        } catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
