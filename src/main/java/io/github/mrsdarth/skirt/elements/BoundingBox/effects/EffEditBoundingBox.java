package io.github.mrsdarth.skirt.elements.BoundingBox.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;

import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Edit Bounding Box")
@Description("move or expand a bounding box")
@Examples("shift {_box} by vector from {_pos1} to {_pos2}")
@Since("1.0.0")

public class EffEditBoundingBox extends Effect {

    static {
        Skript.registerEffect(EffEditBoundingBox.class,
                "(1¦shift|2¦offset|3¦expand) %boundingboxes% by %vector%");
    }

    private Expression<BoundingBox> box;
    private Expression<Vector> vector;
    private int mark;

    @Override
    protected void execute(Event event) {
        Vector v = vector.getSingle(event);
        if (v != null) {
            if (mark < 3) {
                for (BoundingBox b : box.getArray(event)) {
                    b.shift(v);
                }
            } else {
                for (BoundingBox b : box.getArray(event)) {
                    b.expand(v);
                }
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "edit bounding box";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mark = parseResult.mark;
        box = (Expression<BoundingBox>) exprs[0];
        vector = (Expression<Vector>) exprs[1];
        return true;
    }
}
