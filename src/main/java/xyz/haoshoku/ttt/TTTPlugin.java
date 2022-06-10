package xyz.haoshoku.ttt;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.haoshoku.ttt.commands.*;
import xyz.haoshoku.ttt.listener.*;
import xyz.haoshoku.ttt.manager.ListenerHandler;
import xyz.haoshoku.ttt.manager.Manager;
import xyz.haoshoku.ttt.state.GameState;

import java.io.File;
import java.io.IOException;

public class TTTPlugin extends JavaPlugin {

    @Getter
    private static TTTPlugin plugin;

    @Getter
    private Manager manager;

    @Getter
    @Setter
    private String prefix;
    private ListenerHandler handler;

    @Override
    public void onLoad() {
        TTTPlugin.getPlugin( TTTPlugin.class );
        this.saveResource( "messages_en.yml", false );
        this.saveResource( "messages_de.yml", false );
        this.saveResource( "settings.yml", false );
    }

    @Override
    public void onEnable() {
        plugin = this;
        TTTPlugin.getPlugin( TTTPlugin.class );

        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[3];

        if ( !version.equalsIgnoreCase( "v1_8_R3" ) ) {
            Bukkit.getConsoleSender().sendMessage( "§4We do not see a reason to support TTT for higher minecraft versions" );
            Bukkit.getConsoleSender().sendMessage( "§4Server must be §e1.8.3 §4- §e1.8.8" );
            Bukkit.getConsoleSender().sendMessage( "§4For further information, take a look at §ehttps://haoshoku.xyz/go/discord" );
            Bukkit.getPluginManager().disablePlugin( this );
            return;
        }

        if ( Bukkit.getPluginManager().getPlugin( "Multiverse-Core" ) != null )
            Bukkit.getPluginManager().disablePlugin( Bukkit.getPluginManager().getPlugin( "Multiverse-Core" ) );

        for ( World world : Bukkit.getWorlds() ) {
            if ( world.getEnvironment() == World.Environment.NORMAL ) {
                File file = new File( this.getServer().getWorldContainer().getAbsolutePath() + "/" + world.getName() + "/playerdata" );
                File file2 = new File( this.getServer().getWorldContainer().getAbsolutePath() + "/" + world.getName() + "/stats" );
                try {
                    FileUtils.deleteDirectory( file );
                    FileUtils.deleteDirectory( file2 );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        this.manager = new Manager();
        this.manager.getVoting().registerMaps();
        this.manager.applyAllCountdown();
        this.manager.connectToDatabase();

        Bukkit.getMessenger().registerOutgoingPluginChannel( this, "BungeeCord" );

        for ( World world : Bukkit.getWorlds() ) {
            world.setDifficulty( Difficulty.PEACEFUL );
            world.setGameRuleValue( "mobGriefing", "false" );
            world.setGameRuleValue( "doDaylightCycle", "false" );
            world.setGameRuleValue( "doFireTick", "false" );
            world.setWeatherDuration( 0 );
            world.setThunderDuration( 0 );
            world.setStorm( false );
            world.setThundering( false );
            world.setFullTime( 6000L );
            world.setTime( 6000L );


            if ( this.getConfig().getConfigurationSection( "spawns." + world.getName().toLowerCase() ) != null ) {
                int size = this.getConfig().getConfigurationSection( "spawns." + world.getName().toLowerCase() ).getKeys( false ).size();
                for ( int i = 1; i < size + 1; i++ ) {
                    world.loadChunk( world.getChunkAt( TTTPlugin.getPlugin().getManager().getConfigLocation( "spawns." + world.getName().toLowerCase() + "." + i ) ) );
                }
            }


            for ( Entity entity : world.getEntities() ) {
                if ( !(entity instanceof Player) )
                    entity.remove();
            }

            TTTPlugin.getPlugin().getManager().getTesterCountdown().load( world.getName().toLowerCase() );
            TTTPlugin.getPlugin().getManager().getTesterCountdown().setBlocks();
        }

        if ( this.getConfig().getString( "lobby.world" ) != null ) {
            Location location = this.manager.getConfigLocation( "lobby" );
            location.getWorld().loadChunk( location.clone().getChunk() );
        }


        if ( Bukkit.getPluginManager().getPlugin( "ProtocolLib" ) == null ) {
            Bukkit.getConsoleSender().sendMessage( "§4TTT plugin needs ProtocolLib. Please install ProtocolLib first." );
            Bukkit.getPluginManager().disablePlugin( this );
            return;
        }


        this.handler = new ListenerHandler();
        this.handler.start();

        TTTPlugin.getPlugin().getManager().getWebsiteManager().connect();
        this.setPrefix( this.manager.getMessageManager().getColoredValueFromKey( "ttt.prefix" ) );
        this.registerCommand();
        this.registerListener();
        this.startCountdown();
        GameState.setGameState( GameState.LOBBY );
    }

    @Override
    public void onDisable() {
        if ( this.manager != null )
            this.manager.getDatabase().disconnect();
        if ( this.handler != null )
            this.handler.stop();
    }

    private void registerCommand() {
        this.getCommand( "build" ).setExecutor( new BuildCommand() );
        this.getCommand( "detectivechat" ).setExecutor( new DetectiveChatCommand() );
        this.getCommand( "forcereset" ).setExecutor( new ForceResetCommand() );
        this.getCommand( "givepass" ).setExecutor( new GivePassCommand() );
        this.getCommand( "mapteleport" ).setExecutor( new MapTeleportCommand() );
        if ( Bukkit.getPluginManager().getPlugin( "NickAPI" ) != null )
            this.getCommand( "nick" ).setExecutor( new NickCommand() );
        this.getCommand( "resetstats" ).setExecutor( new ResetStatsCommand() );
        this.getCommand( "shop" ).setExecutor( new ShopCommand() );
        this.getCommand( "start" ).setExecutor( new StartCommand() );
        this.getCommand( "stats" ).setExecutor( new StatsCommand() );
        this.getCommand( "tokens" ).setExecutor( new TokensCommand() );
        this.getCommand( "traitorchat" ).setExecutor( new TraitorChatCommand() );
        this.getCommand( "ttt" ).setExecutor( new TTTCommand() );
    }

    private void registerListener() {
        Listener[] listeners = new Listener[]{
                new AsyncPlayerChatListener(), new AsyncPlayerPreLoginListener(), new BlockBreakListener(), new BlockPlaceListener(),
                new CreatureSpawnListener(), new EntityCombustListener(), new EntityDamageByEntityListener(), new EntityDamageListener(),
                new EntityShootBowListener(), new FoodLevelChangeListener(), new InventoryClickListener(), new PlayerCommandPreprocessListener(),
                new PlayerDeathListener(), new PlayerDropItemListener(), new PlayerInteractAtEntityListener(), new PlayerInteractEntityListener(),
                new PlayerInteractListener(), new PlayerJoinListener(), new PlayerKickListener(), new PlayerLoginListener(), new PlayerMoveListener(),
                new PlayerPickupItemListener(), new PlayerQuitListener(), new PlayerRespawnListener(), new PluginDisableListener(),
                new ProjectileHitListener(), new ServerListPingListener(), new WeatherChangeListener() };

        for ( Listener listener : listeners )
            Bukkit.getPluginManager().registerEvents( listener, this );
    }

    private void startCountdown() {
        Bukkit.getScheduler().runTaskTimerAsynchronously( this, () -> {
            this.manager.getDatabase().createTable();
            this.manager.getNickDatabase().createTable();
        }, 20L, 216000 );
    }

}
