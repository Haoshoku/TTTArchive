package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onDamage( EntityDamageByEntityEvent event ) {
        if ( event.getDamager() instanceof Player ) {
            Player damager = (Player) event.getDamager();
            if ( TTTUser.getUser( damager ).isSpectator() ) {
                event.setCancelled( true );
                return;
            }
        }
        if ( event.getEntity() instanceof Player ) {
            Player player = (Player) event.getEntity();

            Player damager = null;

            if ( event.getDamager() instanceof Player )
                damager = (Player) event.getDamager();
            else if ( event.getDamager() instanceof Arrow ) {
                Arrow arrow = (Arrow) event.getDamager();

                if ( arrow.getShooter() instanceof Player )
                    damager = (Player) arrow.getShooter();
            }


            if ( damager != null && GameState.getGameState() == GameState.INGAME ) {
                TTTUser playerUser = TTTUser.getUser( player );
                TTTUser damagerUser = TTTUser.getUser( damager );

                if ( damagerUser.isSpectator() ) {
                    event.setCancelled( true );
                    return;
                }

                if ( playerUser.isTraitor() && damagerUser.isTraitor() ) {
                    event.setDamage( 0 );
                    return;
                }

                if ( playerUser.isDetective() && damagerUser.isDetective() ) {
                    event.setDamage( 0 );
                    return;
                }

                playerUser.setCombatPlayer( damager );
                damagerUser.setCombatPlayer( player );

                playerUser.setCombatTime( System.currentTimeMillis() + 10000L );
                damagerUser.setCombatTime( System.currentTimeMillis() + 10000L );
            }
        }
    }

}
