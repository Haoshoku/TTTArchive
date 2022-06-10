package xyz.haoshoku.ttt.commands;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.user.TTTUser;
import xyz.haoshoku.ttt.util.ItemBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TTTCommand implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( ! ( sender instanceof Player ) ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
            return true;
        }

        Player player = (Player) sender;
        TTTUser user = TTTUser.getUser( player );
        String worldLow = player.getWorld().getName().toLowerCase();

        if ( args.length == 0 ) {
            sendHelp( player );
            return true;
        }

        if ( args.length == 1 ) {
            switch ( args[0].toLowerCase() ) {
                case "build":
                    if ( user.isBuildState() ) {
                        user.setBuildState( false );
                        player.setGameMode( GameMode.SURVIVAL );
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.build_mode_disabled" ) );
                    } else {
                        user.setBuildState( true );
                        player.setGameMode( GameMode.CREATIVE );
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.build_mode_enabled" ) );
                    }
                    break;

                case "tools":
                    player.getInventory().clear();
                    player.setAllowFlight( true );
                    player.getInventory().setItem( 0, new ItemBuilder( Material.WOOD_AXE ).setDisplayName( "§aTester ground setter" ).toItemStack() );
                    player.getInventory().setItem( 1, new ItemBuilder( Material.STONE_AXE ).setDisplayName( "§7Tester light setter" ).toItemStack() );
                    player.getInventory().setItem( 2, new ItemBuilder( Material.GOLD_AXE ).setDisplayName( "§6Tester glass setter" ).toItemStack() );
                    player.getInventory().setItem( 3, new ItemBuilder( Material.IRON_AXE ).setDisplayName( "§fTester button setter" ).toItemStack() );
                    player.getInventory().setItem( 4, new ItemBuilder( Material.DIAMOND_AXE ).setDisplayName( "§bTester trap setter" ).toItemStack() );
                    player.getInventory().setItem( 7, new ItemBuilder( Material.STONE_PICKAXE ).setDisplayName( "§6Chest saver" ).toItemStack() );
                    player.getInventory().setItem( 8, new ItemBuilder( Material.IRON_PICKAXE ).setDisplayName( "§5EnderChest saver" ).toItemStack() );
                    user.setBuildState( true );
                    player.sendMessage( "§cIf you set the §6Tester glass setter§c, be careful!" );
                    player.sendMessage( "§cYou should set it like this:" );
                    player.sendMessage( "§ehttps://haoshoku.xyz/go/tester" );
                    break;

                case "setlobby":
                    writeLocationToConfig( player, "lobby" );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aLobby has been created" );
                    break;

                case "settester":
                    writeLocationToConfig( player, worldLow + ".tester_spawn" );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aTester location for world §e" + worldLow + " §ahas been created" );
                    break;

                case "settesterteleport":
                    writeLocationToConfig( player, worldLow + ".tester_teleport_spawn" );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aTester location for world §e" + worldLow + " §ahas been created" );
                    break;

                default:
                    sendHelp( player );
                    break;
            }
            return true;
        }

        if ( args[0].equalsIgnoreCase( "setspawn" ) ) {
            switch ( args[1].toLowerCase() ) {
                case "delete":
                    if ( args.length == 2 ) {
                        TTTPlugin.getPlugin().getConfig().set( "spawns." + worldLow, null );
                        TTTPlugin.getPlugin().getConfig().set( worldLow, null );
                        player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aSuccessfully deleted §4ALL §adata on map §e" + worldLow );
                    } else {
                        if ( args[2].equalsIgnoreCase( "all" ) ) {
                            for ( String string : TTTPlugin.getPlugin().getConfig().getConfigurationSection( "" ).getKeys( false ) )
                                TTTPlugin.getPlugin().getConfig().set( string, null );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aSuccessfully deleted §4ALL §aspawns you have set");
                        } else if ( NumberUtils.isNumber( args[2] ) ) {
                            int number = Integer.parseInt( args[2] );
                            TTTPlugin.getPlugin().getConfig().set( "spawns." + worldLow + "." + number, null );
                            TTTPlugin.getPlugin().getConfig().set( worldLow + ".ground." + number, null );
                            TTTPlugin.getPlugin().getConfig().set( worldLow + ".button." + number, null );
                            TTTPlugin.getPlugin().getConfig().set( worldLow + ".glass." + number, null );
                            TTTPlugin.getPlugin().getConfig().set( worldLow + ".trap." + number, null );
                            TTTPlugin.getPlugin().getConfig().set( worldLow + ".light." + number, null );
                            TTTPlugin.getPlugin().getConfig().set( worldLow + ".chest." + number, null );
                            TTTPlugin.getPlugin().getConfig().set( worldLow + ".ender_chest." + number, null );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted §4ALL §aspawns from map §e" + worldLow + " §awith the id of §e" + number );
                        }
                    }
                    TTTPlugin.getPlugin().saveConfig();
                    break;

                case "next":
                    int count;
                    if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( "spawns." + worldLow ) != null )
                        count = TTTPlugin.getPlugin().getConfig().getConfigurationSection( "spawns." + worldLow ).getKeys( false ).size();
                    else
                        count = 0;

                    count++;
                    writeLocationToConfig( player, "spawns." + worldLow + "." + count );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aSuccessfully set spawn for map §e" + worldLow + " §anumber §e" + count );
                    break;


                default:
                    break;
            }
            return true;
        }

        if ( args[0].equalsIgnoreCase( "deleteid" ) ) {
            if ( args.length < 3 ) {
                player.sendMessage( "§7- §e/ttt deleteID §a<key> <id> §8- §eDelete the specific location of spawn" );
                player.sendMessage( "§7- §cExample: §e/ttt deleteID glass 5" );
                player.sendMessage( "§7- §cAvailable keys: §ebutton, chest, ender_chest, glass, ground, light, spawn, trap" );
                return true;
            }

            if ( !NumberUtils.isNumber( args[2] ) ) {
                player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §cID must be a number" );
                return true;
            }

            int number = Integer.parseInt( args[2] );


            switch ( args[1].toLowerCase() ) {
                case "button":
                    TTTPlugin.getPlugin().getConfig().set( worldLow + ".button." + number, null );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted " + args[1] + " §awith id §e" + number + " §aon map §e" + worldLow );
                    break;

                case "chest":
                    TTTPlugin.getPlugin().getConfig().set( worldLow + ".chest." + number, null );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted " + args[1] + " §awith id §e" + number + " §aon map §e" + worldLow );
                    break;

                case "ender_chest":
                    TTTPlugin.getPlugin().getConfig().set( worldLow + ".ender_chest." + number, null );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted " + args[1] + " §awith id §e" + number + " §aon map §e" + worldLow );
                    break;

                case "glass":
                    TTTPlugin.getPlugin().getConfig().set( worldLow + ".glass." + number, null );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted " + args[1] + " §awith id §e" + number + " §aon map §e" + worldLow );
                    break;

                case "ground":
                    TTTPlugin.getPlugin().getConfig().set( worldLow + ".ground." + number, null );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted " + args[1] + " §awith id §e" + number + " §aon map §e" + worldLow );
                    break;

                case "light":
                    TTTPlugin.getPlugin().getConfig().set( worldLow + ".light." + number, null );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted " + args[1] + " §awith id §e" + number + " §aon map §e" + worldLow );
                    break;

                case "spawn":
                    TTTPlugin.getPlugin().getConfig().set( "spawns." + worldLow + "." + number, null );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted " + args[1] + " §awith id §e" + number + " §aon map §e" + worldLow );
                    break;

                case "trap":
                    TTTPlugin.getPlugin().getConfig().set( worldLow + ".trap." + number, null );
                    player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aDeleted " + args[1] + " §awith id §e" + number + " §aon map §e" + worldLow );
                    break;


                default:
                    player.sendMessage( "§7- §cInvalid key" );
                    player.sendMessage( "§7- §cAvailable keys: §ebutton, chest, ender_chest, glass, ground, light, spawn, trap" );
                    break;
            }
            TTTPlugin.getPlugin().saveConfig();
        }

        return true;
    }

    private void sendHelp( Player player ) {
        player.sendMessage( "" );
        player.sendMessage( TTTPlugin.getPlugin().getPrefix() );
        player.sendMessage( "§7- §e/ttt build §8- §eAllows/Disallows you to build" );
        player.sendMessage( "§7- §e/ttt tools §8- §eTester/Chest location setter - Be careful!" );
        player.sendMessage( "§7- §e/ttt setlobby §8- §eSets the lobby spawn" );
        player.sendMessage( "§7- §e/ttt settester §8- §eSets the tester spawn" );
        player.sendMessage( "§7- §e/ttt settesterteleport §8- §eIf a player enters tester, the §cOTHERS WITHIN TESTER §ewill be teleported to this location" );
        player.sendMessage( "§7- §e/ttt setspawn next §8- §eSet the next teleport spawn for player" );
        player.sendMessage( "§7- §e/ttt setspawn delete §a{all}§8|§a{id} §8- §eDelete §cALL §espawns from the map you are §ccurrently §ein" );
        player.sendMessage( "§7- §e/mapteleport §a<world>§8|§alist §8- §eTeleports you to the specific map" );
        player.sendMessage( "§7- §cAdditional: §e{id} §8- §eDelete §cALL §espawns with the id you set" );
        player.sendMessage( "§7- §cAdditional: §eall §8- §eDelete §cALL §espawns you have set" );
        player.sendMessage( "§7- §e/ttt deleteID §a<key> <id> §8- §eDelete the specific location of spawn" );
        player.sendMessage( "§7- §cExample: §e/ttt deleteID glass 5" );
        player.sendMessage( "§7- §cAvailable keys: §ebutton, chest, ender_chest, glass, ground, light, spawn, trap" );
    }

    private void writeLocationToConfig( Player player, String key ) {
        TTTPlugin.getPlugin().getConfig().set( key + ".world", player.getWorld().getName() );
        TTTPlugin.getPlugin().getConfig().set( key + ".x", player.getLocation().getX() );
        TTTPlugin.getPlugin().getConfig().set( key + ".y", player.getLocation().getY() );
        TTTPlugin.getPlugin().getConfig().set( key + ".z", player.getLocation().getZ() );
        TTTPlugin.getPlugin().getConfig().set( key + ".yaw", player.getLocation().getYaw() );
        TTTPlugin.getPlugin().getConfig().set( key + ".pitch", player.getLocation().getPitch() );
        TTTPlugin.getPlugin().saveConfig();
    }
}
