package xyz.haoshoku.ttt.countdown;

import lombok.Getter;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.haoshoku.ttt.TTTPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TesterCountdown {

    private BukkitTask task;

    @Getter private List<Location> buttonList, chestList, enderChestList, glassList, groundList, mainGroundList, lightList, trapList;

    private int testerTime;

    public TesterCountdown() {
        this.testerTime = 5;
        this.buttonList = new ArrayList<>();
        this.chestList = new ArrayList<>();
        this.enderChestList = new ArrayList<>();
        this.glassList = new ArrayList<>();
        this.groundList = new ArrayList<>();
        this.mainGroundList = new ArrayList<>();
        this.lightList = new ArrayList<>();
        this.trapList = new ArrayList<>();
    }

    public void load( String name ) {
        if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".button" ) != null ) {
            for ( String string : TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".button" ).getKeys( false ) )
                this.buttonList.add( getLocation( name + ".button." + string ) );
        }

        if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".chest" ) != null ) {
            for ( String string : TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".chest" ).getKeys( false ) )
                this.chestList.add( getLocation( name + ".chest." + string ) );
        }

        if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".ender_chest" ) != null ) {
            for ( String string : TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".ender_chest" ).getKeys( false ) )
                this.enderChestList.add( getLocation( name + ".ender_chest." + string ) );
        }

        if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".glass" ) != null ) {
            for ( String string : TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".glass" ).getKeys( false ) )
                this.glassList.add( getLocation( name + ".glass." + string ) );
        }


        if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".ground" ) != null ) {
            for ( String string : TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".ground" ).getKeys( false ) ) {
                Location location = getLocation( name + ".ground." + string );
                this.mainGroundList.add( location );
                this.groundList.add( location );
                this.groundList.add( location.clone().add( 0, 1, 0 ) );
                this.groundList.add( location.clone().add( 0, 2, 0 ) );
            }
        }


        if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".light" ) != null ) {
            for ( String string : TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".light" ).getKeys( false ) )
                this.lightList.add( getLocation( name + ".light." + string ) );
        }

        if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".trap" ) != null ) {
            for ( String string : TTTPlugin.getPlugin().getConfig().getConfigurationSection( name + ".trap" ).getKeys( false ) )
                this.trapList.add( getLocation( name + ".trap." + string ) );
        }

    }

    public void setBlocks() {
        for ( Location location : this.glassList ) {
            //location.clone().subtract( 0, 1, 0 ).getBlock().setType( Material.GLASS );
            location.clone().getBlock().setType( Material.AIR );
        }

        for ( Location location : this.chestList )
            location.clone().getBlock().setType( Material.CHEST );

        for ( Location location : this.enderChestList )
            location.clone().getBlock().setType( Material.ENDER_CHEST );

        for ( Location location : this.mainGroundList )
            location.clone().getBlock().setType( Material.IRON_BLOCK );

        for ( Location location : this.lightList )
            location.clone().getBlock().setType( Material.REDSTONE_LAMP_OFF );
    }

    private Location getLocation( String name ) {
        World world = Bukkit.getWorld( TTTPlugin.getPlugin().getConfig().getString( name + ".world" ) );
        double x = TTTPlugin.getPlugin().getConfig().getDouble( name + ".x" );
        double y = TTTPlugin.getPlugin().getConfig().getDouble( name + ".y" );
        double z = TTTPlugin.getPlugin().getConfig().getDouble( name + ".z" );
        return new Location( world, x, y, z );
    }

    public void executeTest( Player player ) {
        TTTUser user = TTTUser.getUser( player );
        if ( this.task == null ) {
            this.task = Bukkit.getScheduler().runTaskTimer( TTTPlugin.getPlugin(), () -> {
                if ( this.testerTime == 5 ) {
                    Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.tester_successfully" ).replace( "%player%", player.getName() ) );
                    TTTPlugin.getPlugin().getManager().teleportToLocation( player,
                            TTTPlugin.getPlugin().getManager().getVoting().getWinnerMap().toLowerCase() + ".tester_spawn", false );

                    player.playSound( player.getLocation(), Sound.PISTON_EXTEND, 1, 1 );

                    for ( Location location : this.glassList )
                        location.clone().getBlock().setType( Material.GLASS );

                    for ( TTTUser userLoop : TTTUser.getUsers() ) {
                        if ( userLoop.getPlayer() == player ) continue;

                        for ( Location location : this.groundList ) {
                            if ( location.getBlockX() == userLoop.getPlayer().getLocation().getBlockX()
                                    && location.getBlockY() == userLoop.getPlayer().getLocation().getBlockY()
                                    && location.getBlockZ() == userLoop.getPlayer().getLocation().getBlockZ() ) {
                                if ( userLoop.isAlive() ) {
                                    TTTPlugin.getPlugin().getManager().teleportToLocation( userLoop.getPlayer(),
                                            TTTPlugin.getPlugin().getManager().getVoting().getWinnerMap().toLowerCase() + ".tester_teleport_spawn", false );
                                }
                            }
                        }
                    }
                }

                if ( this.testerTime == 0 ) {
                    this.testerTime = 6;

                    player.playSound( player.getLocation(), Sound.NOTE_PLING, 1, 1 );

                    for ( Location location : this.glassList )
                        location.clone().getBlock().setType( Material.AIR );

                    int random = new Random().nextInt( 101 );

                    if ( random > 75 )
                        user.setInnocentTicket( false );

                    if ( user.isInnocent() || user.isInnocentTicket() ) {
                        user.setInnocentTicket( false );
                        for ( Location location : this.lightList ) {
                            location.clone().getBlock().setType( Material.STAINED_CLAY );
                            location.clone().getBlock().setData( (byte) 13 );
                        }
                    } else if ( user.isTraitor() ){
                        for ( Location location : this.lightList ) {
                            location.clone().getBlock().setType( Material.STAINED_CLAY );
                            location.clone().getBlock().setData( (byte) 14 );
                        }
                    }

                    this.task.cancel();

                    Bukkit.getScheduler().runTaskLater( TTTPlugin.getPlugin(), () -> {
                        for ( Location location : this.lightList )
                            location.clone().getBlock().setType( Material.REDSTONE_LAMP_OFF );

                        this.task = null;
                    }, 60L );

                }

                this.testerTime--;
            }, 0L, 20L );
        }
    }
}
