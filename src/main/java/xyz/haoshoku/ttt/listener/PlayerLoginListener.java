package xyz.haoshoku.ttt.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerLoginListener implements Listener {

    @EventHandler( priority = EventPriority.HIGHEST )
    public void onLogin( PlayerLoginEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );
        user.setPlayer( player );

        if ( !user.isLoaded() ) {
            event.disallow( PlayerLoginEvent.Result.KICK_OTHER, TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.server_is_starting" ) );
            return;
        }

        TTTPlugin.getPlugin().getManager().getDatabase().updateLastName( player );
        TTTPlugin.getPlugin().getManager().getNickDatabase().updateLastName( player );

        if ( GameState.getGameState() == GameState.LOBBY ) {
            if ( Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers() ) {
                if ( !player.hasPermission( "ttt.join" ) ) {
                    event.disallow( PlayerLoginEvent.Result.KICK_OTHER, TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.premium_rank_needed" ) );
                    return;
                }

                List<Player> list = new ArrayList<>();

                for ( Player online : Bukkit.getOnlinePlayers() ) {
                    if ( !online.hasPermission( "ttt.join" ) && player != online )
                        list.add( online );
                }

                if ( list.isEmpty() ) {
                    event.disallow( PlayerLoginEvent.Result.KICK_OTHER, TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.server_full_with_permission" ) );
                    return;
                }

                Player kickedPlayer = list.get( new Random().nextInt( list.size() ) );
                kickedPlayer.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.kicked_message" ) );
                ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeUTF( "Connect" );
                output.writeUTF( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.lobby_server" ) );
                kickedPlayer.sendPluginMessage( TTTPlugin.getPlugin(), "BungeeCord", output.toByteArray() );
                kickedPlayer.kickPlayer( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.kicked_message" ) );
                event.allow();
            }
        } else if ( GameState.getGameState() == GameState.INGAME ) {
            event.allow();
            user.setSpectator( true );
        } else
            event.disallow( PlayerLoginEvent.Result.KICK_OTHER, TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.server_not_joinable" ) );
    }

}
