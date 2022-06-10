package xyz.haoshoku.ttt.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class PluginDisableListener implements Listener {

    @EventHandler
    public void onDisable( PluginDisableEvent event ) {
        Bukkit.getScheduler().cancelAllTasks();
        Bukkit.shutdown();
    }

}
