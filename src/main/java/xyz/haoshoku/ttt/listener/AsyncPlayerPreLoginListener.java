package xyz.haoshoku.ttt.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.manager.dbmanager.TTTDatabase;
import xyz.haoshoku.ttt.user.TTTUser;

import java.util.UUID;

public class AsyncPlayerPreLoginListener implements Listener {

    @EventHandler
    public void onLogin( AsyncPlayerPreLoginEvent event ) {
        UUID uuid = event.getUniqueId();
        TTTUser user = TTTUser.getUserByUUID( uuid );
        TTTDatabase database = TTTPlugin.getPlugin().getManager().getDatabase();
        user.setLoaded( true );
        database.createPlayer( uuid.toString() );
        TTTPlugin.getPlugin().getManager().getNickDatabase().createPlayer( event.getUniqueId().toString() );
        boolean nickState = TTTPlugin.getPlugin().getManager().getNickDatabase().getNickStateSync( event.getUniqueId().toString() );
        user.setAutoNick( nickState );

        int karma = database.getDataSync( uuid.toString(), "karma" );
        int rankUp = database.getDataSync( uuid.toString(), "rank_up" );

        if ( (karma / rankUp) >= 500 ) {
            user.setRankup( true );
            database.addStatsSync( uuid.toString(), "rank_up", 1 );
            database.addStatsSync( uuid.toString(), "detective_pass", 1 );
            database.addStatsSync( uuid.toString(), "traitor_pass", 1 );
        }

        user.setRanking( database.getRanking( uuid.toString() ) );
        user.setKarma( karma );
        user.setDetectivePasses( database.getDataSync( uuid.toString(), "detective_pass" ) );
        user.setTraitorPasses( database.getDataSync( uuid.toString(), "traitor_pass" ) );
        user.setTokens( database.getDataSync( uuid.toString(), "tokens" ) );
    }

}
