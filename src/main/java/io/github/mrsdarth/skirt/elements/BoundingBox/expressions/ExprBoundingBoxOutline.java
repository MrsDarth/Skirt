package io.github.mrsdarth.skirt.elements.BoundingBox.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;


@Name("Bounding Box outline")
@Description("Returns a list of vectors in the shape of a bounding box")
@Examples({"command /blockshape:",
        "\ttrigger:",
        "",
        "\t\tset {_box} to bounding box of (exact target block)",
        "\t\tset {_vectors::*} to box outline of {_box} with density 5",
        "\t\tset {_locations::*} to {_vectors::*} as locations",
        "",
        "\t\tloop 10 times:",
        "\t\t\tplay happy villager at {_locations::*}",
        "\t\t\twait 2 ticks"})
@Since("1.0.0")

public class ExprBoundingBoxOutline extends SimpleExpression<Vector> {

    static {
        Skript.registerExpression(ExprBoundingBoxOutline.class, Vector.class, ExpressionType.COMBINED,
                "[vector] box outline of %boundingboxes%[ with density %-number%]",
                "vector line between %vector% and %vector%[ with density %-number%]");
    }

    private Expression<BoundingBox> boxes;
    private Expression<Number> density;
    private Expression<Vector> vector1;
    private Expression<Vector> vector2;
    private int pattern;

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    public boolean isSingle() {
        return (pattern == 0 && boxes.isSingle());
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        boxes = (Expression<BoundingBox>) exprs[0];
        if (pattern == 0) {
            density = (Expression<Number>) exprs[1];
        } else {
            vector1 = (Expression<Vector>) exprs[0];
            vector2 = (Expression<Vector>) exprs[1];
            density = (Expression<Number>) exprs[2];
        }
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "vector box outline";
    }

    @Override
    @Nullable
    protected Vector[] get(Event event) {
        ArrayList<Vector> vlist = new ArrayList<Vector>();
        Number d1 = (density != null) ? density.getSingle(event) : 1;
        if (d1 != null) {
            double d = d1.doubleValue();
            if (pattern == 0) {
                BoundingBox[] boxlist = boxes.getArray(event);
                for (BoundingBox box : boxlist) {
                    vlist.addAll(Arrays.asList(boxoutline(box, d)));
                }
                return vlist.toArray(new Vector[vlist.size()]);
            } else {
                Vector v1 = vector1.getSingle(event);
                Vector v2 = vector2.getSingle(event);
                if (v1 != null && v2 != null) {
                    return line(v1, v2, d);
                }
            }
        }
        return null;
    }

    private Vector[] boxoutline(BoundingBox bbox, double d) {
        ArrayList<Vector> vectors = new ArrayList<Vector>();
        Vector[] box = boxpoints(bbox.getMin(), bbox.getMax());
        int[][] ints = {{0, 1}, {0, 2}, {0, 3}, {1, 4}, {1, 5}, {2, 4}, {2, 6}, {3, 5}, {3, 6}, {4, 7}, {5, 7}, {6, 7}};
        for (int[] i : ints) {
            vectors.addAll(Arrays.asList(line(box[i[0]], box[i[1]], d)));
        }
        return vectors.toArray(new Vector[vectors.size()]);
    }

    private Vector[] boxpoints(Vector v1, Vector v2) {
        Vector[] vecs = CollectionUtils.array(Vector.getMinimum(v1, v2), Vector.getMaximum(v1, v2));
        Vector[] v = new Vector[8];
        int[][] mm = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {1, 1, 0}, {1, 0, 1}, {0, 1, 1}};
        v[0] = vecs[0];
        for (int i = 0; i < 6; i++) {
            v[i + 1] = new Vector(vecs[mm[i][0]].getX(), vecs[mm[i][1]].getY(), vecs[mm[i][2]].getZ());
        }
        v[7] = vecs[1];
        return v;
    }

    private Vector[] line(Vector v1, Vector v2, double density) {
        int points = 1 + ((int) (v1.distance(v2) * density));
        Vector vec1 = v1.clone().subtract(v2).multiply(1 / ((double) points));
        Vector[] vectors = new Vector[points + 1];
        vectors[0] = v2;
        for (int i = 1; i <= points; i++) {
            vectors[i] = (vec1.clone().multiply(i).add(v2));
        }
        return vectors;
    }


}
