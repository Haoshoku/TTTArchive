package xyz.haoshoku.ttt.commands;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( ! ( sender instanceof Player ) ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
            return true;
        }

        Player player = (Player) sender;
        TTTUser user = TTTUser.getUser( player );

        if ( user.isBuildState() ) {
            user.setBuildState( false );
            player.setGameMode( GameMode.SURVIVAL );
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.build_mode_disabled" ) );
        } else {
            user.setBuildState( true );
            player.setGameMode( GameMode.CREATIVE );
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.build_mode_enabled" ) );
        }

        return true;
    }
}
