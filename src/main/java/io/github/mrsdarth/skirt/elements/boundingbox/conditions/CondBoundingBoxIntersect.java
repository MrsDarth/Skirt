package io.github.mrsdarth.skirt.elements.boundingbox.conditions;


import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Name("Bounding Box intersect")
@Description("Checks whether a bounding box intersects with another, fully contains another, or contains positions")
@Examples("if {_box} intersects bounding box of player:")
@Since("1.0.0")

public class CondBoundingBoxIntersect extends Condition {

    static {
        Skript.registerCondition(CondBoundingBoxIntersect.class,
                "[[bounding] box] %boundingbox% [(4¦does(n't| not))] (1¦fully contain[s]|2¦intersect[s][ with]) %boundingboxes%",
                "[[bounding] box] %boundingbox% (1¦contain|2¦does(n't| not) contain)[s] %vectors%");
    }


    private Expression<BoundingBox> boxExpr1, boxExpr2;
    private Expression<Vector> vectorExpr;

    private int pattern;

    private boolean not, full;


    @SuppressWarnings("unchecked")
    @Override

    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        boxExpr1 = (Expression<BoundingBox>) exprs[0];
        if ((pattern = matchedPattern) == 0) {
            not = parseResult.mark >>> 2 == 1;
            full = (parseResult.mark & 1) == 1;
            boxExpr2 = (Expression<BoundingBox>) exprs[1];
        } else {
            not = parseResult.mark == 2;
            vectorExpr = (Expression<Vector>) exprs[1];
        }
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "bounding box " + boxExpr1.toString(e, debug) + (not ? "does not " : "") + (pattern == 0 ?
                (full ? "fully contains " : "intersect with ") + boxExpr2.toString(e, debug) :
                "contains " + vectorExpr.toString(e, debug));
    }

    @Override
    public boolean check(@NotNull Event e) {
        return Skirtness.getSingle(boxExpr1, e).map(box -> (pattern == 0) ?
                boxExpr2.check(e, full ? box::contains : box::overlaps, not) :
                vectorExpr.check(e, box::contains, not)).orElse(not);
    }

}
