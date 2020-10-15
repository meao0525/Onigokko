package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NigeTouchEvent implements Listener {

    private Onigokko plugin;

    public NigeTouchEvent(Onigokko plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void nigeTouchListener(EntityDamageByEntityEvent e) {
        //人か？
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
            return;
        }
        //ゲーム中か
        if (!plugin.isGaming()) {
            e.setCancelled(true);
            return;
        }

        //逃げに殴られた！！！
        Player damager = (Player)e.getDamager();
        Player target = (Player)e.getEntity();
        if (!plugin.getOni().contains(damager) && !plugin.getOni().contains(target)) {
            //捕まってる人
            if (target.getWalkSpeed() == 0.0F) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ] "
                        + ChatColor.RESET + damager.getDisplayName() + " が "
                        + ChatColor.RESET + target.getDisplayName() + " を　開放しました");
                //元に戻す
                e.setDamage(0.0);
                target.setWalkSpeed(0.2F);
                target.setGlowing(false);
                //エフェクト
                target.sendTitle("", ChatColor.AQUA + "解放！", 0, 1, 1);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 0.2F);
            }
        }
    }
}
