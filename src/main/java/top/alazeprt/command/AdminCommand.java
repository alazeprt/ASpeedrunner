package top.alazeprt.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import top.alazeprt.util.GameThread;

import java.util.*;

public class AdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!commandSender.hasPermission("speedrunner.admin")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this top.alazeprt.command!");
            return true;
        }
        if(strings.length != 6) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /speedrunner start <hunters> <runners> <time> <delay> <world>");
            return true;
        }
        long hunters_count = Long.parseLong(strings[1]);
        long runners_count = Long.parseLong(strings[2]);
        long time = Long.parseLong(strings[3]);
        long delay = Long.parseLong(strings[4]);
        World world = Bukkit.getWorld(strings[5]);
        if(Bukkit.getOnlinePlayers().size() < (hunters_count + runners_count)) {
            commandSender.sendMessage(ChatColor.RED + "There are not enough players online!");
            return true;
        }
        Random random = new Random(new Random().nextInt());
        List<Player> hunters = new ArrayList<>();
        List<Player> runners = new ArrayList<>();
        List<Player> onlinePlayers = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player);
        }
        Collections.shuffle(onlinePlayers);
        for(Player player : onlinePlayers) {
            if(random.nextBoolean() && runners.size() < runners_count) {
                runners.add(player);
                Bukkit.broadcastMessage("Runner: " + player.getName());
            } else if(hunters.size() < hunters_count) {
                hunters.add(player);
                Bukkit.broadcastMessage("Hunner: " + player.getName());
            }
        }
        GameThread.start(hunters, runners, world, time, delay);
        return false;
    }
}
