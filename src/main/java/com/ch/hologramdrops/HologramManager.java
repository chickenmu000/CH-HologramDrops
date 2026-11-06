package com.ch.hologramdrops;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** 떨어진 아이템 <-> 홀로그램(TextDisplay) 관리 */
public class HologramManager {

    private final JavaPlugin plugin;
    private final HologramConfig config;
    /** 아이템 UUID -> 홀로그램 UUID */
    private final Map<UUID, UUID> itemToHologram = new ConcurrentHashMap<>();

    public HologramManager(JavaPlugin plugin, HologramConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void create(Item item) {
        if (!item.isValid()) return;
        Component text = config.buildHologramText(item.getItemStack());
        if (text == null) return;
        if (itemToHologram.containsKey(item.getUniqueId())) {
            refresh(item);
            return;
        }
        Location loc = item.getLocation().clone();
        loc.setY(loc.getY() + config.heightOffset());
        item.getWorld().spawn(loc, TextDisplay.class, d -> {
            applyStyle(d, text);
            item.addPassenger(d);
            itemToHologram.put(item.getUniqueId(), d.getUniqueId());
        });
    }

    /** 아이템 개수/이름이 바뀌었을 때 글자 갱신 */
    public void refresh(Item item) {
        UUID holoId = itemToHologram.get(item.getUniqueId());
        if (holoId == null) return;
        Entity e = item.getWorld().getEntity(holoId);
        if (e instanceof TextDisplay td) {
            Component text = config.buildHologramText(item.getItemStack());
            if (text == null) {
                remove(item);
                return;
            }
            td.text(text);
        } else {
            itemToHologram.remove(item.getUniqueId());
        }
    }

    public void remove(Item item) {
        remove(item.getUniqueId(), item.getWorld());
    }

    /**
     * 홀로그램 제거.
     * 청크가 언로드되는 도중(EntityRemoveFromWorldEvent)에 엔티티를 바로 지우면 서버가 막고 경고를 내므로,
     * 항상 다음 틱에 지움. (청크가 이미 언로드됐다면 홀로그램도 함께 사라진 상태라 지울 것이 없음)
     */
    public void remove(UUID itemId, World world) {
        UUID holoId = itemToHologram.remove(itemId);
        if (holoId == null) return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            Entity e = world.getEntity(holoId);
            if (e != null && e.isValid()) e.remove();
        });
    }

    public boolean isTracked(Item item) {
        return itemToHologram.containsKey(item.getUniqueId());
    }

    /** 플러그인 종료 시 모든 홀로그램 제거 */
    public void purgeAll() {
        for (UUID holoId : Map.copyOf(itemToHologram).values()) {
            for (World w : Bukkit.getWorlds()) {
                Entity e = w.getEntity(holoId);
                if (e != null) e.remove();
            }
        }
        itemToHologram.clear();
    }

    public int trackedCount() {
        return itemToHologram.size();
    }

    private void applyStyle(TextDisplay d, Component text) {
        d.text(text);
        d.setBillboard(Display.Billboard.CENTER);
        d.setAlignment(TextDisplay.TextAlignment.CENTER);
        d.setShadowed(config.textShadow());

        int argb = config.backgroundColorArgb();
        d.setBackgroundColor(Color.fromARGB((argb >> 24) & 0xFF, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF));

        float s = config.textScale();
        d.setTransformation(new Transformation(
                new Vector3f(0f, (float) config.heightOffset(), 0f), new AxisAngle4f(0f, 0f, 0f, 1f),
                new Vector3f(s, s, s), new AxisAngle4f(0f, 0f, 0f, 1f)));
        d.setPersistent(false);
    }
}
