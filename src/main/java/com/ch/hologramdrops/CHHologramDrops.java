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
        getLogger().info("CH-HologramDrops 활성화 - 떨어진 아이템 위에 홀로그램이 표시됩니다.");
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.purgeAll();
        getLogger().info("CH-HologramDrops 비활성화 - 모든 홀로그램을 제거했습니다.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        config.load();
        sender.sendMessage(Component.text("[CH-HologramDrops] ", NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true)
                .append(Component.text("설정을 다시 불러왔습니다.", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, false)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return List.of();
    }
}
