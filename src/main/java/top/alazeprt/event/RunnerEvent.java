package top.alazeprt.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import top.alazeprt.ASpeedrunner;
import top.alazeprt.util.GameThread;

import java.util.ArrayList;
import java.util.List;

public class RunnerEvent implements Listener {

    private static List<Player> waitRespawnPlayers = new ArrayList<>();

    private static List<Player> resetPlayers = new ArrayList<>();

    public static boolean needReset = false;

    @EventHandler
    public void onDieEvent(PlayerDeathEvent event) {
        if(!GameThread.playingRunners.contains(event.getEntity())) return;
        Bukkit.broadcastMessage(ChatColor.YELLOW + event.getEntity().getName() + " lived for " +
                ChatColor.GREEN + String.format("%.2f", GameThread.runTime) + " seconds, " + ChatColor.RED + "now died");
        GameThread.playingRunners.remove(event.getEntity());
        waitRespawnPlayers.add(event.getEntity());
        resetPlayers.add(event.getEntity());
        if(GameThread.playingRunners.isEmpty()) {
            needReset = true;
            Bukkit.getScheduler().runTask(ASpeedrunner.getProvidingPlugin(ASpeedrunner.class), () -> {
                Bukkit.broadcastMessage(GameThread.runners.size() + " " + GameThread.hunters.size());
                for(Player player : GameThread.runners) {
                    player.sendTitle(ChatColor.AQUA + "End!", "");
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().clear();
                    player.teleport(player.getWorld().getSpawnLocation());
                }
                for(Player player : GameThread.hunters) {
                    player.sendTitle(ChatColor.AQUA + "End!", "");
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().clear();
                    player.teleport(player.getWorld().getSpawnLocation());
                }
                GameThread.playingRunners.clear();
                GameThread.hunters.clear();
                GameThread.runners.clear();
                GameThread.thread.interrupt();
                GameThread.thread = null;
                GameThread.runTime = 0;
                GameThread.running = false;
            });
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if(needReset) {
            for(Player player : resetPlayers) {
                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
                player.teleport(player.getWorld().getSpawnLocation());
            }
            return;
        }
        if(!waitRespawnPlayers.contains(event.getPlayer())) return;
        waitRespawnPlayers.remove(event.getPlayer());
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
    }
}
