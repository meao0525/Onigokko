package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OnigoDashEvent implements Listener {

    private Onigokko plugin;

    public OnigoDashEvent(Onigokko plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void UseDashItemListener(PlayerInteractEvent e) {
        //右クリックか
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //ゲーム中か
            if (!plugin.isGaming()) { return; }
            //アドベンチャーモードか
            Player player = e.getPlayer();
            if (!player.getGameMode().equals(GameMode.ADVENTURE)) { return; }
            //発光？
            ItemStack item = player.getInventory().getItemInMainHand();
            if ((item.getItemMeta() == null) || (!item.getItemMeta().getDisplayName().equalsIgnoreCase("迅速"))) { return; }
            //クールダウン中？
            if (player.hasCooldown(item.getType())) { return; }
            //スピードエフェクト5秒、鈍足8秒
            PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 100, 8, true, false);
            PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 160, 2, true, false);
            player.addPotionEffect(speed);
            player.addPotionEffect(slow);
            //クールダウン30秒
            player.setCooldown(Material.FEATHER, 600);
            //エフェクト
            player.playEffect(EntityEffect.FIREWORK_EXPLODE);
            player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 1.0f, 2.0f);
        }
    }
}
