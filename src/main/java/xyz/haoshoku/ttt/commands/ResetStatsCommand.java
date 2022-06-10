package xyz.haoshoku.ttt.commands;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetStatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( ! ( sender instanceof Player ) ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
            return true;
        }

        Player player = (Player) sender;
        TTTUser user = TTTUser.getUser( player );

        if ( args.length == 0 ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.reset_stats_message" ).replace( "%tokens%", String.valueOf( user.getTokens() ) ) );
            return true;
        }

        if ( args[0].equalsIgnoreCase( "confirm" ) ) {
            int token = user.getTokens();
            if ( token == 0 ) {
                player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.reset_stats_no_tokens" ) );
                return true;
            }

            user.setTokens( token - 1 );

            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.reset_stats_successfully" ).replace( "%player%", player.getName() ).replace( "%tokens%", String.valueOf( user.getTokens() ) ));
            TTTPlugin.getPlugin().getManager().getDatabase().removeStats( player.getUniqueId().toString(), "tokens", 1 );
            TTTPlugin.getPlugin().getManager().getDatabase().deleteAllStats( player.getUniqueId().toString() );

            return true;
        }

        return true;
    }

}
