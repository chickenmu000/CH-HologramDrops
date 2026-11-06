package com.ch.hologramdrops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/** 바닥에 떨어진 아이템 위에 이름/개수를 홀로그램으로 표시 */
public class CHHologramDrops extends JavaPlugin {

    private HologramConfig config;
    private HologramManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = new HologramConfig(this);
        config.load();
        manager = new HologramManager(this, config);
        getServer().getPluginManager().registerEvents(new DropListener(this, config, manager), this);
        getLogger().info("CH-HologramDrops " + Lang.get("enabled"));
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.purgeAll();
        getLogger().info("CH-HologramDrops " + Lang.get("disabled"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        config.load();
        sender.sendMessage(Component.text("[CH-HologramDrops] ", NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true)
                .append(Component.text(Lang.get("reloaded"), NamedTextColor.GREEN).decoration(TextDecoration.BOLD, false)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return List.of();
    }
}
