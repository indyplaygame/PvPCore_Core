package indy.pvpcore.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static indy.pvpcore.core.utils.Utils.*;

public class StatsGUI implements Listener {
    private static Inventory inventory = null;

    public StatsGUI() {
        inventory = Bukkit.createInventory(null, 9*getInt("Stats.gui.rows"), getString("Stats.gui.title"));
    }

    public static Inventory getInventory(Player player) {
        initContents(player);
        return inventory;
    }

    public static void initContents(Player player) {
        inventory.clear();

        for(String i : getList("Stats.gui.stats.list")) {
            addItem(inventory, getInt("Stats.gui.stats.items." + i + ".slot"), createItem(
                    getString("Stats.gui.stats.items." + i + ".material"),
                    getString("Stats.gui.stats.items." + i + ".name", player),
                    getList("Stats.gui.stats.items." + i + ".lore", player),
                    getInt("Stats.gui.stats.items." + i + ".amount"),
                    getInt("Stats.gui.stats.items." + i + ".durability"),
                    getString("Stats.gui.stats.items." + i + ".skullOwner").replace("%player%", player.getName())
            ));
        }

        if(getBoolean("Stats.gui.items.empty-slots.enabled")) {
            for(int i = 0; i < inventory.getSize(); i++) {
                if(inventory.getItem(i) == null) {
                    createItem(inventory, getString("Stats.gui.items.empty-slots.material"),
                            getString("Stats.gui.items.empty-slots.name", player),
                            getList("Stats.gui.items.empty-slots.lore", player),
                            getInt("Stats.gui.items.empty-slots.amount"),
                            getInt("Stats.gui.items.empty-slots.durability"), i,
                            getString("Stats.gui.items.empty-slots.skullOwner").replace("%player%", player.getName()));
                }
            }
        }
    }

    public static void addItem(Inventory inv, int slot, ItemStack item) {
        inv.setItem(slot, item);
    }

    public static void addItem(Inventory inv, ItemStack item) {
        inv.addItem(item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getClickedInventory().equals(inventory))) return;
        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if(clickedItem == null) return;

        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) e.setCancelled(true);
    }
}
