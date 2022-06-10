package xyz.haoshoku.ttt.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder( Material material ) {
        this( material, (short) 0 );
    }

    public ItemBuilder( Material material, short subID ) {
        if ( material == null )
            this.itemStack = new ItemStack( Material.STONE );
        else
            this.itemStack = new ItemStack( material, 1, subID );
        this.itemMeta = this.itemStack.getItemMeta();
    }


    public ItemBuilder setDisplayName( String displayName ) {
        this.itemMeta.setDisplayName( displayName );
        this.itemStack.setItemMeta( this.itemMeta );
        return this;
    }

    public ItemBuilder setAmount( int amount ) {
        this.itemStack.setAmount( amount );
        return this;
    }

    public ItemBuilder setLore( List<String> lore ) {
        this.itemMeta.setLore( lore );
        this.itemStack.setItemMeta( this.itemMeta );
        return this;
    }

    public ItemBuilder addEnchantment( Enchantment enchantment, int level ) {
        this.itemStack.addUnsafeEnchantment( enchantment, level );
        return this;
    }

    public ItemStack toItemStack() {
        this.itemMeta.addItemFlags( ItemFlag.HIDE_ATTRIBUTES );
        return this.itemStack;
    }

}
