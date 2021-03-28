package io.github.mrsdarth.skirt;

import java.io.IOException;

import ch.njol.skript.Skript;
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
        System.out.println("sk§66§99!!!!!");
    }

    public Main getInstance() {
        return instance;
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }
}