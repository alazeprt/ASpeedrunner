package top.alazeprt;

import org.bukkit.plugin.java.JavaPlugin;
import top.alazeprt.command.AdminCommand;
import top.alazeprt.event.HunterEvent;
import top.alazeprt.event.RunnerEvent;
import top.alazeprt.util.GameThread;

public class ASpeedrunner extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new RunnerEvent(), this);
        getServer().getPluginManager().registerEvents(new HunterEvent(), this);
        getCommand("speedrunner").setExecutor(new AdminCommand());
    }

    @Override
    public void onDisable() {
        if(GameThread.thread != null && !GameThread.thread.isInterrupted()) {
            GameThread.thread.interrupt();
        }
    }
}
