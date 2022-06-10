package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.countdown.RestartCountdown;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );



        if ( GameState.getGameState() == GameState.LOBBY ) {
            if ( user.isUsingDetectivePass() )
                TTTUser.setGlobalDetectivePassUsed( TTTUser.getGlobalDetectivePassUsed() - 1 );

            if ( user.isUsingTraitorPass() )
                TTTUser.setGlobalTraitorPassUsed( TTTUser.getGlobalTraitorPassUsed() - 1 );
            event.setQuitMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.quit_message" ).replace( "%player%", player.getDisplayName() ) );
        } else if ( GameState.getGameState() == GameState.INGAME && !user.isSpectator() ) {
            event.setQuitMessage( null );
            TTTPlugin.getPlugin().getManager().removePlayerFromRoles( player );
            TTTPlugin.getPlugin().getManager().spawnZombie( player );
            TTTPlugin.getPlugin().getManager().checkGameState();
        } else if ( GameState.getGameState() == GameState.GRACE_PERIOD || GameState.getGameState() == GameState.FORBIDDEN_MOVE ) {
            event.setQuitMessage( null );
            if ( Bukkit.getOnlinePlayers().size() <= 3 && GameState.getGameState() != GameState.RESTARTING ) {
                GameState.setGameState( GameState.RESTARTING );
                Bukkit.getScheduler().cancelAllTasks();
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.user_left_while_ingame" ) );
                new RestartCountdown().runTaskTimer( TTTPlugin.getPlugin(), 20L, 20L );
            }
        } else {
            event.setQuitMessage( null );
        }

        Bukkit.getOnlinePlayers().forEach( online -> TTTPlugin.getPlugin().getManager().readDataFromScoreboard( online ) );
        TTTPlugin.getPlugin().getManager().getVoting().removePlayerVote( player );
        TTTUser.deleteUser( player );

        TTTPlugin.getPlugin().getManager().calculateSpectatorInventory();

    }

}
