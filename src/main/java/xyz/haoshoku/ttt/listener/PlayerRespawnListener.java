package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.manager.FileManager;
import xyz.haoshoku.ttt.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.haoshoku.ttt.TTTPlugin;

public class PlayerRespawnListener implements Listener {

    private FileManager messageManager;

    public PlayerRespawnListener() {
        this.messageManager = TTTPlugin.getPlugin().getManager().getMessageManager();
    }

    @EventHandler
    public void onRespawn( PlayerRespawnEvent event ) {
        Player player = event.getPlayer();

        event.setRespawnLocation( new Location( Bukkit.getWorld( TTTPlugin.getPlugin().getConfig().getString( "lobby.world" ) ),
                TTTPlugin.getPlugin().getConfig().getDouble( "lobby.x" ), TTTPlugin.getPlugin().getConfig().getDouble( "lobby.y" ),
                TTTPlugin.getPlugin().getConfig().getDouble( "lobby.z" ), (float) TTTPlugin.getPlugin().getConfig().getDouble( "lobby.yaw" ),
                (float) TTTPlugin.getPlugin().getConfig().getDouble( "lobby.pitch" ) ) );
        player.spigot().setCollidesWithEntities( false );
        player.setAllowFlight( true );

        for ( Player online : Bukkit.getOnlinePlayers() )
            online.hidePlayer( player );


        player.getInventory().setItem(
                this.messageManager.getConfiguration().getInt( "ttt.items.spectator_compass.slot" ),
                new ItemBuilder( Material.getMaterial( this.messageManager.getColoredValueFromKey( "ttt.items.spectator_compass.material" ) ) )
                        .setDisplayName( this.messageManager.getColoredValueFromKey( "ttt.items.spectator_compass.name" ) ).toItemStack() );
        player.getInventory().setItem(
                this.messageManager.getConfiguration().getInt( "ttt.items.back_to_hub.slot" ),
                new ItemBuilder( Material.getMaterial( this.messageManager.getColoredValueFromKey( "ttt.items.back_to_hub.material" ) ) )
                        .setDisplayName( this.messageManager.getColoredValueFromKey( "ttt.items.back_to_hub.name" ) ).toItemStack() );

        TTTPlugin.getPlugin().getManager().readDataFromScoreboard( player );
    }

}
