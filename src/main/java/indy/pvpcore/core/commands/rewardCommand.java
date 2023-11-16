package indy.pvpcore.core.commands;

import indy.pvpcore.core.mysql.MySQL;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static indy.pvpcore.core.utils.Utils.*;

public class rewardCommand implements CommandExecutor {

    MySQL SQL;

    private static Map<Integer, String> rewards = new HashMap<>();

    public static void getRewards() {
        for(String reward : getList("DailyReward.rewards.list")) {
            rewards.put(getInt("DailyReward.rewards.rewards." + reward + ".streak"), reward);
        }
    }

    private static void grantReward(Player player, String reward) {
        if(getString("DailyReward.rewards.rewards." + reward + ".reward.type").equalsIgnoreCase("ITEM")) {
            player.getInventory().addItem(new ItemStack(
                    Material.valueOf(getString("DailyReward.rewards.rewards." + reward + ".reward.material")),
                    getInt("DailyReward.rewards.rewards." + reward + ".reward.amount"))
            );
        } else if(getString("DailyReward.rewards.rewards." + reward + ".reward.type").equalsIgnoreCase("COMMAND")) {
            plugin().getServer().dispatchCommand(
                    plugin().getServer().getConsoleSender(),
                    getString("DailyReward.rewards.rewards." + reward + ".reward.command").replace("%player%", player.getName())
            );
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(getString("Messages.wrong-executor"));
            return false;
        }

        this.SQL = new MySQL();
        Player player = (Player) sender;
        Date date = new Date(System.currentTimeMillis());
        Date prev_date = new Date(System.currentTimeMillis() - 86400000);
        String reward;

        try {
            SQL.connect();

            ResultSet day_ago = SQL.getRewards(player, date);
            day_ago.next();

            if(day_ago.getInt(1) > 0) {
                player.sendMessage(getString("DailyReward.reward-claimed-message"));
                return false;
            }

            day_ago = SQL.getRewards(player, prev_date);
            day_ago.next();

            if(day_ago.getInt(1) == 0) {
                reward = rewards.get(0);

                grantReward(player, reward);
                SQL.setStreak(player, 1);
            } else {
                ResultSet streak_set = SQL.getStreak(player);
                streak_set.next();

                int streak = streak_set.getInt(1);
                reward = rewards.get(streak);

                grantReward(player, reward);
                if(rewards.containsKey(streak + 1)) SQL.setStreak(player, streak + 1);
                else SQL.setStreak(player, 0);
            }

            SQL.registerReward(player, date);

            ResultSet streak_set = SQL.getStreak(player);
            streak_set.next();

            int streak = streak_set.getInt(1);

            player.sendMessage(getString("DailyReward.claim-message").replace("%streak%", String.valueOf(streak)));
        } catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
