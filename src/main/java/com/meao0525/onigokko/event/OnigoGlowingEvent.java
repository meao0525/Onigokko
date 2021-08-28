package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OnigoGlowingEvent implements Listener {

    private Onigokko plugin;

    public OnigoGlowingEvent(Onigokko plugin) { this.plugin = plugin; }

    @EventHandler
    public void UseGlowingItemListener(PlayerInteractEvent e) {
        //右クリックか
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //ゲーム中か
            if (!plugin.isGaming()) { return; }
            //アドベンチャーモードか
            Player player = e.getPlayer();
            if (!player.getGameMode().equals(GameMode.ADVENTURE)) { return; }
            //発光？
            ItemStack item = player.getInventory().getItemInMainHand();
            if ((item == null) || (!item.getItemMeta().getDisplayName().equalsIgnoreCase("発光"))) { return; }
            //元のイベント消す
            e.setCancelled(true);
            //発光エフェクト30秒
            PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, 200, 1, false);
            //全員光れ！
            for (String n : plugin.getNigeTeam().getEntries()) {
                Player p = Bukkit.getPlayer(n);
                if (!p.isGlowing()) { p.addPotionEffect(glowing); }
            }
            //クールダウン40秒
            player.setCooldown(Material.ENDER_EYE, 800);
        }
    }

}
