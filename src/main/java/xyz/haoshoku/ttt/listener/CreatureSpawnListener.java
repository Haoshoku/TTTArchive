package xyz.haoshoku.ttt.listener;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {

    @EventHandler
    public void onSpawn( CreatureSpawnEvent event ) {
        if ( event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM )
            event.setCancelled( true );

    }

}
