package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void onDrop( PlayerDropItemEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );

        if ( user.isSpectator()
                || GameState.getGameState() == GameState.LOBBY
                || GameState.getGameState() == GameState.RESTARTING ) {
            if ( !user.isBuildState() )
                event.setCancelled( true );
        }

        if ( GameState.getGameState() == GameState.INGAME || GameState.getGameState() == GameState.RESTARTING ) {
            if ( event.getItemDrop().getItemStack().getType() != Material.STONE_SWORD
                    && event.getItemDrop().getItemStack().getType() != Material.IRON_SWORD
                    && event.getItemDrop().getItemStack().getType() != Material.BOW
                    && event.getItemDrop().getItemStack().getType() != Material.ARROW
                    && event.getItemDrop().getItemStack().getType() != Material.WOOD_SWORD )
                event.setCancelled( true );
        }
    }

}
