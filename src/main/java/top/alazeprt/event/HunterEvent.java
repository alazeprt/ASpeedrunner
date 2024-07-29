package top.alazeprt.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.alazeprt.ASpeedrunner;
import top.alazeprt.util.GameThread;

public class HunterEvent implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(GameThread.prepareHunters.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot move while preparing!");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (GameThread.prepareHunters.contains(event.getPlayer()) || GameThread.hunters.contains(event.getPlayer())) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(ASpeedrunner.getProvidingPlugin(ASpeedrunner.class), () -> {
                Bukkit.getScheduler().runTask(ASpeedrunner.getProvidingPlugin(ASpeedrunner.class), () -> {
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false));
                });
            }, 2L);
            event.getPlayer().getInventory().setItem(1, new ItemStack(Material.COOKED_BEEF, 64));
        }

    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (GameThread.prepareHunters.contains(player)) {
                event.setCancelled(true);
                event.getDamager().sendMessage(ChatColor.RED + "You cannot attack hunter while preparing!");
            }
        }
    }
}
