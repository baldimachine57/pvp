package com.example.pvpdrop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Oyuncu başka bir oyuncu tarafından öldürülürse eşyaları düşer.
 * Başka her türlü ölümde (mob, lav, düşme, /kill...) eşyalar ve XP korunur.
 */
public class PvpDropPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("PvpDrop aktif!");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();

        boolean pvp = killer != null && !killer.equals(victim);

        if (pvp) {
            // Başka oyuncu öldürdü: eşyalar ve XP normal şekilde düşsün
            e.setKeepInventory(false);
            e.setKeepLevel(false);

            // Sunucuda keepInventory gamerule açıksa düşecek liste boş gelir,
            // o durumda eşyaları kendimiz düşürelim.
            if (e.getDrops().isEmpty()) {
                for (ItemStack item : victim.getInventory().getContents()) {
                    if (item != null && !item.getType().isAir()) {
                        e.getDrops().add(item.clone());
                    }
                }
                victim.getInventory().clear();
            }
        } else {
            // Başka her ölüm: eşyaları ve XP'yi koru
            e.setKeepInventory(true);
            e.getDrops().clear();
            e.setKeepLevel(true);
            e.setDroppedExp(0);
        }
    }
}
