package indy.pvpcore.core.gui;

import ga.strikepractice.StrikePractice;
import ga.strikepractice.api.StrikePracticeAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static indy.pvpcore.core.utils.Utils.*;

public class ModeGUI implements Listener {
    private static Inventory inventory = null;

    public static Map<Integer, String> modes = new HashMap<>();

    public ModeGUI() {
        inventory = Bukkit.createInventory(null, 9*getInt("ModeGUI.gui.rows"), getString("ModeGUI.gui.title"));
    }

    public static Inventory getInventory(Player player) {
        initContents(player);
        return inventory;
    }

    public static void initContents(Player player) {
        inventory.clear();

        for(String i : getList("ModeGUI.modes.list")) {
            addItem(inventory, getInt("ModeGUI.modes." + i + ".item.slot"), createItem(
                    getString("ModeGUI.modes." + i + ".item.material"),
                    getString("ModeGUI.modes." + i + ".item.name"),
                    getList("ModeGUI.modes." + i + ".item.lore"),
                    getInt("ModeGUI.modes." + i + ".item.amount"),
                    getInt("ModeGUI.modes." + i + ".item.durability"),
                    getString("ModeGUI.modes." + i + ".item.skullOwner").replace("%player%", player.getName())
            ));

            modes.put(getInt("ModeGUI.modes." + i + ".item.slot"), getString("ModeGUI.modes." + i + ".command"));
        }

        if(getBoolean("ModeGUI.gui.items.empty-slots.enabled")) {
            for(int i = 0; i < inventory.getSize(); i++) {
                if(inventory.getItem(i) == null) {
                    createItem(inventory, getString("ModeGUI.gui.items.empty-slots.material"),
                            getString("ModeGUI.gui.items.empty-slots.name"),
                            getList("ModeGUI.gui.items.empty-slots.lore"),
                            getInt("ModeGUI.gui.items.empty-slots.amount"),
                            getInt("ModeGUI.gui.items.empty-slots.durability"), i,
                            getString("ModeGUI.gui.items.empty-slots.skullOwner").replace("%player%", player.getName())
                    );
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

        if(modes.containsKey(slot)) {
            player.chat(modes.get(slot));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) e.setCancelled(true);
    }
}
