package top.alazeprt.util;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import top.alazeprt.ASpeedrunner;
import top.alazeprt.event.RunnerEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameThread {

    public static List<Player> playingRunners = new ArrayList<>();

    public static List<Player> prepareHunters = new ArrayList<>();

    public static List<Player> hunters = new ArrayList<>();

    public static List<Player> runners = new ArrayList<>();

    public static Thread thread;

    public static double runTime;

    public static boolean running = false;

    public static void start(List<Player> hunters, List<Player> runners, World world, long time, long delay) {
        running = true;
        RunnerEvent.needReset = false;
        GameThread.runners = runners;
        GameThread.hunters = hunters;
        thread = new Thread(() -> {
            Bukkit.getScheduler().runTask(ASpeedrunner.getProvidingPlugin(ASpeedrunner.class), () -> {
                world.setTime(0L);
                world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                world.setGameRule(GameRule.KEEP_INVENTORY, true);
                world.setClearWeatherDuration(114514);
                world.setDifficulty(Difficulty.NORMAL);
                world.getWorldBorder().setSize(240.0);
            });
            prepareHunters.addAll(hunters);
            for(Player player : hunters) {
                Bukkit.getScheduler().runTask(ASpeedrunner.getProvidingPlugin(ASpeedrunner.class), () -> {
                    player.teleport(world.getSpawnLocation());
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().clear();
                    player.getInventory().setItem(1, new ItemStack(Material.COOKED_BEEF, 16));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false));
                    player.sendTitle(ChatColor.RED + "You are hunter", ChatColor.YELLOW + "You can start to catch the runner in " + delay + " seconds!");
                });
            }
            for(Player player : runners) {
                Bukkit.getScheduler().runTask(ASpeedrunner.getProvidingPlugin(ASpeedrunner.class), () -> {
                    player.teleport(world.getSpawnLocation());
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().clear();
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false));
                    player.sendTitle(ChatColor.YELLOW + "Leave there!", ChatColor.YELLOW + "You have " + delay + " seconds to leave!");
                });
            }
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getScheduler().runTask(ASpeedrunner.getProvidingPlugin(ASpeedrunner.class), () -> {
                for (Player player : runners) {
                    player.sendTitle(ChatColor.GREEN + "Start!", ChatColor.RED + "Hunters are coming!");
                }
                for (Player player : hunters) {
                    player.sendTitle(ChatColor.GREEN + "Start!", ChatColor.YELLOW + "To catch the runner!");
                    player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));
                }
                world.setGameRule(GameRule.KEEP_INVENTORY, true);
                world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                world.setGameRule(GameRule.DO_MOB_LOOT, false);
                world.setFullTime(0);
            });
            prepareHunters.clear();
            playingRunners.addAll(runners);
            long startTime = System.currentTimeMillis();
            while(runTime < time) {
                runTime = (System.currentTimeMillis() - startTime) / 1000.0;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    return;
                }
            }
            if(Thread.currentThread().isInterrupted()) return;
            RunnerEvent.needReset = true;
            Bukkit.getScheduler().runTask(ASpeedrunner.getProvidingPlugin(ASpeedrunner.class), () -> {
                for(Player player : runners) {
                    player.sendTitle(ChatColor.AQUA + "End!", null);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(world.getSpawnLocation());
                }
                for(Player player : hunters) {
                    player.sendTitle(ChatColor.AQUA + "End!", null);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(world.getSpawnLocation());
                }
                playingRunners.forEach(player -> Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " lived for " + time + " seconds"));
                playingRunners.clear();
                runners.clear();
                hunters.clear();
                runTime = 0;
                running = false;
                thread.interrupt();
                thread = null;
            });
        });
        thread.start();
    }
}
