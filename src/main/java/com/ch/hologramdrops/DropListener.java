package com.ch.hologramdrops;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DropListener implements Listener {

    private final JavaPlugin plugin;
    private final HologramConfig config;
    private final HologramManager manager;

    public DropListener(JavaPlugin plugin, HologramConfig config, HologramManager manager) {
        this.plugin = plugin;
        this.config = config;
        this.manager = manager;
    }

    /** 아이템이 떨어짐 -> 홀로그램 생성 (엔티티가 완전히 생긴 다음 틱에) */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(ItemSpawnEvent e) {
        Item item = e.getEntity();
        if (!config.isWorldEnabled(item.getWorld().getName())) return;
        if (config.isBlacklisted(item.getItemStack().getType())) return;
        Bukkit.getScheduler().runTask(plugin, () -> manager.create(item));
    }

    /** 아이템이 합쳐짐 -> 남는 쪽 개수 갱신 */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMerge(ItemMergeEvent e) {
        Item target = e.getTarget();
        if (!config.isWorldEnabled(target.getWorld().getName())) return;
        Bukkit.getScheduler().runTask(plugin, () -> manager.refresh(target));
    }

    /** 줍기 : 전부 주우면 제거, 일부만 주우면 개수 갱신 */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent e) {
        Item item = e.getItem();
        if (!manager.isTracked(item)) return;
        if (e.getRemaining() == 0) {
            manager.remove(item);
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> manager.refresh(item));
        }
    }

    /** 아이템이 사라짐(디스폰, 청크 언로드 등) -> 홀로그램 정리. 제거는 다음 틱에 처리됨 */
    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent e) {
        if (e.getEntity() instanceof Item item && manager.isTracked(item)) {
            manager.remove(item);
        }
    }
}
