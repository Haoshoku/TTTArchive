package xyz.haoshoku.ttt.countdown;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.scheduler.BukkitTask;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.haoshoku.ttt.TTTPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class LobbyCountdown extends BukkitRunnable {

    @Getter @Setter private int lobbyTime;
    @Getter @Setter private int minPlayers;
    private boolean started;
    @Getter @Setter private BukkitTask teleportTask;

    @Override
    public void run() {

        if ( Bukkit.getOnlinePlayers().size() < minPlayers ) {
            if ( this.started ) {
                this.lobbyTime = 30;
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.lobby_countdown_waiting_for_players" ) );
                this.started = false;

                for ( Player online : Bukkit.getOnlinePlayers() ) {
                    online.setLevel( 0 );
                    online.setExp( 0F );
                }
            }
            return;
        }

        if ( !started )
            started = true;

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            player.setLevel( this.lobbyTime );
            player.setExp( (float) this.lobbyTime / 30F );
        }

        switch ( this.lobbyTime ) {
            case 30: case 15: case 10: case 5: case 4: case 3: case 2: case 1:
                for ( Player player : Bukkit.getOnlinePlayers() )
                    player.playSound( player.getLocation(), Sound.NOTE_PLING, 1, 1 );

                Bukkit.broadcastMessage(
                        TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.lobby_countdown_message" )
                                .replace( "%seconds%", String.valueOf( this.lobbyTime ) ) );
                break;

            case 0:
                this.cancel();

                Bukkit.getScheduler().runTaskAsynchronously( TTTPlugin.getPlugin(), () -> {
                    TTTPlugin.getPlugin().getManager().getVoting().calculateMapWinner();
                    String winnerMap = TTTPlugin.getPlugin().getManager().getVoting().getWinnerMap();

                    TTTPlugin.getPlugin().getManager().getTesterCountdown().load( winnerMap.toLowerCase() );
                    Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.lobby_countdown_teleport" )
                            .replace( "%winner_map%", winnerMap ) );

                    List<Location> locationList = new ArrayList<>();
                    Random random = new Random();
                    if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( "spawns." + winnerMap.toLowerCase() ) != null ) {
                        int size = TTTPlugin.getPlugin().getConfig().getConfigurationSection( "spawns." + winnerMap.toLowerCase() ).getKeys( false ).size();
                        for ( int i = 1; i < size+1; i++ ) {
                            Location location = TTTPlugin.getPlugin().getManager().getConfigLocation( "spawns." + winnerMap.toLowerCase() + "." + i );
                            locationList.add( location );
                        }
                    }

                    Bukkit.getWorld( winnerMap ).setDifficulty( Difficulty.EASY );

                    GameState.setGameState( GameState.FORBIDDEN_MOVE );

                    List<Player> onlinePlayers = new ArrayList<>();
                    onlinePlayers.addAll( Bukkit.getOnlinePlayers() );





                    teleportTask = Bukkit.getScheduler().runTaskTimer( TTTPlugin.getPlugin(), () -> {
                        if ( onlinePlayers.isEmpty() ) {
                            new MoveCountdown().runTaskTimer( TTTPlugin.getPlugin(), 40L, 20L );
                        } else {
                            Player player = onlinePlayers.get( 0 );
                            TTTUser user = TTTUser.getUser( player );

                            player.getInventory().clear();
                            player.updateInventory();

                            user.setBuildState( false );
                            user.setAlive( true );
                            player.setGameMode( GameMode.SURVIVAL );
                            Location location = locationList.get( random.nextInt( locationList.size() ) );
                            TTTPlugin.getPlugin().getManager().getDatabase().addStats( player.getUniqueId().toString(), "played", 1 );
                            TTTUser.setAliveSize( TTTUser.getAliveSize() + 1 );
                            player.teleport( location.add( 0, 1, 0 ) );
                            locationList.remove( location );
                            onlinePlayers.remove( player );
                        }

                    }, 0L, 10L );
                } );

                break;
        }

        this.lobbyTime--;
    }
}
