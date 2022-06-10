package xyz.haoshoku.ttt.commands;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TraitorChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( ! ( sender instanceof Player ) ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
            return true;
        }

        Player player = (Player) sender;

        if ( GameState.getGameState() != GameState.INGAME ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.only_works_ingame" ) );
            return true;
        }

        TTTUser user = TTTUser.getUser( player );

        if ( !user.isTraitor() ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.traitor_chat_only" ) );
            return true;
        }

        if ( args.length == 0 ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.traitor_chat_usage" ) );
            return true;
        }

        StringBuilder builder = new StringBuilder();

        for( int i = 0; i < args.length; i++ )
            builder.append( args[i] + " " );


        for ( TTTUser users : TTTUser.getUsers() ) {
            if ( users.isTraitor() ) {
                users.getPlayer().sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.traitor_chat_message" )
                        .replace( "%player%", player.getName() ).replace( "%message%", builder.toString() ) );
            }
        }

        return false;
    }
}
