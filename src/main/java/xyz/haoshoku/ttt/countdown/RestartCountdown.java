package xyz.haoshoku.ttt.countdown;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.haoshoku.ttt.TTTPlugin;

public class RestartCountdown extends BukkitRunnable {

    private boolean started;
    @Getter @Setter
    private int restartTime;

    public RestartCountdown() {
        this.restartTime = TTTPlugin.getPlugin().getManager().getSettingManager().getConfiguration().getInt( "settings.restart_time" );
    }

    @Override
    public void run() {
        if ( !started ) {
            started = true;

            for ( Player player : Bukkit.getOnlinePlayers() ) {
                player.setHealth( 20.0 );
                player.setLevel( 0 );
                player.setExp( 0.0F );
                player.setFireTicks( 0 );
                player.setAllowFlight( false );
                player.getInventory().clear();
                player.updateInventory();

                for ( PotionEffect potionEffect : player.getActivePotionEffects() )
                    player.removePotionEffect( potionEffect.getType() );
            }
        }

        switch ( this.restartTime ) {
            case 10: case 5: case 4: case 3: case 2:
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.restart_countdown_message" )
                        .replace( "%seconds%", String.valueOf( this.restartTime ) ) );
                break;

            case 9:
                for ( Location location : TTTPlugin.getPlugin().getManager().getTesterCountdown().getMainGroundList() )
                    location.clone().getBlock().setType( Material.IRON_BLOCK );
                break;

            case 1:
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.restart_countdown_message" )
                        .replace( "%seconds%", String.valueOf( this.restartTime ) ) );
                ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeUTF( "Connect" );
                output.writeUTF( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.lobby_server" ) );

                for ( Player player : Bukkit.getOnlinePlayers() )
                    player.sendPluginMessage( TTTPlugin.getPlugin(), "BungeeCord", output.toByteArray() );
                break;

            case 0:
                Bukkit.shutdown();
                break;
        }

        this.restartTime--;
    }
}
