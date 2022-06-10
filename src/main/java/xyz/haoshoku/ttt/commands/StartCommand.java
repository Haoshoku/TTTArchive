package xyz.haoshoku.ttt.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;

public class StartCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {
        if ( Bukkit.getOnlinePlayers().size() <= 2 ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.start_minimum_players" ) );
            return true;
        }

        if ( GameState.getGameState() == GameState.LOBBY ) {
            if ( TTTPlugin.getPlugin().getManager().getLobbyCountdown().getLobbyTime() > 6 )
                TTTPlugin.getPlugin().getManager().getLobbyCountdown().setLobbyTime( 5 );
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.start_started" ) );
        } else if ( GameState.getGameState() == GameState.GRACE_PERIOD ) {
            if ( TTTPlugin.getPlugin().getManager().getGracePeriodCountdown().getPeriodTime() > 3 )
                TTTPlugin.getPlugin().getManager().getGracePeriodCountdown().setPeriodTime( 3 );
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.start_started" ) );
        } else
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.start_already_started" ) );
        return true;
    }
}
