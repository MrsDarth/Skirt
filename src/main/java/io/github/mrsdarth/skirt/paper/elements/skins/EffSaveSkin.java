package io.github.mrsdarth.skirt.paper.elements.skins;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

public class EffSaveSkin extends AsyncEffect {

    private Expression<String> stringExpr;
    private Expression<BufferedImage> imageExpr;
    private Expression<OfflinePlayer> offlinePlayerExpr;

    private Variable<?> variable;

    private int pattern;

    static {
        Skript.registerEffect(EffSaveSkin.class, "load skin from ((file [path]|1¦url) %-string%|2¦%-image%|3¦%-offlineplayer%) [in]to %object%");
    }

    @Override
    protected void execute(@NotNull Event e) {
        try {
            (switch (pattern) {
                case 0 -> Skirtness.getSingle(stringExpr, e).map(File::new).map(Skins::getSkin);
                case 1 -> Skirtness.getSingle(stringExpr, e).map(Skins::getSkin);
                case 2 -> Skirtness.getSingle(imageExpr, e).map(Skins::getSkin);
                case 3 -> Skirtness.getSingle(offlinePlayerExpr, e).map(Skins::getSkin);
                default -> Optional.empty();
            }).ifPresent(skin -> Variables.setVariable(variable.getName().toString(e), skin, e, variable.isLocal()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "load skin from " + switch (pattern) {
            case 0 -> "file ";
            case 1 -> "url ";
            default -> "";
        } + (switch (pattern) {
            case 0, 1 -> stringExpr;
            case 2 -> imageExpr;
            case 3 -> offlinePlayerExpr;
            default -> throw new IllegalStateException();
        }).toString(e, debug) + " to " + variable.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        if (!(exprs[3] instanceof Variable<?> variable)) return false;
        this.variable = variable;
        pattern = parseResult.mark;
        switch (pattern) {
            case 0, 1 -> stringExpr = (Expression<String>) exprs[0];
            case 2 -> imageExpr = (Expression<BufferedImage>) exprs[1];
            case 3 -> offlinePlayerExpr = (Expression<OfflinePlayer>) exprs[2];
        }
        return true;
    }
}
