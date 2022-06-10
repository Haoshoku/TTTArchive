package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.state.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove( PlayerMoveEvent event ) {
        Player player = event.getPlayer();

        if ( GameState.getGameState() == GameState.FORBIDDEN_MOVE ) {
            if ( event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ() )
                player.teleport( event.getFrom() );
        }

    }

}
