package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.Mode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OniTouchEvent implements Listener {

    private Onigokko plugin;

    public OniTouchEvent(Onigokko plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void oniTouchListener(EntityDamageByEntityEvent e) {
        //人か？
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
            return;
        }
        //ゲーム中か
        if (!plugin.isGaming()) {
            e.setCancelled(true);
            return;
        }

        //鬼に殴られた！！！
        Player damager = (Player)e.getDamager();
        Player target = (Player)e.getEntity();
        if (plugin.getOni().contains(damager) && !(plugin.getOni().contains(target))) {
            //捕まってる人
            if (target.getWalkSpeed() == 0.0F) {
                e.setCancelled(true);
                return;
            }
            //ダメージをほぼ失くす（エフェクトは欲しい）
            e.setDamage(0.1);
            Bukkit.broadcastMessage(ChatColor.GOLD + "[どこでも鬼ごっこ] "
                    + ChatColor.RESET + damager.getDisplayName() + " が "
                    + ChatColor.RESET + target.getDisplayName() + " を捕まえました");
            switch (plugin.getMode()) {
                case ONIGOKKO:
                    //鬼入れ替え
                    plugin.getOni().add(target);
                    plugin.getOni().remove(damager);
                    plugin.makeOni(target);
                    plugin.makeNige(damager);
                    //イベントの登録しなおし
                    plugin.registerEvent();
                    break;

                case KEIDORO:
                    //テレポート
                    target.teleport(plugin.getPrison());
                    //動けなくする
                    target.setWalkSpeed(0.0F);
                    target.setGlowing(true);
                    break;

                case FUEONI:
                    //鬼増やす
                    plugin.getOni().add(target);
                    plugin.makeOni(target);
                    //イベントの登録しなおし
                    plugin.registerEvent();
                    break;

                case KOORIONI:
                    //動けなくする
                    target.setWalkSpeed(0.0F);
                    target.setGlowing(true);
                    break;
            }
            //エフェクト
            target.sendTitle("", ChatColor.RED + "確保された...", 0, 3, 1);
            target.getWorld().playSound(target.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.3F, 0.5F);
        }
    }
}
