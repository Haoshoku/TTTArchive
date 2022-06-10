package xyz.haoshoku.ttt.listener;

import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

public class EntityCombustListener implements Listener {

    @EventHandler
    public void onCombust( EntityCombustEvent event ) {
        if ( event.getEntity() instanceof Zombie )
            event.setCancelled( true );
    }

}
