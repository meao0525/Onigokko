package com.meao0525.onigokko.settings;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;

public enum OnigoItem {
    /*-------AdminItem-------*/
    ADMIN_ONI_ITEMSETTING(Material.RED_CONCRETE, "鬼アイテム設定", true, true, false, false),
    ADMIN_NIGE_ITEMSETTING(Material.RED_CONCRETE, "逃げアイテム設定", true, true, false, false),
    /*-------GameItem-------*/
    ONI_HELMET(Material.DIAMOND_HELMET, "鬼ヘルメット", false, false, false, true),
    ONI_CHESTPLATE(Material.DIAMOND_CHESTPLATE, "鬼チェストプレート", false, false, false, true),
    ONI_LEGGINGS(Material.DIAMOND_LEGGINGS, "鬼レギンス", false, false, false, true),
    ONI_BOOTS(Material.DIAMOND_BOOTS, "鬼ブーツ", false, false, false, true),
    ONI_RESPAWN(Material.EMERALD, "リスポーン", false, true, false, true),
    ONIGO_PEARL(Material.ENDER_PEARL, "パール", false, true, true, true),
    ONIGO_GLOWING(Material.ENDER_EYE, "発光", false, true, false, true),
    ONIGO_DASH(Material.FEATHER, "迅速", false, true, true, true);

    //TODO: 設定系
    //TODO: 弓、透明

    private Material material;
    private String name;
    private boolean canThrow; //捨てれない
    private boolean canTouch; //触れない
    private boolean nige; //逃げ持てる
    private boolean oni; //鬼持てる

    private OnigoItem(Material material, String name, boolean canThrow, boolean canTouch, boolean nige, boolean oni) {
        this.material = material;
        this.name = name;
        this.canThrow = canThrow;
        this.canTouch = canTouch;
        this.nige = nige;
        this.oni = oni;
    }

    public ItemStack toItemStack() {
        //アイテム取得
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        //メタ設定
        meta.setDisplayName(name);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                          ItemFlag.HIDE_DESTROYS,
                          ItemFlag.HIDE_ENCHANTS,
                          ItemFlag.HIDE_PLACED_ON,
                          ItemFlag.HIDE_POTION_EFFECTS,
                          ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);

        return item;
    }

    public HashSet<ItemStack> getAdminItems() {
        HashSet<ItemStack> adminItems = new HashSet<>();
        //Adminアイテムを返す
        adminItems.add(ADMIN_ONI_ITEMSETTING.toItemStack());
        adminItems.add(ADMIN_NIGE_ITEMSETTING.toItemStack());

        return adminItems;
    }

    public String getName() {
        return name;
    }

    public boolean isCanThrow() {
        return canThrow;
    }

    public boolean isCanTouch() {
        return canTouch;
    }

    public boolean isNige() {
        return nige;
    }

    public boolean isOni() {
        return oni;
    }
}
