package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.settings.OnigoItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class DefaultGameEvent implements Listener {

    private Onigokko plugin;

    public DefaultGameEvent(Onigokko plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void LoginEventListener(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Team nigeTeam = plugin.getNigeTeam();
        Team oniTeam = plugin.getOniTeam();
        if (plugin.isGaming()) {
            //ゲーム中
            if (player.getGameMode().equals(GameMode.ADVENTURE)) {
                if (!nigeTeam.hasEntry(player.getName())
                        && !oniTeam.hasEntry(player.getName())) {
                    //両方のチームに所属してないなら逃げチームに入れる
                    plugin.makeNige(player);
                    //ランダムな初期地点に移動
                    player.teleport(plugin.getRandomLoc(plugin.getNigeStartloc()));
                    //足の速さを設定
                    plugin.setGameWalkSpeed(player, plugin.getNigeSpeed());
                }
                //タイマー用ボスバー
                plugin.getTimerBar().addPlayer(player);
            }

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
            //タイマー用ボスバー消す
            plugin.getTimerBar().removePlayer(player);
        }
    }

    @EventHandler
    public void MoveEventListener(PlayerMoveEvent e) {
        if (!plugin.isGaming() || !e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }
        Player player = e.getPlayer();
        //上下以外動けないようにする
        if ((player.isGlowing()) && (player.getWalkSpeed() == 0.0)) {
            int fromX = e.getFrom().getBlock().getX();
            int fromZ = e.getFrom().getBlock().getZ();
            int toX = e.getTo().getBlock().getX();
            int toZ = e.getTo().getBlock().getZ();
            //X座標かZ座標どちらかが違う
            if ((fromX != toX) || (fromZ != toZ)) {
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
        //触られたアイテム
        ItemStack item = e.getCurrentItem();
        if (item != null && item.getItemMeta() != null) {
            for (OnigoItem oi : OnigoItem.values()) {
                String itemName = item.getItemMeta().getDisplayName();
                String oiName = oi.toItemStack().getItemMeta().getDisplayName();
                if (itemName.equalsIgnoreCase(oiName) && !(oi.isCanTouch())) {
                    //触っちゃいけないオニゴアイテムです
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void ThrowItemEventListener(PlayerDropItemEvent e) {
        if (!plugin.isGaming()) {
            return;
        }
        //捨てたアイテム
        ItemStack item = e.getItemDrop().getItemStack();
        if (item.getItemMeta() != null) {
            for (OnigoItem oi : OnigoItem.values()) {
                String itemName = item.getItemMeta().getDisplayName();
                String oiName = oi.toItemStack().getItemMeta().getDisplayName();
                if (itemName.equalsIgnoreCase(oiName) && !(oi.isCanThrow())) {
                    //捨てちゃいけないオニゴアイテムです
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
