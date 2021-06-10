package io.github.mrsdarth.skirt.elements.Other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;

@Name("Delete Skript")
@Description("deletes skript")
@Examples("delete skript")
@Since("1.2.0")

public class EffDeleteSkript extends Effect {

    static {
        Skript.registerEffect(EffDeleteSkript.class, "delete skript");
    }

    @Override
    protected void execute(Event event) {
        try {
            Plugin skript = Bukkit.getPluginManager().getPlugin("Skript");
            Method getfile = JavaPlugin.class.getDeclaredMethod("getFile");
            getfile.setAccessible(true);
            File jar = (File) getfile.invoke(skript);
            if (jar.delete()) {
                Bukkit.getPluginManager().disablePlugin(skript);
            } else throw new Exception("L");
        } catch (Exception ex) {
            ex.printStackTrace();
            Skript.error("Failed to delete skript");
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "delete skript";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
