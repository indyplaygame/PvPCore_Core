package indy.pvpcore.core.commands;

import indy.pvpcore.core.gui.StatsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static indy.pvpcore.core.utils.Utils.getString;

public class statsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(getString("Messages.wrong-executor"));
            return false;
        }

        ((Player) sender).openInventory(StatsGUI.getInventory((Player) sender));
        return false;
    }
}
