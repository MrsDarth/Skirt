package io.github.mrsdarth.skirt.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Name("Delete Skript")
@Description("deletes skript")
@Examples("delete skript")
@Since("1.2.0")

public class EffDeleteSkript extends Effect {

    static {
        Skript.registerEffect(EffDeleteSkript.class, "delete skript [(1¦(and|[along] with) all addons)]");
    }

    boolean addons;

    @Override
    protected void execute(@NotNull Event e) {
        if (addons) Skript.getAddons().forEach(EffDeleteSkript::delete);
        delete(Skript.getAddonInstance());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "delete skript" + (addons ? " and all addons" : "");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        addons = parseResult.mark == 1;
        return true;
    }

    private static void delete(SkriptAddon skriptAddon) {
        File jar = skriptAddon.getFile();
        if (jar != null && jar.delete())
            Bukkit.getPluginManager().disablePlugin(skriptAddon.plugin);
        else
            Skirtness.getPlugin().getLogger().warning("Failed to delete " + skriptAddon.getName());
    }
}
