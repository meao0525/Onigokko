package com.meao0525.onigokko.game;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum OnigoItem {
    ONI_HELMET(Material.DIAMOND_HELMET, "鬼ヘルメット", false, false),
    ONI_CHESTPLATE(Material.DIAMOND_CHESTPLATE, "鬼チェストプレート", false, false),
    ONI_LEGGINGS(Material.DIAMOND_LEGGINGS, "鬼レギンス", false, false),
    ONI_BOOTS(Material.DIAMOND_BOOTS, "鬼ブーツ", false, false);


    private Material material;
    private String name;
    private boolean canThrow; //捨てれない
    private boolean canTouch; //触れない

    private OnigoItem(Material material, String name, boolean canThrow, boolean canTouch) {
        this.material = material;
        this.name = name;
        this.canThrow = canThrow;
        this.canTouch = canTouch;
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

    public boolean isCanThrow() {
        return canThrow;
    }

    public boolean isCanTouch() {
        return canTouch;
    }
}
