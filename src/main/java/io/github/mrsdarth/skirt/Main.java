package io.github.mrsdarth.skirt;

import java.io.IOException;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ch.njol.skript.SkriptAddon;


public class Main extends JavaPlugin {

    Main instance;
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

    public Main getInstance() {
        return instance;
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }

    public static String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> nmsclass(String name) {
        return nmsorcraft("net.minecraft.server", name);
    }

    public static Class<?> craftclass(String name) {
        return nmsorcraft("org.bukkit.craftbukkit", name);
    }

    public static Class<?> nmsorcraft(String p, String name) {
        try {
            return Class.forName(String.join(".", (new String[] {p, ver, name})));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}