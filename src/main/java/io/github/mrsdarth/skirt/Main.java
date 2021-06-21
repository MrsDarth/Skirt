package io.github.mrsdarth.skirt;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public class Main extends JavaPlugin {

    private static Main instance;
    SkriptAddon addon;

    public void onEnable() {
        instance = this;
        addon = Skript.registerAddon(this);

        try {
            addon.loadClasses("io.github.mrsdarth.skirt", "elements");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Skirt!");
    }

    public static File cache() {
        return new File(instance.getDataFolder(), "cache");
    }

    public void onDisable() {
        delete(cache());
    }

    private void delete(File f) {
        if (!f.exists()) return;
        if (f.isDirectory()) {
            for (File file: f.listFiles()) {
                delete(file);
            }
        } f.delete();
    }

    public static Main getInstance() {
        return instance;
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }

}