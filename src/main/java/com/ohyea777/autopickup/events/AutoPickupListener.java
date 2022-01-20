package com.ohyea777.autopickup.events;


import com.ohyea777.autopickup.AutoPickup;
import com.ohyea777.autopickup.config.AutoPickupConfig;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoPickupListener implements Listener {

    private AutoPickup getPlugin() {
        return AutoPickup.getInstance();
    }

    private AutoPickupConfig getConfig() {
        return getPlugin().getActualConfig();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && event.getPlayer().hasPermission("autopickup.pickup") && getConfig().canPickup(event.getBlock())) {
            List<ItemStack> drops = new ArrayList<ItemStack>();
            ItemStack customDrop = getConfig().getCustomDrop(event.getBlock());

            if (event.getPlayer().hasPermission("autopickup.customdrops") && customDrop == null) drops.addAll(event.getBlock().getDrops(event.getPlayer().getItemInHand()));
            else if (event.getBlock().getDrops(event.getPlayer().getItemInHand()).size() > 0) drops.add(customDrop);

            if (drops.size() <= 0) return;

            if (event.getPlayer().hasPermission("autopickup.fortuneblocks") && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) && getConfig().canFortune(event.getBlock())) {
                for (ItemStack drop : drops) drop.setAmount(1 + drop.getAmount() * new Random().nextInt(event.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
            }

            if (event.getPlayer().getInventory().addItem(drops.toArray(new ItemStack[drops.size()])).size() > 0)
                event.getPlayer().sendMessage(getConfig().getInventoryFull());

            if (getConfig().isSoundEnabled()) event.getBlock().getWorld().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1, 1);

            if (event.getBlock().getState() instanceof InventoryHolder) {
                InventoryHolder inventoryHolder = (InventoryHolder) event.getBlock().getState();
                ItemStack[] contents = inventoryHolder.getInventory().getContents();

                inventoryHolder.getInventory().clear();

                for (ItemStack itemStack : contents) event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation().add(.5, .5, .1), itemStack);
            }

            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        } else {
            event.getPlayer().sendMessage("wut");
        }
    }

}
