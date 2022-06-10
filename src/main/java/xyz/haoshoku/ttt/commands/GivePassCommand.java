package xyz.haoshoku.ttt.commands;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GivePassCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( args.length < 3 ) {
            sendHelp( sender );
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously( TTTPlugin.getPlugin(), () -> {
            try {
                OfflinePlayer player = Bukkit.getOfflinePlayer( args[0] );
                String passType = args[1].toLowerCase();
                int amount = Integer.parseInt( args[2] );

                if ( !TTTPlugin.getPlugin().getManager().getDatabase().exists( player.getUniqueId().toString() ) ) {
                    sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.player_does_not_exist" ) );
                    return;
                }

                switch ( passType ) {
                    case "traitor":
                    case "t":
                        TTTPlugin.getPlugin().getManager().getDatabase().addStats( player.getUniqueId().toString(), "traitor_pass", amount );
                        sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.give_pass_traitor_given" ).replace( "%amount%", String.valueOf( amount ) ) );

                        if ( Bukkit.getPlayer( player.getUniqueId() ) != null ) {
                            TTTUser user = TTTUser.getUser( Bukkit.getPlayer( player.getUniqueId() ) );
                            user.setTraitorPasses( user.getTraitorPasses() + amount );
                        }
                        break;

                    case "detective":
                    case "d":
                        TTTPlugin.getPlugin().getManager().getDatabase().addStats( player.getUniqueId().toString(), "detective_pass", amount );
                        sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.give_pass_detective_given" ).replace( "%amount%", String.valueOf( amount ) ) );
                        if ( Bukkit.getPlayer( player.getUniqueId() ) != null ) {
                            TTTUser user = TTTUser.getUser( Bukkit.getPlayer( player.getUniqueId() ) );
                            user.setDetectivePasses( user.getDetectivePasses() + amount );
                        }
                        break;

                    case "token":
                    case "tokens":
                        TTTPlugin.getPlugin().getManager().getDatabase().addStats( player.getUniqueId().toString(), "tokens", amount );
                        sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.give_pass_tokens_given" ).replace( "%amount%", String.valueOf( amount ) ) );
                        if ( Bukkit.getPlayer( player.getUniqueId() ) != null ) {
                            TTTUser user = TTTUser.getUser( Bukkit.getPlayer( player.getUniqueId() ) );
                            user.setTokens( user.getTokens() + amount );
                        }
                        break;

                    default:
                        sendHelp( sender );
                        break;
                }


            } catch ( Exception e ) {
                sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.give_pass_error" ) );
            }
        } );


        return true;
    }

    private void sendHelp( CommandSender sender ) {
        sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.give_pass_usage" ) );
    }

}
