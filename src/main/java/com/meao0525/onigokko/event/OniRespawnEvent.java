package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class OniRespawnEvent implements Listener {

    private Onigokko plugin;

    public OniRespawnEvent(Onigokko plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void UseRespawnItemListener(PlayerInteractEvent e) {
        //右クリックか
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //ゲーム中か
            if (!plugin.isGaming()) { return; }
            //アドベンチャーモードか
            Player player = e.getPlayer();
            if (!player.getGameMode().equals(GameMode.ADVENTURE)) { return; }
            //リスポーン？
            ItemStack item = player.getInventory().getItemInMainHand();
            if ((item.getItemMeta() == null) || (!item.getItemMeta().getDisplayName().equalsIgnoreCase("リスポーン"))) { return; }
            //リスポーンさせる
            player.teleport(plugin.getRandomLoc(plugin.getOniStartloc()));
            //効果音
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, 0.5F);
        }
    }
}
