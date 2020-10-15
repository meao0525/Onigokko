package com.meao0525.onigokko.event;

import com.meao0525.onigokko.Onigokko;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

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
            e.setCancelled(true);
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
}
