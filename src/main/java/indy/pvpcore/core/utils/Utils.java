package indy.pvpcore.core.utils;

import com.mojang.authlib.GameProfile;
import indy.pvpcore.core.main.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Utils {

    public static Plugin plugin() {
        return Main.getPlugin(Main.class);
    }

    public static FileConfiguration getConfig() {
        return plugin().getConfig();
    }

    public static Integer getInt(String path) {
        return getConfig().getInt(path);
    }

    public static String getString(String path) {
        return formatColor(getConfig().getString(path));
    }

    public static String getString(String path, Player player) {
        return PlaceholderAPI.setPlaceholders(player, formatColor(getConfig().getString(path)));
    }

    public static Boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    public static double getDouble(String path) {
        return getConfig().getDouble(path);
    }

    public static List<String> getList(String path) {
        return getConfig().getStringList(path);
    }

    public static List<String> getList(String path, Player player) {
        List<String> list = getConfig().getStringList(path);

        for(int i = 0; i < list.size(); i++) {
            list.set(i, PlaceholderAPI.setPlaceholders(player, list.get(i)));
        }

//        list.forEach((i) -> {
//            System.out.println(i);
//            PlaceholderAPI.setPlaceholders(player, i);
//        });

        return list;
    }

    public static String formatColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static ItemStack createItem(String material, String name, List<String> lore, int amount, int durability, String skullOwner) {
        ItemStack item = new ItemStack(Material.valueOf(material), amount, (short) durability);
        ItemMeta meta = item.getItemMeta();

        for(int i = 0; i < lore.size(); i++) {
            lore.set(i, Utils.formatColor(lore.get(i)));
        }

        if(material.equals("SKULL_ITEM")) {
            return createSkull(name, lore, amount, durability, skullOwner);
        } else {
            meta.setDisplayName(name);
            meta.setLore(lore);

            item.setItemMeta(meta);

            return item;
        }
    }

    public static ItemStack createSkull(String name, List<String> lore, int amount, int durability, String skullOwner) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, amount, (short) durability);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        for(int i = 0; i < lore.size(); i++) {
            lore.set(i, Utils.formatColor(lore.get(i)));
        }

        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.setOwner(skullOwner);

        item.setItemMeta(meta);

        return item;
    }

    public static void createItem(Inventory inventory, String material, String name, List<String> lore, int amount, int durability, int slot, String skullOwner) {
        inventory.setItem(slot, createItem(material, name, lore, amount, durability, skullOwner));
    }

    public static void sendDebugMessage(String message) {
        if(getConfig().getBoolean("DeveloperMode.enabled")) System.out.println(message);
    }
}
