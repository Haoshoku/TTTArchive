package xyz.haoshoku.ttt.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ListenerHandler {

    private FileConfiguration settingsConfig;

    public ListenerHandler() {
        this.settingsConfig = TTTPlugin.getPlugin().getManager().getSettingManager().getConfiguration();
    }

    public void start() {
        ProtocolLibrary.getProtocolManager().addPacketListener( new PacketAdapter( TTTPlugin.getPlugin(), PacketType.Play.Server.ENTITY_EQUIPMENT ) {
            @Override
            public void onPacketSending( PacketEvent event ) {
                if ( GameState.getGameState() == GameState.INGAME ) {
                    Player player = event.getPlayer();
                    TTTUser playerUser = TTTUser.getUser( player );

                    if ( playerUser.isTraitor() )
                        return;

                    int entityID = event.getPacket().getIntegers().read( 0 );
                    int slot = event.getPacket().getIntegers().read( 1 );

                    PacketPlayOutEntityEquipment

                    if ( player.getEntityId() != entityID && slot == 3 ) {
                        if ( TTTUser.getEntityIDMap().containsKey( entityID ) ) {
                            Player entity = TTTUser.getEntityIDMap().get( entityID );
                            TTTUser entityUser = TTTUser.getUser( entity );

                            if ( playerUser.isDetective() || playerUser.isInnocent() || playerUser.isSpectator() ) {
                                if ( entityUser.isDetective() )
                                    event.getPacket().getItemModifier().write( 0, TTTPlugin.getPlugin().getManager().getLeatherItemStack( Color.BLUE ) );
                                else if ( entityUser.isInnocent() || entityUser.isTraitor() )
                                    event.getPacket().getItemModifier().write( 0, TTTPlugin.getPlugin().getManager().getLeatherItemStack( Color.GRAY ) );
                            }
                        }
                    }
                }
            }
        } );

        ProtocolLibrary.getProtocolManager().addPacketListener( new PacketAdapter( TTTPlugin.getPlugin(), PacketType.Play.Client.CUSTOM_PAYLOAD ) {
            @Override
            public void onPacketReceiving( PacketEvent event ) {
                Player player = event.getPlayer();
                TTTUser user = TTTUser.getUser( player );
                String payload = event.getPacket().getStrings().read( 0 );

                if ( payload.equalsIgnoreCase( "MC|BSign" ) || payload.equalsIgnoreCase( "MC|BEdit" ) ) {
                    if ( System.currentTimeMillis() <= user.getPayloadCooldown() ) {
                        event.setCancelled( true );
                        return;
                    }
                    user.setPayloadCooldown( System.currentTimeMillis() + 5000L );
                }
            }
        } );

        ProtocolLibrary.getProtocolManager().addPacketListener( new PacketAdapter( TTTPlugin.getPlugin(), PacketType.Play.Client.POSITION ) {
            @Override
            public void onPacketReceiving( PacketEvent event ) {
                Player player = event.getPlayer();

                if ( settingsConfig.getBoolean( "settings.massive_chunk_loading.enabled" ) ) {
                    int playerX = player.getLocation().getBlockX();
                    int playerY = player.getLocation().getBlockY();
                    int playerZ = player.getLocation().getBlockZ();

                    int packetX = event.getPacket().getDoubles().read( 0 ).intValue();
                    int packetY = event.getPacket().getDoubles().read( 1 ).intValue();
                    int packetZ = event.getPacket().getDoubles().read( 2 ).intValue();

                    int diffX = Math.max( playerX, packetX ) - Math.min( playerX, packetX );
                    int diffY = Math.max( playerY, packetY ) - Math.min( playerY, packetY );
                    int diffZ = Math.max( playerZ, packetZ ) - Math.min( playerZ, packetZ );

                    if ( diffX > settingsConfig.getInt( "settings.massive_chunk_loading.diff_x" )
                            || diffY > settingsConfig.getInt( "settings.massive_chunk_loading.diff_y" )
                            || diffZ > settingsConfig.getInt( "settings.massive_chunk_loading.diff_z" ) )
                        event.setCancelled( true );
                }
            }
        } );

    }

    public void stop() {
        ProtocolLibrary.getProtocolManager().removePacketListeners( TTTPlugin.getPlugin() );
    }

}
