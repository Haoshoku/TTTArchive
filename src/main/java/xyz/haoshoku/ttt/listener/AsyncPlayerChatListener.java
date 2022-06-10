package xyz.haoshoku.ttt.listener;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.manager.FileManager;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat( AsyncPlayerChatEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );

        if ( GameState.getGameState() == GameState.INGAME ) {
            if ( user.isSpectator() ) {
                event.setCancelled( true );

                for ( TTTUser users : TTTUser.getUsers() ) {
                    if ( users.isSpectator() ) {
                        users.getPlayer().sendMessage(
                                TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.chat.spectator_chat" )
                                        .replace( "%player%", event.getPlayer().getName() )
                                        .replace( "%message%", event.getMessage() ) );
                    }
                }
                return;
            }
            if ( user.isDetective() )
                event.setFormat( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.chat.ingame_detective_chat" )
                        .replace( "%player%", player.getName() )
                        .replace( "%message%", "%2$s" ) );
            else
                event.setFormat( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.chat.ingame_chat" )
                        .replace( "%player%", player.getName() )
                        .replace( "%message%", "%2$s" ) );

        } else {
            FileManager manager = TTTPlugin.getPlugin().getManager().getSettingManager();
            String prefix = "";
            for ( String string : manager.getConfiguration().getConfigurationSection( "settings.ranks" ).getKeys( false ) ) {
                if ( user.isAutoNick() && GameState.getGameState() != GameState.RESTARTING ) {
                    Scoreboard scoreboard = player.getScoreboard();
                    Team team = scoreboard.getTeam( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.nick.use_nick_rank" ) );
                    prefix = team.getPrefix();
                } else if ( player.hasPermission( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.ranks." + string + ".permission" ) ) ) {
                    prefix = manager.getColoredValueFromKey( "settings.ranks." + string + ".prefix" );
                    break;
                }
            }
            event.setFormat( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.chat.lobby_chat" )
                    .replace( "%player%", player.getName() )
                    .replace( "%message%", "%2$s" )
                    .replace( "%playerPrefix%", prefix ) );
        }
    }

}
