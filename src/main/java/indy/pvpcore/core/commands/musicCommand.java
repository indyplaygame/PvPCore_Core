package indy.pvpcore.core.commands;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import indy.pvpcore.core.events.Events;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static indy.pvpcore.core.utils.Utils.*;

public class musicCommand implements CommandExecutor {

    RadioSongPlayer song_player = Events.song_player;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(getString("Messages.wrong-executor"));
            return false;
        }

        Player player = (Player) sender;

        if(song_player.getPlayerUUIDs().contains(player.getUniqueId())) {
            player.sendMessage(getString("Messages.music-disable"));
            song_player.removePlayer(player);
        } else {
            player.sendMessage(getString("Messages.music-enable"));
            song_player.addPlayer(player);
        }

        return false;
    }
}
