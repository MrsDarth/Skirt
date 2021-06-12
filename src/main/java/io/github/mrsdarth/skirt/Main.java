package io.github.mrsdarth.skirt;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;


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

}