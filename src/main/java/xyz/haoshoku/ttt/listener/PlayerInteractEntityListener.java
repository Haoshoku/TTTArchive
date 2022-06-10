package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import xyz.haoshoku.ttt.TTTPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerInteractEntityListener implements Listener {

    @EventHandler
    public void onInteract( PlayerInteractEntityEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );

        if ( !user.isBuildState() ) {
            if ( event.getRightClicked() instanceof Zombie ) {
                Zombie zombie = (Zombie) event.getRightClicked();

                if ( GameState.getGameState() != GameState.INGAME ) {
                    player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.only_works_ingame" ) );
                    return;
                }

                if ( user.isSpectator() )
                    return;

                if ( TTTUser.getZombieOfPlayer().containsKey( zombie ) ) {
                    Object[] data = TTTUser.getZombieOfPlayer().get( zombie );
                    boolean identificated = (boolean) data[2];
                    if ( !identificated && user.isDetective() && player.getItemInHand() != null && player.getItemInHand().getType() == Material.STICK ) {
                        zombie.setCustomName( (String) data[0] );
                        zombie.setCustomNameVisible( true );

                        zombie.getEquipment().setHelmet( (ItemStack) data[3] );

                        String name = TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.zombie.corpse_found" )
                                .replace( "%player%", String.valueOf( data[0] )) .replace( "%role%", String.valueOf( data[1] ) );

                        Bukkit.broadcastMessage( name );

                        TTTUser.getZombieOfPlayer().put( zombie, new Object[]{ data[0], data[1], true, user.getSkull() } );
                        user.setDetectivePoints( user.getDetectivePoints() + 1 );
                        player.getWorld().playSound( player.getLocation(), Sound.LEVEL_UP, 1, 1 );

                        Scoreboard scoreboard = player.getScoreboard();
                        Objective objective = scoreboard.getObjective( "obj" );
                        Team team = scoreboard.getTeam( "points" );


                        scoreboard.resetScores( team.getPrefix() );
                        team.setPrefix( "§e" + user.getTraitorPoints() );
                        objective.getScore( team.getPrefix() ).setScore( 1 );

                    } else if ( identificated ) {
                        String name = TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.zombie.corpse_already_found" )
                                .replace( "%player%", String.valueOf( data[0] ) ).replace( "%role%", String.valueOf( data[1] ) );

                        player.sendMessage( name );
                    } else {
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.zombie.corpse_not_analysed" ) );
                    }

                }
            } else
                event.setCancelled( true );
        }

    }

}
