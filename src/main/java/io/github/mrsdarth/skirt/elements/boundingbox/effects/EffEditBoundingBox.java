package io.github.mrsdarth.skirt.elements.boundingbox.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;


@Name("Edit Bounding Box")
@Description("move or expand a bounding box")
@Examples("shift {_box} by vector from {_pos1} to {_pos2}")
@Since("1.0.0")

public class EffEditBoundingBox extends Effect {

    static {
        Skript.registerEffect(EffEditBoundingBox.class,
                "(shift|1¦expand) %boundingboxes% by %vector%");
    }

    private Expression<BoundingBox> boxExpr;
    private Expression<Vector> vectorExpr;
    private boolean shift;


    @Override
    protected void execute(@NotNull Event e) {
        Skirtness.getSingle(vectorExpr, e).ifPresent(vector -> Arrays.stream(boxExpr.getArray(e)).forEach(shift ? box -> box.shift(vector) : box -> box.expand(vector)));
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (shift ? "shift " : "expand ") + boxExpr.toString(e, debug) + " by " + vectorExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        boxExpr = (Expression<BoundingBox>) exprs[0];
        vectorExpr = (Expression<Vector>) exprs[1];
        shift = parseResult.mark == 0;
        return true;
    }
}
