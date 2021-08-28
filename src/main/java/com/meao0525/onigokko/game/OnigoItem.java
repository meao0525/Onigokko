package com.meao0525.onigokko.game;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum OnigoItem {
    ONI_HELMET(Material.DIAMOND_HELMET, "鬼ヘルメット", false, false, false, true),
    ONI_CHESTPLATE(Material.DIAMOND_CHESTPLATE, "鬼チェストプレート", false, false, false, true),
    ONI_LEGGINGS(Material.DIAMOND_LEGGINGS, "鬼レギンス", false, false, false, true),
    ONI_BOOTS(Material.DIAMOND_BOOTS, "鬼ブーツ", false, false, false, true),
    ONI_RESPAWN(Material.EMERALD, "リスポーン", false, true, false, true),
    ONIGO_PEARL(Material.ENDER_PEARL, "ブリンク", false, true, true, true),
    ONIGO_GLOWING(Material.ENDER_EYE, "発光", false, true, false, true);

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
