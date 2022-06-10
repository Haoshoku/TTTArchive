package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onPlace( BlockPlaceEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );

        if ( user.isBuildState() )
            return;

        event.setCancelled( true );
    }

}
