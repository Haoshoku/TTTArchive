package xyz.haoshoku.ttt.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.haoshoku.ttt.TTTPlugin;

public class MapTeleportCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( ! ( sender instanceof Player ) ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
            return true;
        }

        Player player = (Player) sender;

        if ( args.length == 0 ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.map_teleport_usage" ) );
            return true;
        }

        if ( args[0].equalsIgnoreCase( "list" ) ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.map_teleport_list" ) );
            for ( World world : Bukkit.getWorlds() )
                player.sendMessage( "§7- §e" + world.getName() );
            return true;
        }

        World world = Bukkit.getWorld( args[0] );

        if ( world == null ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.map_teleport_does_not_exist" ) );
            return true;
        }

        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.map_teleport_teleported" )
                .replace( "%world%", world.getName() ) );
        player.teleport( world.getSpawnLocation() );

        return true;
    }

}
