package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.OnigoItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

    public OnigoPearlThrowEvent(Onigokko plugin) { this.plugin = plugin; }

    @EventHandler
    public void PlayerTeleportListener(PlayerTeleportEvent e) {
        //ゲーム中か
        if (!plugin.isGaming()) { return; }
        //アドベンチャーモードか
        Player player = e.getPlayer();
        if (!player.getGameMode().equals(GameMode.ADVENTURE)) { return; }
        //エンダーパールのテレポートか
        if (!e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) { return; }

        //再度与える
        player.getInventory().addItem(OnigoItem.ONIGO_PEARL.toItemStack());
        player.setCooldown(Material.ENDER_PEARL, 600);
    }

}
