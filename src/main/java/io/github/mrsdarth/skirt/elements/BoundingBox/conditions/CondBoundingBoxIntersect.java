package io.github.mrsdarth.skirt.elements.BoundingBox.conditions;


import org.bukkit.util.Vector;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Bounding Box intersect")
@Description("Checks whether a bounding box intersects with another, fully contains another, or contains positions")
@Examples("if {_box} intersects bounding box of player:")
@Since("1.0.0")

public class CondBoundingBoxIntersect extends Condition {

    static {
        Skript.registerCondition(CondBoundingBoxIntersect.class,
                "[[bounding ]box ]%boundingbox% (1¦fully contain[s]|2¦intersects[ with]) %boundingbox%",
                "[[bounding ]box ]%boundingbox% does(n't| not) (1¦fully contain[s]|2¦intersects[ with]) %boundingbox%",
                "[[bounding ]box ]%boundingbox% (1¦contain|2¦does not contain)[s] %vectors%");
    }




    private Expression<BoundingBox> box1;
    private Expression<BoundingBox> box2;
    private Expression<Vector> v;

    private int pattern;

    private boolean not;
    private boolean full;

    @SuppressWarnings("unchecked")
    @Override

    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        box1 = (Expression<BoundingBox>) exprs[0];
        pattern = matchedPattern;
        if (pattern <= 2) {
            box2 = (Expression<BoundingBox>) exprs[1];
            full = (parser.mark == 1);
            not = (pattern == 2);
        } else {
            not = (parser.mark == 2);
            v = (Expression<Vector>) exprs[1];
        }
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "bounding box intersection";
    }

    @Override
    public boolean check(Event e) {
        BoundingBox b1 = box1.getSingle(e);
        if (b1 != null) {
            if (pattern <= 2) {
                BoundingBox b2 = box2.getSingle(e);
                if (b2 != null) {
                    boolean bool = (full) ? b1.contains(b2) : b1.overlaps(b2);
                    return (not != bool);
                }
            } else {
                for (Vector vector: v.getArray(e)) {
                    if (!b1.contains(vector) && !not) {
                        return false;
                    }
                    return !not;
                }
                return true;
            }
        }
        return false;
    }

}
