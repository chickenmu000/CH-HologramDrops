package com.ch.hologramdrops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** config.yml 읽기 + 홀로그램 글자 만들기 */
public class HologramConfig {

    private final JavaPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    private String formatTemplate;
    private boolean hideSingleCount;
    private double heightOffset;
    private int backgroundColorArgb;
    private float textScale;
    private boolean textShadow;
    private Set<String> enabledWorlds;
    private Set<Material> blacklistedMaterials;
    private boolean customNamesOnly;
    private Map<Material, String> nameOverrides;

    public HologramConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();
        FileConfiguration c = plugin.getConfig();

        String lang = c.getString("language", "ko");
        if (!Lang.set(lang)) plugin.getLogger().warning(Lang.get("bad-language", lang));

        formatTemplate = c.getString("hologram-format", "<white><item> <gray>x<amount>");
        hideSingleCount = c.getBoolean("hide-single-count", true);
        heightOffset = c.getDouble("hologram-height-offset", 0.75);
        textScale = (float) c.getDouble("text-scale", 1.0);
        textShadow = c.getBoolean("text-shadow", false);

        try {
            backgroundColorArgb = (int) Long.parseLong(c.getString("text-background-color", "00000000"), 16);
        } catch (NumberFormatException ex) {
            backgroundColorArgb = 0;
        }

        List<String> worlds = c.getStringList("enabled-worlds");
        enabledWorlds = worlds.isEmpty() ? Collections.emptySet() : new HashSet<>(worlds);

        blacklistedMaterials = EnumSet.noneOf(Material.class);
        for (String name : c.getStringList("blacklisted-materials")) {
            try {
                blacklistedMaterials.add(Material.valueOf(name.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning(Lang.get("bad-material", name));
            }
        }

        customNamesOnly = c.getBoolean("custom-names-only", false);

        nameOverrides = new EnumMap<>(Material.class);
        ConfigurationSection sec = c.getConfigurationSection("item-name-overrides");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                try {
                    nameOverrides.put(Material.valueOf(key.toUpperCase(Locale.ROOT)), sec.getString(key, ""));
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().warning(Lang.get("bad-override", key));
                }
            }
        }
    }

    public boolean isWorldEnabled(String world) {
        return enabledWorlds.isEmpty() || enabledWorlds.contains(world);
    }

    public boolean isBlacklisted(Material m) {
        return blacklistedMaterials.contains(m);
    }

    public double heightOffset() {
        return heightOffset;
    }

    public int backgroundColorArgb() {
        return backgroundColorArgb;
    }

    public float textScale() {
        return textScale;
    }

    public boolean textShadow() {
        return textShadow;
    }

    /** 홀로그램에 보여줄 글자. 표시하지 않을 아이템이면 null */
    public Component buildHologramText(ItemStack item) {
        if (isBlacklisted(item.getType())) return null;
        Component name = resolveItemName(item);
        if (name == null) return null;

        int amount = item.getAmount();
        String amountText = (hideSingleCount && amount == 1) ? "" : String.valueOf(amount);

        String template = formatTemplate;
        if (amountText.isEmpty()) {
            // 개수를 숨길 때 "x<amount>" / "(<amount>)" 와 앞 공백도 함께 제거
            template = template.replaceAll("\\s*x<amount>", "")
                    .replaceAll("\\s*\\(<amount>\\)", "")
                    .replace("<amount>", "");
        }
        return mm.deserialize(template, TagResolver.resolver(
                Placeholder.parsed("item", mm.serialize(name)),
                Placeholder.unparsed("amount", amountText)));
    }

    private Component resolveItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) return meta.displayName();
        if (customNamesOnly) return null;
        String override = nameOverrides.get(item.getType());
        if (override != null) return mm.deserialize(override);
        return Component.translatable(item.translationKey());
    }
}
