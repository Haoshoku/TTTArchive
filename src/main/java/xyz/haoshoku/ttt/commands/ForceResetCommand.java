package xyz.haoshoku.ttt.commands;

import xyz.haoshoku.ttt.TTTPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( args.length == 0 ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.force_reset_usage" ));
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously( TTTPlugin.getPlugin(), () -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( args[0] );

            if ( !TTTPlugin.getPlugin().getManager().getDatabase().exists( offlinePlayer.getUniqueId().toString() ) ) {
                sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.player_does_not_exist" ));
                return;
            }

            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.force_reset_successfully" ).replace( "%player%", offlinePlayer.getName() ) );
            TTTPlugin.getPlugin().getManager().getDatabase().deleteAllStats( offlinePlayer.getUniqueId().toString() );
        } );

        return true;
    }
}
