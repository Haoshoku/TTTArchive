package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onDamage( EntityDamageEvent event ) {
        if ( event.getEntity() instanceof Zombie ) event.setCancelled( true );

        if ( event.getEntity() instanceof Player ) {
            Player player = (Player) event.getEntity();
            TTTUser user = TTTUser.getUser( player );

            if ( GameState.getGameState() != GameState.INGAME ) {
                event.setCancelled( true );

                if ( event.getCause() == EntityDamageEvent.DamageCause.VOID )
                    TTTPlugin.getPlugin().getManager().teleportToLocation( player, "lobby", false );
                return;
            }



            if ( user.isSpectator() )
                event.setCancelled( true );
        }

    }

}
