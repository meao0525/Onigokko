package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.OnigoItem;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
            //リスポーン？
            ItemStack item = e.getItem();
            if ((item == null) || (!item.getItemMeta().getDisplayName().equalsIgnoreCase("リスポーン"))) { return; }
            //アドベンチャーモードか
            Player player = e.getPlayer();
            if (!player.getGameMode().equals(GameMode.ADVENTURE)) { return; }
            //リスポーンさせる
            player.teleport(plugin.getRandomStartLoc(plugin.getOniStartloc()));
        }
    }
}
