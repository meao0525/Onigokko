package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.OnigoItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class OnigoPearlThrowEvent implements Listener {

    private Onigokko plugin;
    private HashMap<Player, TeleportThread> teleporting = new HashMap<>();

    public OnigoPearlThrowEvent(Onigokko plugin) { this.plugin = plugin; }

    @EventHandler
    public void OnigoPearlThrowListener(PlayerInteractEvent e) {
        //ゲーム中か
        if (!plugin.isGaming()) { return; }
        //ブリンク火？
        ItemStack item = e.getItem();
        if ((item == null) || (!item.getItemMeta().getDisplayName().equalsIgnoreCase("ブリンク"))) { return; }
        //アドベンチャーモードか
        Player player = e.getPlayer();
        if (!player.getGameMode().equals(GameMode.ADVENTURE)) { return; }

        if (teleporting.containsKey(player)) {
            player.sendMessage(ChatColor.GRAY + "ブリンク中です");
        } else {
            //新しいスレッド
            TeleportThread thread = new TeleportThread(player);
            //スレッド登録
            teleporting.put(player, thread);
        }
    }

    //タイマー用内部クラス
    private class TeleportThread {

        private Player player;

        private TeleportThread(Player player) {
            this.player = player;
            startThread();
        }

        private void startThread() {
            //スレッド開始
            new BukkitRunnable() {
                @Override
                public void run() {
                    //再度与える
                    player.getInventory().addItem(OnigoItem.ONIGO_PEARL.toItemStack());
                    //テレポート中一覧から削除
                    teleporting.remove(player);
                }
            }.runTaskLater(plugin, 600);
        }
    }

}
