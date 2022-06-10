package xyz.haoshoku.ttt.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener {

    @EventHandler
    public void onChange( WeatherChangeEvent event ) {
        event.setCancelled( true );
    }

}
