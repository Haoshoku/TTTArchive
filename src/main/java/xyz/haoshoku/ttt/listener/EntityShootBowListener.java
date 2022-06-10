package xyz.haoshoku.ttt.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class EntityShootBowListener implements Listener {

    @EventHandler
    public void onBow( EntityShootBowEvent event ) {
        if ( event.getEntity() instanceof Player ) {
            Player player = (Player) event.getEntity();

            if ( player.getInventory().contains( Material.MONSTER_EGG ) ) {
                event.getProjectile().setCustomName( "§aCreeper" );
                for ( ItemStack itemStack : player.getInventory().getContents() ) {
                    if ( itemStack != null ) {
                        if ( itemStack.getType() == Material.MONSTER_EGG ) {
                            if ( itemStack.getAmount() != 1 )
                                itemStack.setAmount( itemStack.getAmount() - 1 );
                            else
                                player.getInventory().removeItem( itemStack );
                            break;
                        }
                    }
                }
            }
        }
    }

}
