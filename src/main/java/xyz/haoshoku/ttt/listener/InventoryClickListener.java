package xyz.haoshoku.ttt.listener;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.manager.FileManager;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import xyz.haoshoku.ttt.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private FileManager messageManager;

    public InventoryClickListener() {
        this.messageManager = TTTPlugin.getPlugin().getManager().getMessageManager();
    }

    @EventHandler
    public void onClick( InventoryClickEvent event ) {
        Player player = (Player) event.getWhoClicked();
        TTTUser user = TTTUser.getUser( player );

        if ( event.getInventory() != null ) {
            if ( event.getInventory().getType() == InventoryType.CRAFTING ) {
                if ( user.isBuildState() ) return;
                if ( GameState.getGameState() == GameState.LOBBY || user.isSpectator() ) {
                    event.setCancelled( true );
                    return;
                }

                if ( event.getRawSlot() == 6 )
                    event.setCancelled( true );

            }

            if ( event.getInventory().getName().equalsIgnoreCase( this.messageManager.getColoredValueFromKey( "ttt.items.spectator_compass.name" ) ) ) {
                event.setCancelled( true );
                if ( event.getCurrentItem() != null ) {
                    if ( event.getCurrentItem().hasItemMeta() ) {
                        String name = event.getCurrentItem().getItemMeta().getDisplayName().substring( 2 );
                        Player teleportPlayer = Bukkit.getPlayer( name );

                        player.closeInventory();
                        if ( teleportPlayer == null ) {
                            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.compass_player_not_online" ) );
                            return;
                        }

                        if ( !TTTUser.getUser( teleportPlayer ).isAlive() ) {
                            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.compass_player_dead" ) );
                            return;
                        }

                        player.teleport( teleportPlayer );
                    }
                }
            }
            if ( event.getCurrentItem() != null && event.getInventory().getName().equalsIgnoreCase(
                    TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.shop_detective_inventory" )
                    .replace( "%points%", String.valueOf( user.getDetectivePoints() ) ) ) ) {
                event.setCancelled( true );

                if ( event.getCurrentItem().getType() == Material.BOW ) {
                    if ( user.getDetectivePoints() < 3 ) {
                        player.closeInventory();
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.shop_not_enough_points" ) );
                        return;
                    }
                    user.setDetectivePoints( user.getDetectivePoints() - 3 );
                    ItemStack bow = new ItemBuilder( Material.BOW ).setDisplayName( "§eOne Shot Bow" ).addEnchantment( Enchantment.ARROW_DAMAGE, 1000 ).toItemStack();
                    bow.setDurability( (short) 384 );
                    player.getInventory().addItem( new ItemStack( Material.ARROW ) );
                    player.getInventory().addItem( bow );
                    player.closeInventory();
                }

                if ( event.getCurrentItem().getType() == Material.EYE_OF_ENDER ) {
                    if ( user.getDetectivePoints() < 4 ) {
                        player.closeInventory();
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.shop_not_enough_points" ) );
                        return;
                    }
                    player.getInventory().addItem( event.getCurrentItem().clone() );
                    player.closeInventory();
                }

                TTTPlugin.getPlugin().getManager().readDataFromScoreboard( player );

            }

            if ( event.getCurrentItem() != null && event.getInventory().getName().equalsIgnoreCase( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.shop_traitor_inventory" )
                    .replace( "%points%", String.valueOf( user.getTraitorPoints() ) ) ) ) {
                event.setCancelled( true );

                switch ( event.getCurrentItem().getType() ) {
                    case MONSTER_EGG:
                        if ( user.getTraitorPoints() < 2 ) {
                            player.closeInventory();
                            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.shop_not_enough_points" ) );
                            return;
                        }

                        user.setTraitorPoints( user.getTraitorPoints() - 2 );

                        ItemStack itemStack = event.getCurrentItem().clone();
                        itemStack.setAmount( 3 );
                        player.getInventory().addItem( itemStack );
                        player.closeInventory();
                        break;

                    case EYE_OF_ENDER:
                        if ( user.getTraitorPoints() < 4 ) {
                            player.closeInventory();
                            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.shop_not_enough_points" ) );
                            return;
                        }
                        user.setTraitorPoints( user.getTraitorPoints() - 4 );
                        player.getInventory().addItem( event.getCurrentItem().clone() );
                        player.closeInventory();
                        break;

                    case STAINED_GLASS_PANE:
                        if ( user.isInnocentTicket() ) {
                            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.innocent_ticket_message" ) );
                            return;
                        }
                        if ( user.getTraitorPoints() < 3 ) {
                            player.closeInventory();
                            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.shop_not_enough_points" ) );
                            return;
                        }

                        user.setInnocentTicket( true );
                        user.setTraitorPoints( user.getTraitorPoints() - 3 );
                        player.getInventory().addItem( event.getCurrentItem().clone() );
                        player.closeInventory();

                        break;
                }

                TTTPlugin.getPlugin().getManager().readDataFromScoreboard( player );
            }
        }
        
        if ( event.getInventory().getName() != null ) {
            if ( event.getInventory().getName().equalsIgnoreCase( this.messageManager.getColoredValueFromKey( "ttt.items.map_voting_item.name" )) ) {
                event.setCancelled( true );

                if ( event.getCurrentItem() != null ) {
                    if ( event.getCurrentItem().getType() == Material.MAP ) {
                        if ( event.getCurrentItem().hasItemMeta() ) {
                            String name = event.getCurrentItem().getItemMeta().getDisplayName().substring( 2 );
                            TTTPlugin.getPlugin().getManager().getVoting().addPlayerVote( player, name );
                            player.closeInventory();
                        }
                    }
                }
            }
            if ( event.getInventory().getName().equalsIgnoreCase( this.messageManager.getColoredValueFromKey( "ttt.items.detective_traitor_selection.name" ) ) ) {
                event.setCancelled( true );

                if ( event.getRawSlot() == 2 ) {
                    if ( user.isUsingDetectivePass() || user.isUsingTraitorPass() ) {
                        player.closeInventory();
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.pass_already_used" ) );
                        return;
                    }
                    if ( TTTUser.getGlobalDetectivePassUsed() >= 2 ) {
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.pass_too_many_used" ) );
                        player.closeInventory();
                        return;
                    }

                    if ( user.getDetectivePasses() <= 0 ) {
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.pass_not_enough_detective_passes" ) );
                        player.closeInventory();
                        return;
                    }

                    TTTUser.setGlobalDetectivePassUsed( TTTUser.getGlobalDetectivePassUsed() + 1 );
                    user.setUsingDetectivePass( true );
                    player.closeInventory();
                    player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.pass_detective_done" ) );
                }

                if ( event.getRawSlot() == 6 ) {
                    if ( user.isUsingDetectivePass() || user.isUsingTraitorPass() ) {
                        player.closeInventory();
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.pass_already_used" ) );
                        return;
                    }
                    if ( TTTUser.getGlobalTraitorPassUsed() == 2 ) {
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.pass_too_many_used" ) );
                        player.closeInventory();
                        return;
                    }

                    if ( user.getTraitorPasses() <= 0 ) {
                        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.pass_not_enough_traitor_passes" ) );
                        player.closeInventory();
                        return;
                    }

                    TTTUser.setGlobalTraitorPassUsed( TTTUser.getGlobalTraitorPassUsed() + 1 );
                    user.setUsingTraitorPass( true );
                    player.closeInventory();
                    player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.pass_traitor_done" ) );
                }
            }
        }
    }

}


