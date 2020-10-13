package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import com.meao0525.onigokko.game.Mode;
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
        //鬼に殴られた！！！
        Player damager = (Player)e.getDamager();
        Player target = (Player)e.getEntity();
        if (plugin.getOni().contains(damager) && !(plugin.getOni().contains(target))) {
            //ダメージをほぼ失くす（エフェクトは欲しい）
            e.setDamage(0.1);
            switch (plugin.getMode()) {
                case ONIGOKKO:
                    //鬼入れ替え
                    plugin.getOni().add(target);
                    plugin.getOni().remove(damager);
                    //テレポート
                    target.teleport(plugin.getOniStartloc());
                    damager.teleport(plugin.getNigeStartloc());
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
                    //テレポート
                    target.teleport(plugin.getOniStartloc());
                    //イベントの登録しなおし
                    plugin.registerEvent();
                    break;

                case KOORIONI:
                    //動けなくする
                    target.setWalkSpeed(0.0F);
                    target.setGlowing(true);
                    break;
            }
        }
    }
}
