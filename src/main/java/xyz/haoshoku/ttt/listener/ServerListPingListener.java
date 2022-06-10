package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

    @EventHandler
    public void onPing( ServerListPingEvent event ) {
        if ( GameState.getGameState() == GameState.LOBBY )
            event.setMotd( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.motd.lobby" ) );
        else if ( GameState.getGameState() == GameState.RESTARTING )
            event.setMotd( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.motd.restarting" ) );
        else
            event.setMotd( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.motd.ingame" ) );
    }

}
