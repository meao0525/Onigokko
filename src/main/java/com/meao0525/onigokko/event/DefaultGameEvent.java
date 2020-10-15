package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.OnigoItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class DefaultGameEvent implements Listener {

    private Onigokko plugin;

    public DefaultGameEvent(Onigokko plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void MoveEventListener(PlayerMoveEvent e) {
        if (!plugin.isGaming()) {
            return;
        }
        Player player = e.getPlayer();
        if ((player.isGlowing()) && (player.getWalkSpeed() == 0.0)) {
            if (e.getFrom().getBlock().getLocation().equals(e.getTo().getBlock().getLocation())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void FallDamageEventListener(EntityDamageEvent e) {
        if (!plugin.isGaming()) {
            return;
        }
        if ((e.getEntity() instanceof Player)
                && (e.getCause().equals(EntityDamageEvent.DamageCause.FALL))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void InventoryClickEventListener(InventoryClickEvent e) {
        if (!plugin.isGaming()) {
            return;
        }
        if (e.getInventory().getType().equals(InventoryType.PLAYER)) {
            //触られたアイテム
            ItemStack item = e.getCurrentItem();
            for (OnigoItem oi : OnigoItem.values()) {
                if (item.equals(oi.toItemStack()) && !(oi.isCanTouch())) {
                    //触っちゃいけないオニゴアイテムです
                    e.setCancelled(true);
                }
            }
        }
    }
}
