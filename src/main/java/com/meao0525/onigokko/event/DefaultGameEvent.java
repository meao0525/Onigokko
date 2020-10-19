package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.OnigoItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class DefaultGameEvent implements Listener {

    private Onigokko plugin;

    public DefaultGameEvent(Onigokko plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void LoginEventListener(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        Team nigeTeam = plugin.getNigeTeam();
        Team oniTeam = plugin.getOniTeam();
        if (plugin.isGaming()) {
            //ゲーム中
            if (!nigeTeam.hasEntry(player.getName())
                    && !oniTeam.hasEntry(player.getName())) {
                //両方のチームに所属してないなら逃げチームに入れる
                plugin.makeNige(player);
            }
            //タイマー表示
            plugin.getTimerBar().addPlayer(player);

        } else {
            //ゲーム中じゃない
            if (oniTeam.hasEntry(player.getName())) {
                oniTeam.removeEntry(player.getName());
                //装備消す
                player.getInventory().setHelmet(new ItemStack(Material.AIR));
                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                player.getInventory().setLeggings(new ItemStack(Material.AIR));
                player.getInventory().setBoots(new ItemStack(Material.AIR));
            } else {
                nigeTeam.removeEntry(player.getName());
                //足の速さ戻す
                player.setWalkSpeed(0.2F);
                //発行消す
                player.setGlowing(false);
            }
        }
    }

    @EventHandler
    public void MoveEventListener(PlayerMoveEvent e) {
        if (!plugin.isGaming()) {
            return;
        }
        Player player = e.getPlayer();
        //捕まってる人は動けないよ
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
        //落下ダメ消すよ
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
