package xyz.haoshoku.ttt.commands;

import xyz.haoshoku.ttt.TTTPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokensCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( ! ( sender instanceof Player ) ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
            return true;
        }

        Player player = (Player) sender;
        TTTPlugin.getPlugin().getManager().getDatabase().getDataAsync( player.getUniqueId().toString(), "tokens", integer -> player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.tokens_of_player" )
                .replace( "%tokens%", String.valueOf( integer ) ) ) );

        return true;
    }
}
