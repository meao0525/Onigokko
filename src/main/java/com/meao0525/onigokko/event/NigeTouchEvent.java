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
        if (!(plugin.getOni().contains(damager.getName())) && !(plugin.getOni().contains(target.getName()))) {
            e.setCancelled(true);
            //お前も捕まってるやないかい
            if (damager.isGlowing() && damager.getWalkSpeed() == 0.0) { return; }
            //捕まってる人を殴った
            if ((target.isGlowing()) && (target.getWalkSpeed() == 0.0)) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ] "
                        + ChatColor.RESET + damager.getDisplayName() + " が "
                        + ChatColor.RESET + target.getDisplayName() + " を開放しました");
                //元に戻す
                plugin.setGameWalkSpeed(target, plugin.getNigeSpeed());
                target.setGlowing(false);
                //エフェクト
                target.sendTitle("", ChatColor.AQUA + "解放！", 0, 40, 20);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3F, 0.2F);
            }

        } else if (!(plugin.getOni().contains(damager.getName())) && plugin.getOni().contains(target.getName())) {
            //殴った方が逃げ、殴られた方が鬼
            e.setCancelled(true);
        }
    }
}
