package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItemListener implements Listener {

    @EventHandler
    public void onPickup( PlayerPickupItemEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );

        if ( user.isSpectator()
                || GameState.getGameState() == GameState.LOBBY
                || GameState.getGameState() == GameState.RESTARTING ) {
            if ( !user.isBuildState() )
                event.setCancelled( true );
        }

    }

}
