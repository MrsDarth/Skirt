package io.github.mrsdarth.skirt;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.expressions.ExprRelationalVariable;
import ch.njol.skript.lang.ExpressionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.net.URL;
import java.util.Scanner;


public class Main extends JavaPlugin {

    private static Main instance;
    SkriptAddon addon;

    @Override
    public void onEnable() {
        instance = this;
        addon = Skript.registerAddon(this);

        try {
            addon.loadClasses("io.github.mrsdarth.skirt", "elements");

            System.out.println("Skirt!");

            Metrics metrics = new Metrics(this, 11834);

            Scanner skirt = new Scanner(new URL("https://api.github.com/repos/mrsdarth/skirt/releases").openStream());
            StringBuilder text = new StringBuilder();
            while (skirt.hasNext())
                text.append(skirt.next());
            String latest = ((JSONObject) ((JSONArray) new JSONParser().parse(text.toString())).get(0)).get("tag_name").toString();
            if (!this.getDescription().getVersion().equals(latest)) {
                TextComponent msg = Component.text()
                        .append(Component.text("Go get ").color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text("Skirt " + latest + " ").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("here").color(NamedTextColor.AQUA)
                                        .decorate(TextDecoration.UNDERLINED)
                                        .hoverEvent(HoverEvent.showText(Component.text("Click to go to latest Skirt version").color(NamedTextColor.GREEN)))
                                        .clickEvent(ClickEvent.openUrl("https://github.com/MrsDarth/Skirt/releases/latest")))
                        .build();
                Bukkit.getPluginManager().registerEvent(
                        PlayerJoinEvent.class,
                        new Listener() {}, EventPriority.NORMAL,
                        (listener, event) -> {
                            Player p = ((PlayerJoinEvent) event).getPlayer();
                            if (p.isOp()) p.sendMessage(msg);
                        }, this);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onDisable() {
        delete(cache());
    }

    private static void delete(File f) {
        if (!f.exists()) return;
        if (f.isDirectory()) {
            for (File file: f.listFiles()) {
                delete(file);
            }
        } f.delete();
    }

    public static File cache() {
        return new File(instance.getDataFolder(), "cache");
    }

    public static Main getInstance() {
        return instance;
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }

}