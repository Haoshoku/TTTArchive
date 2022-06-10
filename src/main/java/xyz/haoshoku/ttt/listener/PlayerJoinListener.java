package xyz.haoshoku.ttt.listener;

import net.haoshoku.nick.NickPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.manager.FileManager;
import xyz.haoshoku.ttt.user.TTTUser;
import xyz.haoshoku.ttt.util.ItemBuilder;

public class PlayerJoinListener implements Listener {

    private FileManager messageManager;

    public PlayerJoinListener() {
        this.messageManager = TTTPlugin.getPlugin().getManager().getMessageManager();
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );
        TTTPlugin.getPlugin().getManager().apply( player );

        if ( user.isRankup() )
            player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.join_karma_bonus" ) );

        player.getInventory().clear();
        player.getInventory().setArmorContents( null );
        player.setHealth( 20.0 );
        player.setFoodLevel( 20 );
        player.setExp( 0.0F );
        player.setLevel( 0 );
        player.setFireTicks( 0 );
        player.setGameMode( GameMode.SURVIVAL );

        for ( PotionEffect potionEffect : player.getActivePotionEffects() )
            player.removePotionEffect( potionEffect.getType() );

        if ( user.isSpectator() ) {
            event.setJoinMessage( null );
            player.setAllowFlight( true );
            player.spigot().setCollidesWithEntities( false );

            for ( Player online : Bukkit.getOnlinePlayers() )
                online.hidePlayer( player );

            player.getInventory().setItem(
                    this.messageManager.getConfiguration().getInt( "ttt.items.spectator_compass.slot" ),
                    new ItemBuilder( Material.getMaterial( this.messageManager.getColoredValueFromKey( "ttt.items.spectator_compass.material" ) ) )
                            .setDisplayName( this.messageManager.getColoredValueFromKey( "ttt.items.spectator_compass.name" ) ).toItemStack() );
        } else {
            event.setJoinMessage( null );
            player.setAllowFlight( false );

            player.getInventory().setItem(
                    this.messageManager.getConfiguration().getInt( "ttt.items.detective_traitor_selection.slot" ),
                    new ItemBuilder( Material.getMaterial( this.messageManager.getColoredValueFromKey( "ttt.items.detective_traitor_selection.material" ) ) )
                            .setDisplayName( this.messageManager.getColoredValueFromKey( "ttt.items.detective_traitor_selection.name" ) ).toItemStack() );

            player.getInventory().setItem(
                    this.messageManager.getConfiguration().getInt( "ttt.items.map_voting_item.slot" ),
                    new ItemBuilder( Material.getMaterial( this.messageManager.getColoredValueFromKey( "ttt.items.map_voting_item.material" ) ) )
                            .setDisplayName( this.messageManager.getColoredValueFromKey( "ttt.items.map_voting_item.name" ) ).toItemStack() );

            TTTPlugin.getPlugin().getManager().refreshPlayerScoreboardAtEnd( false );

            if ( user.isAutoNick() && player.hasPermission( "ttt.nick" ) && TTTPlugin.getPlugin().getManager().getSettingManager().getConfiguration().getBoolean( "settings.auto_nick" ) ) {
                Bukkit.getScheduler().runTaskLater( NickPlugin.getPlugin(), () -> {
                    TTTPlugin.getPlugin().getManager().nick( player, true, true );
                }, 3L );
            } else {
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.join_message" ).replace( "%player%", player.getDisplayName() ) );
            }
        }

        Bukkit.getOnlinePlayers().forEach( online -> TTTPlugin.getPlugin().getManager().readDataFromScoreboard( online ) );


        player.getInventory().setItem(
                this.messageManager.getConfiguration().getInt( "ttt.items.back_to_hub.slot" ),
                new ItemBuilder( Material.getMaterial( this.messageManager.getColoredValueFromKey( "ttt.items.back_to_hub.material" ) ) )
                        .setDisplayName( this.messageManager.getColoredValueFromKey( "ttt.items.back_to_hub.name" ) ).toItemStack() );

        player.updateInventory();
        TTTPlugin.getPlugin().getManager().teleportToLocation( player, "lobby", true );
    }


}
