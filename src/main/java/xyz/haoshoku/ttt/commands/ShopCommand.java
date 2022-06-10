package xyz.haoshoku.ttt.commands;

import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.state.GameState;
import xyz.haoshoku.ttt.user.TTTUser;
import xyz.haoshoku.ttt.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args ) {

        if ( ! ( sender instanceof Player ) ) {
            sender.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.no_player" ) );
            return true;
        }

        Player player = (Player) sender;

        if ( GameState.getGameState() != GameState.INGAME ) {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.only_works_ingame" ) );
            return true;
        }

        TTTUser user = TTTUser.getUser( player );

        if ( user.isDetective() ) {
            Inventory inventory = Bukkit.createInventory( null, 9,
                    TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.shop_detective_inventory" )
                            .replace( "%points%", String.valueOf( user.getDetectivePoints() ) ) );
            inventory.setItem( 0, new ItemBuilder( Material.BOW ).
                    addEnchantment( Enchantment.ARROW_DAMAGE, 1000 ).setLore( Arrays.asList( "§e3 points" ) ).setDisplayName( "§eOne Shot Bow" ).toItemStack() );
            inventory.setItem( 1, new ItemBuilder( Material.EYE_OF_ENDER ).setLore( Arrays.asList( "§e4 points" ) ).setDisplayName( "§aSwapper" ).toItemStack() );
            player.openInventory( inventory );
        } else if ( user.isTraitor() ) {
            Inventory inventory = Bukkit.createInventory( null, 9,
                    TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.command.shop_traitor_inventory" )
                    .replace( "%points%", String.valueOf( user.getTraitorPoints() ) ) );
            inventory.setItem( 0, new ItemBuilder( Material.MONSTER_EGG, (short) 50 ).setDisplayName( "§aCreeper Arrows" ).setLore( Arrays.asList( "§e2 points" ) ).toItemStack() );
            inventory.setItem( 1, new ItemBuilder( Material.EYE_OF_ENDER ).setLore( Arrays.asList( "§e4 points" ) ).setDisplayName( "§aSwapper" ).toItemStack() );
            inventory.setItem( 2, new ItemBuilder( Material.STAINED_GLASS_PANE, (short) 5 ).setLore( Arrays.asList( "§e3 points" ) ).setDisplayName( "§aInnocent Ticket" ).toItemStack() );
            player.openInventory( inventory );
        } else {
            player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.traitor_detective_requirement" ) );
        }

        return false;
    }

}
