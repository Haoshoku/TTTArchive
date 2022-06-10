package xyz.haoshoku.ttt.countdown;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import xyz.haoshoku.ttt.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.user.TTTUser;

import java.lang.reflect.Constructor;

public class MoveCountdown extends BukkitRunnable {

    private int moveTime;

    public MoveCountdown() {
        this.moveTime = 5;
        if ( TTTPlugin.getPlugin().getManager().getLobbyCountdown().getTeleportTask() != null ) {
            TTTPlugin.getPlugin().getManager().getLobbyCountdown().getTeleportTask().cancel();
            TTTPlugin.getPlugin().getManager().getLobbyCountdown().setTeleportTask( null );
        }
    }

    @Override
    public void run() {
        switch ( this.moveTime ) {
            case 5: case 4: case 3: case 2: case 1:
                for ( Player player : Bukkit.getOnlinePlayers() ) {
                    this.send( player, "§4" + this.moveTime, "     ", 0, 20, 20 );
                    player.playSound( player.getLocation(), Sound.NOTE_PLING, 1, 1 );
                }
                break;

            case 0:
                for ( Player player : Bukkit.getOnlinePlayers() ) {
                    this.send( player, "   ", "   ", 0, 0, 0 );
                    player.playSound( player.getLocation(), Sound.LEVEL_UP, 10, 10 );
                }
                this.cancel();
                GameState.setGameState( GameState.GRACE_PERIOD );
                TTTPlugin.getPlugin().getManager().getGracePeriodCountdown().runTaskTimer( TTTPlugin.getPlugin(), 20L, 20L );
                break;
        }

        this.moveTime--;
    }

    private void send(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    fadeInTime, showTime, fadeOutTime);


            Object chatsTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> timingTitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object timingPacket = timingTitleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
            sendPacket(player, timingPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
