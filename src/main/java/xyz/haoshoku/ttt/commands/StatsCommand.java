package xyz.haoshoku.ttt.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.manager.dbmanager.TTTDatabase;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {
        if ( args.length == 0 ) {
            if ( ! ( sender instanceof Player ) ) {
                sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
                return true;
            }

            Bukkit.getScheduler().runTaskAsynchronously( TTTPlugin.getPlugin(), () -> sendHelp( sender, ((Player) sender) ) );

            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously( TTTPlugin.getPlugin(), () -> sendHelp( sender, Bukkit.getOfflinePlayer( args[0] ) ) );
        return true;
    }

    private void sendHelp( CommandSender sender, OfflinePlayer player ) {

        int count = 0;

        for( int i = 0; i < TTTPlugin.getPlugin().getManager().getMessageManager().getConfiguration().getConfigurationSection( "ttt.command.stats" ).getKeys( false ).size(); i++ ) {
            count++;
            String line = TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.stats.line_" + count );

            if ( line == null )
                break;

            sender.sendMessage( applied( player, "ttt.command.stats.line_" + count ) );
        }
    }

    private String applied( OfflinePlayer player, String string ) {
        TTTDatabase database = TTTPlugin.getPlugin().getManager().getDatabase();
        int kills = database.getDataSync( player.getUniqueId().toString(), "kills" );
        int wins = database.getDataSync( player.getUniqueId().toString(), "wins" );
        int played = database.getDataSync( player.getUniqueId().toString(), "played" );

        String config = TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( string );
        config = config.replace( "%player%", player.getName() );
        config = config.replace( "%ranking%", String.valueOf( database.getRanking( player.getUniqueId().toString() ) ) );
        config = config.replace( "%karma%", String.valueOf( database.getDataSync( player.getUniqueId().toString(), "karma" ) ) );
        config = config.replace( "%kills%", String.valueOf( kills ) );
        config = config.replace( "%deaths%", String.valueOf( database.getDataSync( player.getUniqueId().toString(), "deaths" ) ) );
        config = config.replace( "%played%", String.valueOf( played ) );
        config = config.replace( "%wins%", String.valueOf( wins ) );
        config = config.replace( "%win_rate%", (this.getStats( wins, played ) ) );
        config = config.replace( "%false_kill_rate%", (this.getStats( database.getDataSync( player.getUniqueId().toString(), "fail_rate" ), kills ) ) );
        return config;
    }

    private String getStats( double value1, double value2 ) {
        if ( value1 == 0D && value2 == 0D )
            return "0.0";

        if ( value1 >= 0D && value2 == 0D )
            return "0.0";

        double quotient = value1 / value2;
        quotient = Math.round( quotient * 100D ) / 100D;

        String string = String.valueOf( quotient * 100D );
        if ( string.length() > 5 ) {
            string = string.substring( 0, 5 );
        }
        return string;
    }

}
