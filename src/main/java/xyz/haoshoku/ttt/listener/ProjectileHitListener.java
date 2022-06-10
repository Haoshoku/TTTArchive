package xyz.haoshoku.ttt.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener {

    @EventHandler
    public void onHit( ProjectileHitEvent event ) {
        if ( event.getEntity() instanceof Arrow ) {
            if ( event.getEntity().getCustomName() != null ) {
                if ( event.getEntity().getCustomName().equalsIgnoreCase( "§aCreeper" ) ) {
                    event.getEntity().getWorld().spawnEntity( event.getEntity().getLocation(), EntityType.CREEPER );
                }
            }

        }
    }


}
