package indy.pvpcore.core.commands;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import indy.pvpcore.core.events.Events;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static indy.pvpcore.core.utils.Utils.getDouble;
import static indy.pvpcore.core.utils.Utils.getString;

public class spawnCommand implements CommandExecutor {

    RadioSongPlayer song_player = Events.song_player;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(getString("Messages.wrong-executor"));
            return false;
        }

        Player player = (Player) sender;

        player.teleport(new Location(
                Bukkit.getWorld(getString("Lobby.backtospawn.spawn.world")),
                getDouble("Lobby.backtospawn.spawn.x"),
                getDouble("Lobby.backtospawn.spawn.y"),
                getDouble("Lobby.backtospawn.spawn.z"))
        );

        player.sendMessage(getString("Messages.spawn-command"));

        return false;
    }
}
