package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class PlayerInteractAtEntityListener implements Listener {

    @EventHandler
    public void onInteract( PlayerInteractAtEntityEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );


        if ( !user.isBuildState() )
            event.setCancelled( true );
    }

}
