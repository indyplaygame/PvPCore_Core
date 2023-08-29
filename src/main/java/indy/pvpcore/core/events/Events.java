package indy.pvpcore.core.events;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import indy.pvpcore.core.gui.ModeGUI;
import indy.pvpcore.core.gui.StatsGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static indy.pvpcore.core.utils.Utils.*;

public class Events implements Listener {

    private static List<Player> editingKit = new ArrayList<>();
    private static Map<Player, Long> cooldowns = new HashMap<>();
    public static List<Player> hiddenPlayers = new ArrayList<>();
    public static RadioSongPlayer song_player;

    public static void setupMusic() {
        List<String> song_list = getList("Lobby.music.songs");
        Playlist playlist = new Playlist(NBSDecoder.parse(new File(song_list.get(0))));

        song_list.remove(0);

        if(!song_list.isEmpty()) {
            for (String path : song_list) {
                Song song = NBSDecoder.parse(new File(path));
                playlist.add(song);
            }
        }

        song_player = new RadioSongPlayer(playlist);
        song_player.setRepeatMode(RepeatMode.ALL);
        song_player.setRandom(getBoolean("Lobby.music.random-order"));
        song_player.setPlaying(true);
        song_player.setVolume(getInt("Lobby.music.volume").byteValue());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        initItems(player);

        for(String message : getList("TutorialMessage.message")) {
            player.sendMessage(formatColor(message));
        }

        if(!song_player.getPlayerUUIDs().contains(player.getUniqueId())) {
            song_player.addPlayer(player);
        }

        for(Player p : hiddenPlayers) {
            p.hidePlayer(player);
        }

        e.getPlayer().teleport(new Location(
                Bukkit.getWorld(getString("Lobby.backtospawn.spawn.world")),
                getDouble("Lobby.backtospawn.spawn.x"),
                getDouble("Lobby.backtospawn.spawn.y"),
                getDouble("Lobby.backtospawn.spawn.z"))
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if(!editingKit.contains(player)) return;

        player.chat(getString("Kits.player-leave-command"));

        editingKit.remove(player);
    }

    public static void initItems(Player player) {
        player.getInventory().setItem(getInt("ModeGUI.item.slot"), createItem(
                getString("ModeGUI.item.material"),
                getString("ModeGUI.item.name"),
                getList("ModeGUI.item.lore"),
                getInt("ModeGUI.item.amount"),
                getInt("ModeGUI.item.durability"),
                getString("ModeGUI.item.skullOwner").replace("%player%", player.getName())
        ));

        player.getInventory().setItem(getInt("PlayersVisibilitySwitch.item.slot"), createItem(
                getString("PlayersVisibilitySwitch.item.shown.material"),
                getString("PlayersVisibilitySwitch.item.shown.name"),
                getList("PlayersVisibilitySwitch.item.shown.lore"),
                getInt("PlayersVisibilitySwitch.item.shown.amount"),
                getInt("PlayersVisibilitySwitch.item.shown.durability"),
                getString("PlayersVisibilitySwitch.item.shown.skullOwner").replace("%player%", player.getName())
        ));

        player.getInventory().setItem(getInt("Stats.item.slot"), createItem(
                getString("Stats.item.material"),
                getString("Stats.item.name"),
                getList("Stats.item.lore"),
                getInt("Stats.item.amount"),
                getInt("Stats.item.durability"),
                getString("Stats.item.skullOwner").replace("%player%", player.getName())
        ));

        player.getInventory().setItem(getInt("Dash.item.slot"), createItem(
                getString("Dash.item.material"),
                getString("Dash.item.name"),
                getList("Dash.item.lore"),
                getInt("Dash.item.amount"),
                getInt("Dash.item.durability"),
                getString("Dash.item.skullOwner").replace("%player%", player.getName())
        ));

        player.getInventory().setItem(getInt("Kits.item.slot"), createItem(
                getString("Kits.item.material"),
                getString("Kits.item.name"),
                getList("Kits.item.lore"),
                getInt("Kits.item.amount"),
                getInt("Kits.item.durability"),
                getString("Kits.item.skullOwner").replace("%player%", player.getName())
        ));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!e.getWhoClicked().hasPermission("core.ignoreclickevent")) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        if(!e.getWhoClicked().hasPermission("core.ignoreclickevent")) e.setCancelled(true);
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        if(e.hasBlock() && e.getClickedBlock().getLocation().equals(new Location(
                Bukkit.getWorld(getString("Kits.room.exit-button.world")),
                getDouble("Kits.room.exit-button.x"),
                getDouble("Kits.room.exit-button.y"),
                getDouble("Kits.room.exit-button.z")
        )) && editingKit.contains(player)) {
            player.chat(getString("Kits.room.exit-button.command"));

            player.teleport(new Location(
                    Bukkit.getWorld(getString("Lobby.backtospawn.spawn.world")),
                    getDouble("Lobby.backtospawn.spawn.x"),
                    getDouble("Lobby.backtospawn.spawn.y"),
                    getDouble("Lobby.backtospawn.spawn.z"))
            );

            initItems(player);

            for(Player p : Bukkit.getOnlinePlayers()) {
                player.showPlayer(p);
            }

            editingKit.remove(player);
        }

        if(e.hasItem() && e.getItem().getItemMeta().hasDisplayName()) {
            String item_name = e.getItem().getItemMeta().getDisplayName();

            if(item_name.equals(getString("ModeGUI.item.name"))) {
                player.openInventory(ModeGUI.getInventory(e.getPlayer()));
            } else if(item_name.equals(getString("Stats.item.name"))) {
                player.openInventory(StatsGUI.getInventory(e.getPlayer()));
            } else if(item_name.equals(getString("PlayersVisibilitySwitch.item.shown.name"))) {
                hidePlayers(player);
            } else if(item_name.equals(getString("PlayersVisibilitySwitch.item.hidden.name"))) {
                showPlayers(player);
            } else if(item_name.equals(getString("Dash.item.name"))) {

                if(cooldowns.containsKey(player)) {
                    if(cooldowns.get(player) > System.currentTimeMillis()) {
                        int timeLeft = (int) ((cooldowns.get(player) - System.currentTimeMillis()) / 1000);
                        player.sendMessage(getString("Messages.dash-cooldown", player).replace("%time%", String.valueOf(timeLeft)));
                        return;
                    }
                }
                cooldowns.put(player, System.currentTimeMillis() + (getInt("Dash.cooldown")*1000));
                throwPlayer(player);

            } else if(item_name.equals(getString("Kits.item.name"))) {
                editKit(player);
            } else return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(e.getPlayer().getLocation().getY() < getDouble("Lobby.backtospawn.level")) {
            e.getPlayer().teleport(new Location(
                    Bukkit.getWorld(getString("Lobby.backtospawn.spawn.world")),
                    getDouble("Lobby.backtospawn.spawn.x"),
                    getDouble("Lobby.backtospawn.spawn.y"),
                    getDouble("Lobby.backtospawn.spawn.z"))
            );
        }
    }

    public static void editKit(Player player) {
        if(editingKit.contains(player)) return;

        player.chat(getString("Kits.command"));

        player.teleport(new Location(
           Bukkit.getWorld(getString("Kits.room.coords.world")),
           getDouble("Kits.room.coords.x"),
           getDouble("Kits.room.coords.y"),
           getDouble("Kits.room.coords.z")
        ));

        player.getInventory().clear();
        for(Player p : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(p);
        }

        editingKit.add(player);
    }

    public static void hidePlayers(Player player) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(p);
        }
        player.getInventory().setItem(getInt("PlayersVisibilitySwitch.item.slot"), createItem(
                getString("PlayersVisibilitySwitch.item.hidden.material"),
                getString("PlayersVisibilitySwitch.item.hidden.name"),
                getList("PlayersVisibilitySwitch.item.hidden.lore"),
                getInt("PlayersVisibilitySwitch.item.hidden.amount"),
                getInt("PlayersVisibilitySwitch.item.hidden.durability"),
                getString("PlayersVisibilitySwitch.item.hidden.skullOwner").replace("%player%", player.getName())
        ));

        player.sendMessage(getString("PlayersVisibilitySwitch.messages.hidden"));

        if(!hiddenPlayers.contains(player)) hiddenPlayers.add(player);
    }

    public static void showPlayers(Player player) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            player.showPlayer(p);
        }
        player.getInventory().setItem(getInt("PlayersVisibilitySwitch.item.slot"), createItem(
                getString("PlayersVisibilitySwitch.item.shown.material"),
                getString("PlayersVisibilitySwitch.item.shown.name"),
                getList("PlayersVisibilitySwitch.item.shown.lore"),
                getInt("PlayersVisibilitySwitch.item.shown.amount"),
                getInt("PlayersVisibilitySwitch.item.shown.durability"),
                getString("PlayersVisibilitySwitch.item.shown.skullOwner").replace("%player%", player.getName())
        ));

        player.sendMessage(getString("PlayersVisibilitySwitch.messages.shown"));

        if(hiddenPlayers.contains(player)) hiddenPlayers.remove(player);
    }

    public void throwPlayer(Player player) {
        Location location = player.getLocation();
        location.setPitch(getInt("Dash.pitch"));

        Vector vector = location.getDirection();
        player.setVelocity(vector.multiply(getInt("Dash.multiplier")));

        if(getBoolean("Dash.sound.enabled")) player.playSound(location,
                Sound.valueOf(getString("Dash.sound.sound")),
                getInt("Dash.sound.volume"),
                getInt("Dash.sound.pitch"));
    }
}