package xyz.haoshoku.ttt.countdown;

import lombok.Getter;
import lombok.Setter;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class IngameCountdown extends BukkitRunnable {

    @Getter @Setter
    private int ingameTime;

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach( players -> showActionbar( players, "§a" + formatSeconds( this.ingameTime ) ) );
        switch ( this.ingameTime ) {
            case 60:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                Bukkit.getOnlinePlayers().forEach( players -> players.playSound( players.getLocation(), Sound.NOTE_PLING, 1, 1 ) );
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.ingame_countdown_message" ) );
                break;

            case 0:
                Bukkit.getOnlinePlayers().forEach( players -> showActionbar( players, null ) );
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.ingame_countdown_end" ) );
                Bukkit.getScheduler().cancelAllTasks();
                GameState.setGameState( GameState.RESTARTING );
                new RestartCountdown().runTaskTimer( TTTPlugin.getPlugin(), 0L, 20L );
                break;
        }



        this.ingameTime--;
    }

    private void showActionbar( Player player, String text ) {
        PacketPlayOutChat packet = new PacketPlayOutChat( IChatBaseComponent.ChatSerializer.a( "{\"text\":\"" + text + "\"}" ), (byte) 2 );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket( packet );
    }

    private String formatSeconds( int seconds )  {
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format( "%02d:%02d", minutes, seconds );
    }
}
