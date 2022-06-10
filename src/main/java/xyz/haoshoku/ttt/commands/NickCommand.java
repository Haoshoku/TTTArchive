package xyz.haoshoku.ttt.commands;

import net.haoshoku.nick.NickPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;

import java.util.Map;
import java.util.UUID;

public class NickCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( ! ( sender instanceof Player ) ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
            return true;
        }

        if ( GameState.getGameState() != GameState.LOBBY && TTTPlugin.getPlugin().getManager().getLobbyCountdown().getLobbyTime() < 5 ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.only_works_lobby" ) );
            return true;
        }

        Player player = (Player) sender;

        if ( args.length > 0 && args[0].equalsIgnoreCase( "list" ) ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.nick_list" ) );

            for ( Map.Entry<UUID, String> entry : NickPlugin.getPlugin().getAPI().getNickedPlayers().entrySet() ) {
                player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.nick_list_output" )
                        .replace( "%oldName%", NickPlugin.getPlugin().getAPI().getOriginalGameProfileName( Bukkit.getPlayer( entry.getKey() ) ) ).replace( "%newName%", entry.getValue() ) );
            }
            return true;
        }

        TTTPlugin.getPlugin().getManager().nick( player, false, false );
        return true;
    }

}
