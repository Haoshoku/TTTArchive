package xyz.haoshoku.ttt.listener;

import net.haoshoku.nick.NickPlugin;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.manager.FileManager;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private FileManager messageManager;
    private FileManager settingsManager;

    public PlayerDeathListener() {
        this.messageManager = TTTPlugin.getPlugin().getManager().getMessageManager();
        this.settingsManager = TTTPlugin.getPlugin().getManager().getSettingManager();
    }

    @EventHandler
    public void onDeath( PlayerDeathEvent event ) {
        Player player = event.getEntity();
        TTTUser playerUser = TTTUser.getUser( player );

        if ( NickPlugin.getPlugin().getAPI().isNicked( player ) ) {
            NickPlugin.getPlugin().getAPI().unnick( player );
            NickPlugin.getPlugin().getAPI().resetGameProfileName( player );
            NickPlugin.getPlugin().getAPI().refreshPlayer( player );
        }

        event.getDrops().clear();
        event.setDroppedExp( 0 );
        event.setDeathMessage( null );

        Bukkit.getScheduler().runTaskLater( TTTPlugin.getPlugin(), () -> player.spigot().respawn(), 10L );


        TTTPlugin.getPlugin().getManager().getDatabase().addStats( player.getUniqueId().toString(), "deaths", 1 );

        if ( playerUser.getCombatPlayer() != null && playerUser.getCombatTime() >= System.currentTimeMillis() ) {
            Player killer = playerUser.getCombatPlayer();
            TTTUser killerUser = TTTUser.getUser( killer );

            if ( Bukkit.getPlayer( killer.getName() ) != null ) {
                TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "kills", 1 );

                if ( playerUser.isInnocent() && killerUser.isTraitor() ) {
                    int karma = this.settingsManager.getConfiguration().getInt( "settings.karma.traitor_kills_innocent" );
                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "karma", karma );

                    killer.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.innocent_positive_killed" )
                            .replace( "%player%", player.getName() )
                            .replace( "%karma%", String.valueOf( karma ) ) );

                    player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.killed_by_traitor" )
                            .replace( "%killer%", killer.getName() ) );

                    killerUser.setTraitorPoints( killerUser.getTraitorPoints() + 1 );
                    killerUser.setKarma( killerUser.getKarma() + karma );
                }

                if ( playerUser.isTraitor() && killerUser.isDetective() ) {
                    int karma = this.settingsManager.getConfiguration().getInt( "settings.karma.detective_kills_traitor" );

                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "karma", karma );

                    killer.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.traitor_killed" )
                            .replace( "%player%", player.getName() )
                            .replace( "%karma%", String.valueOf( karma ) ) );

                    player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.killed_by_detective" ).replace( "%killer%", killer.getName() ) );
                    killerUser.setDetectivePoints( killerUser.getDetectivePoints() + 2 );
                    killerUser.setKarma( killerUser.getKarma() + karma );
                }

                if ( playerUser.isTraitor() && killerUser.isInnocent() ) {
                    int karma = this.settingsManager.getConfiguration().getInt( "settings.karma.innocent_kills_traitor" );

                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "karma", karma );
                    killer.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.traitor_killed" )
                            .replace( "%player%", player.getName() )
                            .replace( "%karma%", String.valueOf( karma ) ) );
                    player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.killed_by_innocent" ).replace( "%killer%", killer.getName() ) );
                    killerUser.setKarma( killerUser.getKarma() + karma );
                }

                if ( playerUser.isDetective() && killerUser.isTraitor() ) {
                    int karma = this.settingsManager.getConfiguration().getInt( "settings.karma.traitor_kills_detective" );
                    killerUser.setTraitorPoints( killerUser.getTraitorPoints() + 3 );
                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "karma", karma );
                    killer.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.detective_positive_killed" )
                            .replace( "%player%", player.getName() )
                            .replace( "%karma%", String.valueOf( karma ) ) );
                    player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.killed_by_traitor" ).replace( "%killer%", killer.getName() ) );
                    killerUser.setKarma( killerUser.getKarma() + karma );
                }

                if ( playerUser.isDetective() && killerUser.isInnocent() ) {
                    int karma = this.settingsManager.getConfiguration().getInt( "settings.karma.innocent_kills_detective" );
                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "karma", karma );
                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "fail_rate", 1 );
                    killer.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.detective_negative_killed" )
                            .replace( "%player%", player.getName() )
                            .replace( "%karma%", String.valueOf( karma ) ) );
                    player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.killed_by_innocent" ).replace( "%killer%", killer.getName() ) );
                    killerUser.setKarma( killerUser.getKarma() - karma );
                }

                if ( playerUser.isInnocent() && killerUser.isInnocent() ) {
                    int karma = this.settingsManager.getConfiguration().getInt( "settings.karma.innocent_kills_innocent" );
                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "karma", karma );
                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "fail_rate", 1 );
                    killer.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.innocent_negative_killed" )
                            .replace( "%player%", player.getName() )
                            .replace( "%karma%", String.valueOf( karma ) ) );
                    player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.killed_by_innocent" ).replace( "%killer%", killer.getName() ) );
                    killerUser.setKarma( killerUser.getKarma() - karma );
                }

                if ( playerUser.isInnocent() && killerUser.isDetective() ) {
                    int karma = this.settingsManager.getConfiguration().getInt( "settings.karma.detective_kills_innocent" );
                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "karma", karma );
                    TTTPlugin.getPlugin().getManager().getDatabase().addStats( killer.getUniqueId().toString(), "fail_rate", 1 );
                    killer.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.innocent_negative_killed" )
                            .replace( "%player%", player.getName() )
                            .replace( "%karma%", String.valueOf( karma ) ) );
                    player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.deaths.killed_by_detective" ).replace( "%killer%", killer.getName() ) );
                    killerUser.setDetectivePoints( killerUser.getDetectivePoints() - 1 );
                    killerUser.setKarma( killerUser.getKarma() - karma );
                }

            }
        }

        if ( playerUser.isDetective() || playerUser.isInnocent() )
            TTTUser.setInnoDetectiveSize( TTTUser.getInnoDetectiveSize() - 1 );
        else if ( playerUser.isTraitor() )
            TTTUser.setTraitorSize( TTTUser.getTraitorSize() - 1 );

        TTTUser.setAliveSize( TTTUser.getAliveSize() - 1 );
        TTTPlugin.getPlugin().getManager().spawnZombie( player );

        playerUser.setAlive( false );
        playerUser.setSpectator( true );
        playerUser.setTraitor( false );
        playerUser.setInnocent( false );
        playerUser.setDetective( false );


        Bukkit.getOnlinePlayers().forEach( online -> TTTPlugin.getPlugin().getManager().readDataFromScoreboard( online ) );
        TTTPlugin.getPlugin().getManager().checkGameState();
        TTTPlugin.getPlugin().getManager().calculateSpectatorInventory();
    }

}
