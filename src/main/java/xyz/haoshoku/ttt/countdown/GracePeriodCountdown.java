package xyz.haoshoku.ttt.countdown;

import lombok.Getter;
import lombok.Setter;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import xyz.haoshoku.ttt.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GracePeriodCountdown extends BukkitRunnable {

    @Getter @Setter
    private int periodTime;


    @Override
    public void run() {
        switch ( this.periodTime ) {
            case 30: case 15: case 10: case 5: case 4: case 3: case 2: case 1:
                for ( Player player : Bukkit.getOnlinePlayers() )
                    player.playSound( player.getLocation(), Sound.NOTE_PLING, 1, 1 );
                Bukkit.broadcastMessage(
                        TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.grace_period_countdown_message" )
                                .replace( "%seconds%", String.valueOf( this.periodTime ) ) );
                break;

            case 0:
                for ( Player player : Bukkit.getOnlinePlayers() )
                    player.playSound( player.getLocation(), Sound.NOTE_BASS, 1, 1 );
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.grace_period_countdown_end" ) );
                this.cancel();
                GameState.setGameState( GameState.INGAME );
                createRoles();

                TTTPlugin.getPlugin().getManager().getIngameCountdown().runTaskTimer( TTTPlugin.getPlugin(), 0L, 20L );
                break;
        }

        this.periodTime--;
    }


    private void createRoles() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        switch ( onlinePlayers ) {
            case 3:
                calculate( 0, 1 );
                break;

            case 4: case 5:
                calculate( 1, 1 );
                break;

            case 6: case 7:
                calculate( 1, 2 );
                break;

            case 8: case 9:
                calculate( 2, 2 );
                break;

            case 10:
                calculate( 2, 3 );
                break;

            case 11: case 12:
                calculate( 2, 4 );
                break;

            case 13: case 14: case 15:
                calculate( 3, 5 );
                break;

            case 16: case 17: case 18:
                calculate( 4, 6 );
                break;

            case 19: case 20: case 21:
                calculate( 4, 7 );
                break;

            case 22: case 23: case 24:
                calculate( 4, 8 );
                break;
        }
    }

    private void calculate( int detectiveCount, int traitorCount ) {
        List<Player> list = new ArrayList<>();


        int detective = 0, traitor = 0;

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            list.add( player );
            player.updateInventory();
            TTTUser user = TTTUser.getUser( player );

            if ( user.isUsingDetectivePass() ) {
                user.setDetective( true );
                TTTUser.setInnoDetectiveSize( TTTUser.getInnoDetectiveSize() + 1 );
                detective++;
                sendDetectiveMessage( player );
                TTTPlugin.getPlugin().getManager().getDatabase().removeStats( player.getUniqueId().toString(), "detective_pass", 1 );
                list.remove( player );
                player.getInventory().setItem( 8, new ItemBuilder( Material.STICK ).setDisplayName( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.items.ingame.detective_analyser" ) ).toItemStack() );
            }

            if ( user.isUsingTraitorPass() ) {
                user.setTraitor( true );
                TTTUser.setTraitorSize( TTTUser.getTraitorSize() + 1 );
                traitor++;
                TTTUser.getTraitorList().add( player.getName() );
                sendTraitorMessage( player );

                TTTPlugin.getPlugin().getManager().getDatabase().removeStats( player.getUniqueId().toString(), "traitor_pass", 1 );
                list.remove( player );
            }
        }

        while ( traitor < traitorCount ) {
            Player player = list.get( new Random().nextInt( list.size() ) );
            TTTUser user = TTTUser.getUser( player );

            TTTUser.setTraitorSize( TTTUser.getTraitorSize() + 1 );
            TTTUser.getTraitorList().add( player.getName() );

            traitor++;
            user.setTraitor( true );
            list.remove( player );
            sendTraitorMessage( player );
        }

        while ( detective < detectiveCount ) {
            Player player = list.get( new Random().nextInt( list.size() ) );
            TTTUser user = TTTUser.getUser( player );

            TTTUser.setInnoDetectiveSize( TTTUser.getInnoDetectiveSize() + 1 );
            detective++;
            user.setDetective( true );
            list.remove( player );

            sendDetectiveMessage( player );
            player.getInventory().setItem( 8, new ItemBuilder( Material.STICK ).setDisplayName( "§cCorpse analyser" ).toItemStack() );
        }

        for ( Player player : list ) {
            TTTUser user = TTTUser.getUser( player );
            user.setInnocent( true );
            TTTUser.setInnoDetectiveSize( TTTUser.getInnoDetectiveSize() + 1 );
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.role_innocent" ) );
        }

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            TTTUser user = TTTUser.getUser( player );
            TTTUser.getEntityIDMap().put( player.getEntityId(), player );
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Team team = scoreboard.registerNewTeam( "points" );

            if ( user.isDetective() ) {
                player.getInventory().setChestplate( TTTPlugin.getPlugin().getManager().getLeatherItemStack( Color.BLUE ) );

                team.setPrefix( "§e2" );
                user.setDetectivePoints( 2 );

                ItemStack itemStack = user.getSkull().clone();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName( "§9" + player.getName() );
                itemStack.setItemMeta( itemMeta );
                user.setSkull( itemStack );

            } else if ( user.isTraitor() ) {
                player.getInventory().setChestplate( TTTPlugin.getPlugin().getManager().getLeatherItemStack( Color.RED ) );
                player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.traitor_list" )
                        .replace( "%traitor_list%", TTTPlugin.getPlugin().getManager().getTraitorList() ) );
                team.setPrefix( "§e2" );
                user.setTraitorPoints( 2 );
            } else {
                player.getInventory().setChestplate( TTTPlugin.getPlugin().getManager().getLeatherItemStack( Color.GRAY ) );
            }

            player.setScoreboard( scoreboard );
            player.updateInventory();
        }

        for ( Player player : Bukkit.getOnlinePlayers() ) {
            TTTUser playerUser = TTTUser.getUser( player );
            Scoreboard scoreboard = player.getScoreboard();

            Team detectiveTeam = ( scoreboard.getTeam( "detective" ) != null ? scoreboard.getTeam( "detective" ) : scoreboard.registerNewTeam( "detective" ) );
            Team innocentTeam = ( scoreboard.getTeam( "innocent" ) != null ? scoreboard.getTeam( "innocent" ) : scoreboard.registerNewTeam( "innocent" ) );
            Team traitorTeam = ( scoreboard.getTeam( "traitor" ) != null ? scoreboard.getTeam( "traitor" ) : scoreboard.registerNewTeam( "traitor" ) );

            detectiveTeam.setPrefix( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.tab.detective" ) );
            innocentTeam.setPrefix(  TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.tab.innocent" )  );
            traitorTeam.setPrefix(  TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.tab.traitor" )  );

            for ( Player online : Bukkit.getOnlinePlayers() ) {
                TTTUser onlineUser = TTTUser.getUser( online );

                if ( playerUser.isTraitor() ) {
                    if ( onlineUser.isTraitor() )
                        traitorTeam.addEntry( online.getName() );

                    if ( onlineUser.isDetective() )
                        detectiveTeam.addEntry( online.getName() );

                    if ( onlineUser.isInnocent() )
                        innocentTeam.addEntry( online.getName() );

                }

                if ( playerUser.isInnocent() || playerUser.isDetective() ) {
                    if ( onlineUser.isTraitor() )
                        innocentTeam.addEntry( online.getName() );

                    if ( onlineUser.isDetective() )
                        detectiveTeam.addEntry( online.getName() );

                    if ( onlineUser.isInnocent() )
                        innocentTeam.addEntry( online.getName() );
                }
            }

            TTTPlugin.getPlugin().getManager().readDataFromScoreboard( player );
        }

        TTTPlugin.getPlugin().getManager().calculateSpectatorInventory();

    }
    private void sendDetectiveMessage( Player player ) {
        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.role_detective" ) );
    }

    private void sendTraitorMessage( Player player ) {
        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.role_traitor" ) );
    }




}
