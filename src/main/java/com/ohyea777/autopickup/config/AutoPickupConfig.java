package com.ohyea777.autopickup.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoPickupConfig {

    private String prefix = "&8[&2AutoPickup&8]";
    private String permissionDenied = "%prefix% &cYou do not have permission to do that!";
    private String inventoryFull = "%prefix% &cYour inventory is full!";
    private String reload = "%prefix% &aThe config has been reloaded!";

    private String key = "&a";
    private String splitter = "&8";
    private String value = "&2";

    private boolean soundEnabled = true;

    private boolean fortuneAll = false;

    private boolean whiteList = false;
    private List<WhiteBlackListBlock> whiteListedBlocks = new ArrayList<WhiteBlackListBlock>();

    private List<WhiteBlackListBlock> blackListedBlocks = new ArrayList<WhiteBlackListBlock>();

    private List<WhiteBlackListBlock> fortuneBlocks = Arrays.asList(
            new WhiteBlackListBlock(Material.COAL_ORE, 0),
            new WhiteBlackListBlock(Material.LAPIS_ORE, 0),
            new WhiteBlackListBlock(Material.DIAMOND_ORE, 0),
            new WhiteBlackListBlock(Material.EMERALD_ORE, 0)
    );

    private boolean specificWorlds = false;
    private List<String> worlds = new ArrayList<String>();

    private List<CustomDrop> customDrops = Arrays.asList(
            new CustomDrop(Material.IRON_ORE, Material.IRON_INGOT),
            new CustomDrop(Material.GOLD_ORE, Material.GOLD_INGOT)
    );

    public String getPrefix() {
        return format(prefix);
    }

    public String getPermissionDenied() {
        return formatWithPrefix(permissionDenied);
    }

    public String getInventoryFull() {
        return formatWithPrefix(inventoryFull);
    }

    public String getReload() {
        return formatWithPrefix(reload);
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean canPickup(Block block) {
        if (specificWorlds && !worlds.contains(block.getWorld().getName())) return false;

        if (whiteList) {
            for (WhiteBlackListBlock whiteListed : whiteListedBlocks) if (whiteListed.isWhiteBlackListed(block)) return true;

            return false;
        } else for (WhiteBlackListBlock blackListed : blackListedBlocks) if (blackListed.isWhiteBlackListed(block)) return false;

        return true;
    }

    public boolean canFortune(Block block) {
        if (specificWorlds && !worlds.contains(block.getWorld().getName())) return false;

        if (fortuneAll) return true;
        else for (WhiteBlackListBlock fortuneBlock : fortuneBlocks) if (fortuneBlock.isWhiteBlackListed(block)) return true;

        return false;
    }

    public ItemStack getCustomDrop(Block block) {
        for (CustomDrop drop : customDrops) if (drop.isWhiteBlackListed(block)) return drop.getItem();

        return null;
    }

    private String format(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public String formatWithPrefix(String string) {
        return format(string).replace("%prefix%", getPrefix()).replace("%k%", format(key)).replace("%v%", format(value)).replace("%s%", format(splitter));
    }

    public class WhiteBlackListBlock {

        private String world = "all-worlds";
        private String material = Material.AIR.name();
        private short damage = 0;

        public WhiteBlackListBlock(Material material) {
            this("all-worlds", material, (short) 0);
        }

        public WhiteBlackListBlock(Material material, int damage) {
            this("all-worlds", material, damage);
        }

        public WhiteBlackListBlock(String world, Material material, int damage) {
            this.world = world;
            this.material = material.name();
            this.damage = (short) damage;
        }

        public String getWorld() {
            return world;
        }

        public Material getMaterial() {
            return Material.getMaterial(material) == null ? Material.AIR : Material.getMaterial(material);
        }

        public short getDamage() {
            return damage;
        }

        public boolean isWhiteBlackListed(Block block) {
            return getMaterial().equals(block.getType()) && (byte) damage == block.getData() && (getWorld() == null || getWorld().isEmpty() || getWorld().equals("all-worlds") || getWorld().equals(block.getWorld().getName()));
        }

    }

    public class CustomDrop extends WhiteBlackListBlock {

        private String itemMaterial = Material.AIR.name();
        private short itemDamage = 0;
        private int itemCount = 1;

        public CustomDrop(Material material, Material itemMaterial) {
            this(material, itemMaterial, 0);
        }

        public CustomDrop(Material material, Material itemMaterial, int itemDamage) {
            this(material, itemMaterial, itemDamage, 1);
        }

        public CustomDrop(Material material, Material itemMaterial, int itemDamage, int itemCount) {
            super(material);

            this.itemMaterial = itemMaterial.name();
            this.itemDamage = (short) itemDamage;
            this.itemCount = itemCount;
        }

        public Material getItemMaterial() {
            return Material.getMaterial(itemMaterial) == null ? Material.AIR : Material.getMaterial(itemMaterial);
        }

        public ItemStack getItem() {
            return new ItemStack(getItemMaterial(), itemCount, itemDamage);
        }

    }

}
