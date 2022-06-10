package xyz.haoshoku.ttt.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.manager.FileManager;
import xyz.haoshoku.ttt.manager.MapVoting;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import xyz.haoshoku.ttt.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerInteractListener implements Listener {

    private FileManager messageManager;
    private Random random;
    private Set<Material> materialSet;

    public PlayerInteractListener() {
        this.messageManager = TTTPlugin.getPlugin().getManager().getMessageManager();
        this.random = new Random();
        this.materialSet = new HashSet<>();
        registerBlacklist();
    }

    @EventHandler
    public void onInteract( PlayerInteractEvent event ) {
        Player player = event.getPlayer();
        TTTUser user = TTTUser.getUser( player );

        /*
        Holz-Axt: tester ground setter
        Stein-Axt: Tester light setter
        Gold-Axt: Tester glass Setter
        Iron-Axt: Tester Button Setter
        Diamond-Axe: Traitorfalle
         */

        if ( user.isBuildState() ) {
            if ( event.getItem() != null ) {
                if ( event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null ) {

                    Location location = event.getClickedBlock().getLocation();
                    String worldName = location.getWorld().getName().toLowerCase();

                    int count;

                    switch ( event.getItem().getType().toString() ) {
                        case "STONE_PICKAXE":
                            event.setCancelled( true );
                            if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".chest" ) != null )
                                count = TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".chest" ).getKeys( false ).size();
                            else
                                count = 0;

                            count++;
                            saveLocationToConfig( worldName + ".chest." + count, location );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aChest §anumber §e" + count + " §aon world §e" + worldName + " §ahas been successfully saved" );
                            break;

                        case "IRON_PICKAXE":
                            event.setCancelled( true );
                            if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".ender_chest" ) != null )
                                count = TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".ender_chest" ).getKeys( false ).size();
                            else
                                count = 0;

                            count++;
                            saveLocationToConfig( worldName + ".ender_chest." + count, location );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aEnderChest §anumber §e" + count + " §aon world §e" + worldName + " §ahas been successfully saved" );
                            break;

                        case "WOOD_AXE":
                            event.setCancelled( true );
                            if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".ground" ) != null )
                                count = TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".ground" ).getKeys( false ).size();
                            else
                                count = 0;

                            count++;
                            saveLocationToConfig( worldName + ".ground." + count, location );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aTester §4ground §anumber §e" + count + " §aon world §e" + worldName + " §ahas been successfully saved" );
                            break;

                        case "STONE_AXE":
                            event.setCancelled( true );
                            if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".light" ) != null )
                                count = TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".light" ).getKeys( false ).size();
                            else
                                count = 0;

                            count++;
                            saveLocationToConfig( worldName + ".light." + count, location );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aTester §4light §anumber §e" + count + " §aon world §e" + worldName + " §ahas been successfully saved" );
                            break;

                        case "GOLD_AXE":
                            event.setCancelled( true );
                            if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".glass" ) != null )
                                count = TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".glass" ).getKeys( false ).size();
                            else
                                count = 0;

                            count++;
                            saveLocationToConfig( worldName + ".glass." + count, location );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aTester §4glass §anumber §e" + count + " §aon world §e" + worldName + " §ahas been successfully saved" );
                            break;

                        case "IRON_AXE":
                            event.setCancelled( true );
                            if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".button" ) != null )
                                count = TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".button" ).getKeys( false ).size();
                            else
                                count = 0;

                            count++;

                            saveLocationToConfig( worldName + ".button." + count, location );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aTester §4button §anumber §e" + count + " §aon world §e" + worldName + " §ahas been successfully saved" );
                            break;

                        case "DIAMOND_AXE":
                            event.setCancelled( true );
                            if ( TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".trap" ) != null )
                                count = TTTPlugin.getPlugin().getConfig().getConfigurationSection( worldName + ".trap" ).getKeys( false ).size();
                            else
                                count = 0;

                            count++;

                            saveLocationToConfig( worldName + ".trap." + count, location );
                            player.sendMessage( TTTPlugin.getPlugin().getPrefix() + " §aTester §4trap §anumber §e" + count + " §aon world §e" + worldName + " §ahas been successfully saved" );
                            break;

                    }

                }
            }

            return;
        }


        if ( !user.isBuildState() ) {
            if ( event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR ) {
                if ( event.getItem() != null ) {
                    if ( event.getItem().getType().toString().equalsIgnoreCase( this.messageManager.getColoredValueFromKey( "ttt.items.back_to_hub.material" ) ) ) {
                        event.setCancelled( true );
                        player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.hub_item" ) );
                        ByteArrayDataOutput output = ByteStreams.newDataOutput();
                        output.writeUTF( "Connect" );
                        output.writeUTF( TTTPlugin.getPlugin().getManager().getSettingManager().getColoredValueFromKey( "settings.lobby_server" ) );
                        player.sendPluginMessage( TTTPlugin.getPlugin(), "BungeeCord", output.toByteArray() );
                    }

                    if ( event.getItem().getType().toString().equalsIgnoreCase( this.messageManager.getColoredValueFromKey( "ttt.items.spectator_compass.material" ) ) ) {
                        event.setCancelled( true );
                        if ( user.isSpectator() )
                            player.openInventory( TTTPlugin.getPlugin().getManager().getSpectatorInventory() );
                    }

                    if ( user.isSpectator() ) {
                        event.setCancelled( true );
                        return;
                    }

                    if ( event.getItem().getType().toString().equalsIgnoreCase( "STAINED_GLASS_PANE" ) ) {
                        event.setCancelled( true );
                        if ( user.isInnocentTicket() && user.isTraitor() ) {
                            player.getInventory().remove( Material.STAINED_GLASS_PANE );
                            player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.innocent_ticket_use" ) );
                            return;
                        }
                    }

                    if ( event.getItem().getType().toString().equalsIgnoreCase( this.messageManager.getColoredValueFromKey( "ttt.items.detective_traitor_selection.material" ) ) ) {
                        event.setCancelled( true );
                        Inventory passInventory = Bukkit.createInventory( null, 9, this.messageManager.getColoredValueFromKey( "ttt.items.detective_traitor_selection.name" ) );

                        player.playSound( player.getLocation(), Sound.CHEST_OPEN, 1, 1 );

                        String detectiveMessage = this.messageManager.getColoredValueFromKey( "ttt.opened_items.inventory_detective_pass_left" ).replace( "%pass%", String.valueOf( user.getDetectivePasses() ) );
                        String traitorMessage = this.messageManager.getColoredValueFromKey( "ttt.opened_items.inventory_traitor_pass_left" ).replace( "%pass%", String.valueOf( user.getTraitorPasses() ) );
                        String detectiveDisplayName = this.messageManager.getColoredValueFromKey( "ttt.opened_items.inventory_detective_pass_display_name" );
                        String traitorDisplayName = this.messageManager.getColoredValueFromKey( "ttt.opened_items.inventory_traitor_pass_display_name" );

                        passInventory.setItem( 2, new ItemBuilder( Material.WOOL, (short) 11 ).setDisplayName( detectiveDisplayName )
                                .setLore( Arrays.asList( detectiveMessage ) ).toItemStack() );
                        passInventory.setItem( 6, new ItemBuilder( Material.WOOL, (short) 14 ).setDisplayName( traitorDisplayName )
                                .setLore( Arrays.asList( traitorMessage ) ).toItemStack() );

                        player.openInventory( passInventory );

                    }

                    if ( event.getItem().getType().toString().equalsIgnoreCase( "EYE_OF_ENDER" ) ) {
                        event.setCancelled( true );
                        if ( GameState.getGameState() == GameState.INGAME ) {
                            List<Player> list = new LinkedList<>();

                            for ( TTTUser users : TTTUser.getUsers() ) {
                                if ( users.isAlive() ) {
                                    if ( users.getPlayer() != player )
                                        list.add( users.getPlayer() );
                                }
                            }

                            Player randomPlayer = list.get( new Random().nextInt( list.size() ) );
                            if ( randomPlayer == null ) {
                                player.sendMessage( "§cError: No players available" );
                                return;
                            }
                            Location playerLocation = player.getLocation().clone();
                            Location randomPlayerLocation = randomPlayer.getLocation().clone();

                            player.teleport( randomPlayerLocation );
                            randomPlayer.teleport( playerLocation );
                            for ( ItemStack itemStack : player.getInventory().getContents() ) {
                                if ( itemStack != null ) {
                                    if ( itemStack.getType() == Material.EYE_OF_ENDER ) {
                                        if ( itemStack.getAmount() == 1 ) {
                                            itemStack.setType( Material.AIR );
                                        } else {
                                            itemStack.setAmount( itemStack.getAmount() - 1 );
                                        }
                                        break;
                                    }
                                }
                            }
                            player.updateInventory();
                        }
                    }

                    if ( event.getItem().getType().toString().equalsIgnoreCase( this.messageManager.getColoredValueFromKey( "ttt.items.map_voting_item.material" ) ) ) {
                        event.setCancelled( true );
                        Inventory mapInventory = Bukkit.createInventory( null, 27, this.messageManager.getColoredValueFromKey( "ttt.items.map_voting_item.name" ) );
                        MapVoting voting = TTTPlugin.getPlugin().getManager().getVoting();

                        int count = 0;
                        for ( Map.Entry<String, Integer> entry : voting.getMapVotes().entrySet() ) {
                            String map = entry.getKey();
                            int votes = entry.getValue();

                            String mapVoting = this.messageManager.getColoredValueFromKey( "ttt.opened_items.map_voting" ).replace( "%map%", map ).replace( "%maps%", map );
                            String mapVotingLore = this.messageManager.getColoredValueFromKey( "ttt.opened_items.map_voting_lore" ).replace( "%votes%", String.valueOf( votes ) );

                            mapInventory.setItem( count, new ItemBuilder( Material.getMaterial( this.messageManager.getColoredValueFromKey( "ttt.opened_items.map_voting_material" ) ) ).setDisplayName( mapVoting )
                                    .setLore( Arrays.asList( mapVotingLore ) ).toItemStack() );

                            count++;
                        }

                        player.openInventory( mapInventory );
                    }


                } else {
                    if ( user.isSpectator() ) {
                        event.setCancelled( true );
                        return;
                    }
                }
            }

            if ( event.getClickedBlock() != null ) {
                if ( event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
                    checkItem( player, event );
                    if ( this.materialSet.contains( event.getClickedBlock().getType() ) ) {
                        event.setCancelled( true );
                        return;
                    }

                    if ( GameState.getGameState() != GameState.GRACE_PERIOD && GameState.getGameState() != GameState.INGAME ) {
                        event.setCancelled( true );
                        return;
                    }

                    if ( GameState.getGameState() == GameState.INGAME ) {
                        if ( TTTPlugin.getPlugin().getManager().getTesterCountdown().getButtonList().contains( event.getClickedBlock().getLocation() ) ) {
                            if ( !user.isDetective() && !user.isSpectator() )
                                TTTPlugin.getPlugin().getManager().getTesterCountdown().executeTest( player );
                            else
                                player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.tester_not_useable" ) );
                        }

                        if ( TTTPlugin.getPlugin().getManager().getTesterCountdown().getTrapList().contains( event.getClickedBlock().getLocation() ) ) {
                            if ( user.isTraitor() ) {
                                if ( TTTUser.isTraitorTrapUsed() ) {
                                    player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.trap_already_used" ) );
                                    return;
                                }

                                Bukkit.broadcastMessage( this.messageManager.getColoredValueFromKey( "ttt.trap_executed" ) );

                                TTTUser.setTraitorTrapUsed( true );

                                for ( Location location : TTTPlugin.getPlugin().getManager().getTesterCountdown().getMainGroundList() )
                                    location.clone().getBlock().setType( Material.AIR );


                                Bukkit.getScheduler().runTaskLater( TTTPlugin.getPlugin(), () -> {
                                    for ( Location location : TTTPlugin.getPlugin().getManager().getTesterCountdown().getMainGroundList() )
                                        location.clone().getBlock().setType( Material.IRON_BLOCK );
                                }, 20L * 5L );

                            } else {
                                player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.trap_only_traitor" ) );
                            }
                        }
                    }
                }
            }
        }


        if ( event.getAction() == Action.PHYSICAL ) {
            switch ( player.getLocation().getBlock().getType().toString() ) {
                case "STONE_PLATE":
                case "WOOD_PLATE":
                case "IRON_PLATE":
                case "GOLD_PLATE":
                    break;

                default:
                    event.setCancelled( true );
                    break;
            }

        }
    }


    private void checkItem( Player player, PlayerInteractEvent event ) {
        Inventory inventory = player.getInventory();

        if ( TTTUser.getUser( player ).isSpectator() ) {
            event.setCancelled( true );
            return;
        }

        if ( event.getClickedBlock().getType() == Material.ENDER_CHEST ) {
            event.setCancelled( true );
            if ( player.getInventory().contains( Material.IRON_SWORD ) ) return;
            if ( GameState.getGameState() != GameState.INGAME ) {
                player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.enderchest_only_ingame" ) );
                return;
            }
            event.getClickedBlock().setType( Material.AIR );
            player.getInventory().addItem( new ItemStack( Material.IRON_SWORD ) );
        }

        if ( event.getClickedBlock().getType() == Material.CHEST ) {
            event.setCancelled( true );
            TTTUser user = TTTUser.getUser( player );

            if ( user.getChestOpened() >= TTTPlugin.getPlugin().getManager().getSettingManager().getConfiguration().getInt( "settings.max_chest_open" ) ) {
                player.sendMessage( this.messageManager.getColoredValueFromKey( "ttt.too_many_chest_opened" ) );
                return;
            }

            if ( GameState.getGameState() != GameState.GRACE_PERIOD && GameState.getGameState() != GameState.INGAME )
                return;

            if ( inventory.contains( Material.STONE_SWORD ) && inventory.contains( Material.BOW ) && inventory.contains( Material.WOOD_SWORD ) )
                return;

            event.getClickedBlock().setType( Material.AIR );
            user.setChestOpened( user.getChestOpened() + 1 );
            player.getWorld().playSound( player.getLocation(), Sound.WOOD_CLICK, 1, 1 );

            if ( inventory.contains( Material.STONE_SWORD ) && inventory.contains( Material.BOW ) && !inventory.contains( Material.WOOD_SWORD ) )
                inventory.addItem( new ItemStack( Material.WOOD_SWORD ) );
            else if ( inventory.contains( Material.STONE_SWORD ) && !inventory.contains( Material.BOW ) && inventory.contains( Material.WOOD_SWORD ) ) {
                inventory.addItem( new ItemStack( Material.BOW ) );
                inventory.addItem( new ItemStack( Material.ARROW, 32 ) );
            } else if ( !inventory.contains( Material.STONE_SWORD ) && inventory.contains( Material.BOW ) && inventory.contains( Material.WOOD_SWORD ) )
                inventory.addItem( new ItemStack( Material.STONE_SWORD ) );
            else if ( !inventory.contains( Material.STONE_SWORD ) && !inventory.contains( Material.BOW ) && inventory.contains( Material.WOOD_SWORD ) ) {
                List<Material> list = Arrays.asList( Material.STONE_SWORD, Material.BOW );
                Material material = list.get( this.random.nextInt( list.size() ) );
                inventory.addItem( new ItemStack( material ) );
                if ( material == Material.BOW )
                    player.getInventory().addItem( new ItemStack( Material.ARROW, 32 ) );
            } else if ( !inventory.contains( Material.STONE_SWORD ) && inventory.contains( Material.BOW ) && !inventory.contains( Material.WOOD_SWORD ) ) {
                List<Material> list = Arrays.asList( Material.STONE_SWORD, Material.WOOD_SWORD );
                inventory.addItem( new ItemStack( list.get( new Random().nextInt( list.size() ) ) ) );
            } else if ( inventory.contains( Material.STONE_SWORD ) && !inventory.contains( Material.BOW ) && !inventory.contains( Material.WOOD_SWORD ) ) {
                List<Material> list = Arrays.asList( Material.BOW, Material.WOOD_SWORD );
                Material material = list.get( this.random.nextInt( list.size() ) );
                inventory.addItem( new ItemStack( material ) );
                if ( material == Material.BOW )
                    player.getInventory().addItem( new ItemStack( Material.ARROW, 32 ) );
            } else {
                List<Material> list = Arrays.asList( Material.STONE_SWORD, Material.BOW, Material.WOOD_SWORD );
                Material material = list.get( this.random.nextInt( list.size() ) );
                inventory.addItem( new ItemStack( material ) );
                if ( material == Material.BOW )
                    player.getInventory().addItem( new ItemStack( Material.ARROW, 32 ) );
            }
            player.updateInventory();
        }

    }

    private void registerBlacklist() {
        this.materialSet.add( Material.WORKBENCH );
        this.materialSet.add( Material.ENCHANTMENT_TABLE );
        this.materialSet.add( Material.FURNACE );
        this.materialSet.add( Material.DISPENSER );
        this.materialSet.add( Material.HOPPER );
        this.materialSet.add( Material.ANVIL );
        this.materialSet.add( Material.TRAPPED_CHEST );
    }

    private void saveLocationToConfig( String path, Location location ) {
        TTTPlugin.getPlugin().getConfig().set( path + ".world", location.getWorld().getName() );
        TTTPlugin.getPlugin().getConfig().set( path + ".x", location.getBlockX() );
        TTTPlugin.getPlugin().getConfig().set( path + ".y", location.getBlockY() );
        TTTPlugin.getPlugin().getConfig().set( path + ".z", location.getBlockZ() );
        TTTPlugin.getPlugin().saveConfig();
    }


}
