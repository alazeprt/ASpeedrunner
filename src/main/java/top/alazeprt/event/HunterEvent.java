package top.alazeprt.event;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import top.alazeprt.util.GameThread;

public class HunterEvent implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(GameThread.prepareHunters.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot move while preparing!");
        }
    }
}
