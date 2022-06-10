package xyz.haoshoku.ttt.manager;

import lombok.Getter;
import net.haoshoku.nick.NickPlugin;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.countdown.*;
import xyz.haoshoku.ttt.manager.dbmanager.NickDatabase;
import xyz.haoshoku.ttt.manager.dbmanager.TTTDatabase;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Getter
public class Manager {

    private TTTDatabase database;
    private NickDatabase nickDatabase;

    private Inventory spectatorInventory;

    private FileManager messageManager;
    private FileManager settingManager;
    private WebsiteManager websiteManager;

    private MapVoting voting;

    private LobbyCountdown lobbyCountdown;
    private GracePeriodCountdown gracePeriodCountdown;
    private IngameCountdown ingameCountdown;
    private TesterCountdown testerCountdown;

    public Manager() {
        this.settingManager = new FileManager( "settings.yml" );
        if ( this.settingManager.getConfiguration().getString( "settings.language" ) != null ) {
            if ( this.settingManager.getConfiguration().getString( "settings.language" ).equalsIgnoreCase( "de" ) )
                this.messageManager = new FileManager( "messages_de.yml" );
            else
                this.messageManager = new FileManager( "messages_en.yml" );
        }


        this.websiteManager = new WebsiteManager();
        this.voting = new MapVoting();
        this.lobbyCountdown = new LobbyCountdown();
        this.gracePeriodCountdown = new GracePeriodCountdown();
        this.ingameCountdown = new IngameCountdown();
        this.testerCountdown = new TesterCountdown();
    }

    public void connectToDatabase() {
        if ( this.settingManager.getConfiguration().getBoolean( "settings.mysql.use_mysql" ) ) {
            this.database = new TTTDatabase( this.settingManager.getConfiguration().getString( "settings.mysql.host" ), this.settingManager.getConfiguration().getString( "settings.mysql.port" ),
                    this.settingManager.getConfiguration().getString( "settings.mysql.database" ), this.settingManager.getConfiguration().getString( "settings.mysql.username" ), this.settingManager.getConfiguration().getString( "settings.mysql.password" ), 1 );
            this.database.connect();
        }

        if ( this.settingManager.getConfiguration().getBoolean( "settings.nick.mysql.use_mysql" ) ) {
            this.nickDatabase = new NickDatabase( this.settingManager.getConfiguration().getString( "settings.nick.mysql.host" ), this.settingManager.getConfiguration().getString( "settings.nick.mysql.port" ),
                    this.settingManager.getConfiguration().getString( "settings.nick.mysql.database" ), this.settingManager.getConfiguration().getString( "settings.nick.mysql.username" ), this.settingManager.getConfiguration().getString( "settings.nick.mysql.password" ), 2 );
            this.nickDatabase.connect();
        }
    }

    public void applyAllCountdown() {
        this.lobbyCountdown.setLobbyTime( this.settingManager.getConfiguration().getInt( "settings.lobby_time" ) );
        this.gracePeriodCountdown.setPeriodTime( this.settingManager.getConfiguration().getInt( "settings.grace_period_time" ) );
        this.ingameCountdown.setIngameTime( this.settingManager.getConfiguration().getInt( "settings.ingame_time" ) );

        if ( this.settingManager.getConfiguration().getInt( "settings.min_players" ) < 3 )
            this.lobbyCountdown.setMinPlayers( 3 );
        else
            this.lobbyCountdown.setMinPlayers( this.settingManager.getConfiguration().getInt( "settings.min_players" ) );

        this.lobbyCountdown.runTaskTimer( TTTPlugin.getPlugin(), 20L, 20L );
    }

    public void calculateSpectatorInventory() {
        this.spectatorInventory = Bukkit.createInventory( null, 27, this.messageManager.getColoredValueFromKey( "ttt.items.spectator_compass.name" ) );
        int playerCount = 0;
        for ( TTTUser users : TTTUser.getUsers() ) {
            if ( users.isAlive() ) {
                this.spectatorInventory.setItem( playerCount, users.getSkull() );
                playerCount++;
            }
        }
    }

    public void teleportToLocation( Player player, String name, boolean scheduler ) {
        if ( !TTTPlugin.getPlugin().getManager().getWebsiteManager().isEnabled() ) {
            Bukkit.broadcastMessage( TTTPlugin.getPlugin().getPrefix() + " §cPlugin is disabled" );
            return;
        }
        String finalName = name.toLowerCase();
        if ( TTTPlugin.getPlugin().getConfig().getString( finalName + ".world" ) != null ) {
            if ( scheduler )
                Bukkit.getScheduler().runTaskLater( TTTPlugin.getPlugin(), () -> executeTeleport( player, finalName ), 3L );
            else
                executeTeleport( player, finalName );
        } else {
            Bukkit.broadcastMessage( TTTPlugin.getPlugin().getPrefix() + " §cError: Spawn §e" + name + " §cdoes not exist" );
            if ( name.equalsIgnoreCase( "lobby" ) ) {
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getPrefix() + " §cLobby map should be the default world which is on server.properties (default: §e\"world\"§c)" );
                Bukkit.broadcastMessage( TTTPlugin.getPlugin().getPrefix() + " §cDo not add the lobby in settings.yml (category: maps)" );
            }
        }
    }

    public Location getConfigLocation( String name ) {
        if ( TTTPlugin.getPlugin().getConfig().getString( name + ".world" ) == null )
            return null;
        World world = Bukkit.getWorld( TTTPlugin.getPlugin().getConfig().getString( name + ".world" ) );
        double x = TTTPlugin.getPlugin().getConfig().getDouble( name + ".x" );
        double y = TTTPlugin.getPlugin().getConfig().getDouble( name + ".y" );
        double z = TTTPlugin.getPlugin().getConfig().getDouble( name + ".z" );
        double yaw = TTTPlugin.getPlugin().getConfig().getDouble( name + ".yaw" );
        double pitch = TTTPlugin.getPlugin().getConfig().getDouble( name + ".pitch" );

        Location location = new Location( world, x, y, z, (float) yaw, (float) pitch );
        return location;
    }

    private void executeTeleport( Player player, String name ) {
        player.teleport( this.getConfigLocation( name ) );
    }

    public void checkGameState() {
        if ( !TTTPlugin.getPlugin().getManager().getWebsiteManager().isEnabled() ) {
            Bukkit.broadcastMessage( TTTPlugin.getPlugin().getPrefix() + " §cPlugin is disabled" );
            return;
        }
        if ( GameState.getGameState() == GameState.RESTARTING ) return;
        if ( TTTUser.getInnoDetectiveSize() > 0 && TTTUser.getTraitorSize() <= 0 ) {
            Bukkit.getScheduler().cancelAllTasks();
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                player.setAllowFlight( false );
                player.spigot().respawn();
                TTTPlugin.getPlugin().getManager().teleportToLocation( player, "lobby", true );
            }

            refreshPlayerScoreboardAtEnd( true );
            displayAllPlayers();
            GameState.setGameState( GameState.RESTARTING );
            new RestartCountdown().runTaskTimer( TTTPlugin.getPlugin(), 20L, 20L );
            Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.innocent_win" ) );
            sendTraitorList();

        } else if ( TTTUser.getInnoDetectiveSize() <= 0 && TTTUser.getTraitorSize() > 0 ) {
            Bukkit.getScheduler().cancelAllTasks();

            GameState.setGameState( GameState.RESTARTING );
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                player.setAllowFlight( false );
                player.spigot().respawn();
                TTTPlugin.getPlugin().getManager().teleportToLocation( player, "lobby", true );
            }


            refreshPlayerScoreboardAtEnd( true );
            displayAllPlayers();
            Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.traitor_win" ) );
            sendTraitorList();
            new RestartCountdown().runTaskTimer( TTTPlugin.getPlugin(), 20L, 20L );
        } else if ( TTTUser.getInnoDetectiveSize() <= 0 && TTTUser.getTraitorSize() <= 0 ) {

            Bukkit.getScheduler().cancelAllTasks();
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                player.setAllowFlight( false );
                player.spigot().respawn();
                TTTPlugin.getPlugin().getManager().teleportToLocation( player, "lobby", true );
            }

            refreshPlayerScoreboardAtEnd( true );
            displayAllPlayers();
            GameState.setGameState( GameState.RESTARTING );
            Bukkit.getScheduler().cancelAllTasks();
            Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.nobody_wins" ) );
            sendTraitorList();
            new RestartCountdown().runTaskTimer( TTTPlugin.getPlugin(), 20L, 20L );
        }
    }

    public void sendTraitorList() {
        for ( TTTUser user : TTTUser.getUsers() ) {
            if ( user.isAlive() )
                TTTPlugin.getPlugin().getManager().getDatabase().addStats( user.getPlayer().getUniqueId().toString(), "wins", 1 );
        }
        Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.traitors_are_message" ) );

        Bukkit.broadcastMessage( TTTPlugin.getPlugin().getPrefix() + " " + getTraitorList() );
    }

    public String getTraitorList() {
        StringBuilder builder = new StringBuilder();
        for ( String string : TTTUser.getTraitorList() )
            builder.append( "§4" + string + ", " );

        String list = builder.toString().substring( 0, builder.toString().length() - 2 );
        return list;
    }

    public void spawnZombie( Player player ) {
        if ( !TTTPlugin.getPlugin().getManager().getWebsiteManager().isEnabled() ) {
            Bukkit.broadcastMessage( TTTPlugin.getPlugin().getPrefix() + " §cPlugin is disabled" );
            return;
        }
        TTTUser user = TTTUser.getUser( player );
        Location location = player.getLocation().clone();
        location.setYaw( 0F );
        location.setPitch( 0F );

        Zombie zombie = (Zombie) player.getWorld().spawnEntity( location, EntityType.ZOMBIE );
        zombie.setBaby( false );
        zombie.setCustomName( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.zombie.custom_name" ) );
        zombie.setCustomNameVisible( true );
        disableMovement( zombie );

        if ( user.isDetective() )

            TTTUser.getZombieOfPlayer().put( zombie, new Object[]{ "§9" + player.getName(), "§9detective", false, user.getSkull() } );
        else if ( user.isTraitor() )
            TTTUser.getZombieOfPlayer().put( zombie, new Object[]{ "§4" + player.getName(), "§4traitor", false, user.getSkull() } );
        else
            TTTUser.getZombieOfPlayer().put( zombie, new Object[]{ "§a" + player.getName(), "§ainnocent", false, user.getSkull() } );
    }

    private void disableMovement( Entity entity ) {
        net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        nms.c( tag );
        tag.setBoolean( "NoAI", true );
        EntityLiving entitys = (EntityLiving) nms;
        entitys.a( tag );
    }

    public void removePlayerFromRoles( Player player ) {
        TTTUser user = TTTUser.getUser( player );
        if ( user.isAlive() )
            TTTUser.setAliveSize( TTTUser.getAliveSize() - 1 );

        if ( user.isInnocent() || user.isDetective() )
            TTTUser.setInnoDetectiveSize( TTTUser.getInnoDetectiveSize() - 1 );

        if ( user.isTraitor() )
            TTTUser.setTraitorSize( TTTUser.getTraitorSize() - 1 );
    }

    public ItemStack getLeatherItemStack( Color color ) {
        ItemStack itemStack = new ItemStack( Material.LEATHER_CHESTPLATE );
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        armorMeta.setColor( color );
        armorMeta.spigot().setUnbreakable( true );
        itemStack.setItemMeta( armorMeta );
        return itemStack;
    }


    public void displayAllPlayers() {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            player.getInventory().clear();
            player.getInventory().setArmorContents( null );

            for ( Player online : Bukkit.getOnlinePlayers() ) {
                player.showPlayer( online );
                online.showPlayer( player );
            }

        }
    }


    public void refreshPlayerScoreboardAtEnd( boolean createNew ) {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            Scoreboard scoreboard;

            if ( player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard() || createNew ) {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard( scoreboard );
            } else
                scoreboard = player.getScoreboard();

            FileManager manager = TTTPlugin.getPlugin().getManager().getSettingManager();

            List<Team> list = new ArrayList<>();

            for ( String string : manager.getConfiguration().getConfigurationSection( "settings.ranks" ).getKeys( false ) )
                list.add( scoreboard.getTeam( string ) != null ? scoreboard.getTeam( string ) : scoreboard.registerNewTeam( string ) );

            for ( Team team : list ) {
                String key = manager.getColoredValueFromKey( "settings.ranks." + team.getName() + ".tab_prefix" );
                if ( key.length() > 16 )
                    key = key.substring( 0, 16 );
                team.setPrefix( key );
            }

            for ( Player online : Bukkit.getOnlinePlayers() ) {
                if ( NickPlugin.getPlugin().getAPI().isNicked( online )) {
                    Team team = scoreboard.getTeam( this.settingManager.getColoredValueFromKey( "settings.nick.use_nick_rank" ) );
                    if ( team != null ) {
                        team.addEntry( online.getName() );
                    } else {
                        Bukkit.broadcastMessage( "§cError: Nicked rank is wrong" );
                    }
                    continue;
                }
                for ( Team team : list ) {
                    if ( online.hasPermission( manager.getColoredValueFromKey( "settings.ranks." + team.getName() + ".permission" ) ) ) {
                        team.addEntry( online.getName() );
                        break;
                    }
                }
            }

            if ( NickPlugin.getPlugin().getAPI().isNicked( player ) ) {
                Team team = scoreboard.getTeam( this.settingManager.getColoredValueFromKey( "settings.nick.use_nick_rank" ) );
                player.setDisplayName( team.getPrefix() + player.getName() );
            } else {
                for ( Team team : list ) {
                    if ( player.hasPermission( manager.getColoredValueFromKey( "settings.ranks." + team.getName() + ".permission" ) ) ) {
                        player.setDisplayName( team.getPrefix() + player.getName() );
                        break;
                    }
                }
            }
        }

    }

    public void apply( Player player ) {
        TTTUser user = TTTUser.getUser( player );
        ItemStack itemStack = new ItemStack( Material.SKULL_ITEM, 1, (short) 3 );
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner( player.getName() );
        skullMeta.setDisplayName( "§a" + player.getName() );
        itemStack.setItemMeta( skullMeta );
        user.setSkull( itemStack );
    }

    public void readDataFromScoreboard( Player player ) {
        TTTUser user = TTTUser.getUser( player );
        if ( GameState.getGameState() == GameState.LOBBY || GameState.getGameState() == GameState.GRACE_PERIOD || GameState.getGameState() == GameState.FORBIDDEN_MOVE ) {
            setData( player, "lobby_stats", "ttt.scoreboard_lobby" );
        } else if ( GameState.getGameState() == GameState.INGAME ) {
            if ( user.isDetective() )
                setData( player, "detective_stats", "ttt.scoreboard_detective" );
            else if ( user.isTraitor() )
                setData( player, "traitor_stats", "ttt.scoreboard_traitor" );
            else if ( user.isInnocent() )
                setData( player, "innocent_stats", "ttt.scoreboard_innocent" );
            else if ( user.isSpectator() ) {
                setData( player, "spectator_stats", "ttt.scoreboard_spectator" );
                Scoreboard scoreboard = player.getScoreboard();

                Team detectiveTeam = (scoreboard.getTeam( "detective" ) != null ? scoreboard.getTeam( "detective" ) : scoreboard.registerNewTeam( "detective" ));
                Team innoTeam = (scoreboard.getTeam( "innocent" ) != null ? scoreboard.getTeam( "innocent" ) : scoreboard.registerNewTeam( "innocent" ));
                Team spectatorTeam = (scoreboard.getTeam( "spectator" ) != null ? scoreboard.getTeam( "spectator" ) : scoreboard.registerNewTeam( "spectator" ));

                detectiveTeam.setPrefix( "§9" );
                innoTeam.setPrefix( "§a" );
                spectatorTeam.setPrefix( "§7" );


                for ( Player online : Bukkit.getOnlinePlayers() ) {
                    TTTUser users = TTTUser.getUser( online );

                    if ( users.isDetective() )
                        detectiveTeam.addEntry( online.getName() );
                    else if ( users.isSpectator() )
                        spectatorTeam.addEntry( online.getName() );
                    else
                        innoTeam.addEntry( online.getName() );
                }
            }
        }

    }


    private void setData( Player player, String objectiveName, String configPath ) {
        List<Team> list = new LinkedList<>();
        Scoreboard scoreboard = player.getScoreboard();

        for ( Team team : scoreboard.getTeams() )
            scoreboard.resetScores( team.getPrefix() );

        Objective objective = scoreboard.getObjective( objectiveName ) != null ? scoreboard.getObjective( objectiveName ) : scoreboard.registerNewObjective( objectiveName, "dummy" );
        objective.setDisplayName( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( configPath + ".display_name" ) );
        objective.setDisplaySlot( DisplaySlot.SIDEBAR );

        for ( String string : TTTPlugin.getPlugin().getManager().getMessageManager().getConfiguration().getConfigurationSection( configPath ).getKeys( false ) ) {
            String key = TTTPlugin.getPlugin().getManager().getMessageManager().getScoreboardLine( player, configPath + "." + string );
            if ( string.equalsIgnoreCase( "display_name" ) ) continue;
            Team team = scoreboard.getTeam( string ) != null ? scoreboard.getTeam( string ) : scoreboard.registerNewTeam( string );
            if ( key.length() > 16 )
                key = key.substring( 0, 16 );
            team.setPrefix( key );
            list.add( team );
        }

        int size = TTTPlugin.getPlugin().getManager().getMessageManager().getConfiguration().getConfigurationSection( configPath ).getKeys( false ).size() - 2;

        for ( Team team : list ) {
            objective.getScore( team.getPrefix() ).setScore( size );
            size--;
        }
    }

    private synchronized String getRandomName() {
        List<String> list = TTTPlugin.getPlugin().getManager().getSettingManager().getConfiguration().getStringList( "settings.nick_random_names" );
        List<String> list2 = new ArrayList<>();

        for ( String string : list ) {
            if ( !NickPlugin.getPlugin().getAPI().nickExists( string ) && Bukkit.getPlayer( string ) == null )
                list2.add( string );
        }

        if ( list2.isEmpty() )
            return null;

        return list2.get( new Random().nextInt( list2.size() ) );
    }

    public synchronized void nick( Player player, boolean joinListener, boolean force ) {
        NickDatabase database = TTTPlugin.getPlugin().getManager().getNickDatabase();
        TTTUser user = TTTUser.getUser( player );
        database.getNickStateAsync( player.getUniqueId().toString(), new Consumer<Boolean>() {
            @Override
            public void accept( Boolean result ) {
                if ( result && !force ) {
                    database.setNickState( player.getUniqueId().toString(), false );
                    NickPlugin.getPlugin().getAPI().unnick( player );
                    NickPlugin.getPlugin().getAPI().resetGameProfileName( player );
                    NickPlugin.getPlugin().getAPI().refreshPlayer( player );
                    user.setAutoNick( false );
                    TTTPlugin.getPlugin().getManager().refreshPlayerScoreboardAtEnd( false );
                    player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.nick_successfully_unnick" ) );
                } else {
                    database.setNickState( player.getUniqueId().toString(), true );

                    String name = getRandomName();

                    if ( name == null ) {
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.nick_no_nick_exists" ) );
                        return;
                    }
                    NickPlugin.getPlugin().getAPI().nick( player, name );
                    NickPlugin.getPlugin().getAPI().setSkin( player, name );
                    NickPlugin.getPlugin().getAPI().setGameProfileName( player, name );
                    NickPlugin.getPlugin().getAPI().refreshPlayer( player );
                    player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.nick_successfully_nick" )
                            .replace( "%name%", name ) );
                    TTTPlugin.getPlugin().getManager().refreshPlayerScoreboardAtEnd( false );
                    if ( joinListener ) {
                        Bukkit.broadcastMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.join_message" ).replace( "%player%", player.getDisplayName() ) );
                    }
                    user.setAutoNick( true );

                }



            }
        } );
    }

}
