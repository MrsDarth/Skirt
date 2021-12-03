package io.github.mrsdarth.skirt.elements.boundingbox.expressions;

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
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;


@Name("Bounding Box outline")
@Description("Returns a list of vectors in the shape of a bounding box, or between locations")
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

public class ExprBoundingBoxOutline extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBoundingBoxOutline.class, Object.class, ExpressionType.COMBINED,
                "[vector] box outline of %boundingboxes% [with density %-number%]",
                "vector line between %vector% and %vector% [with density %-number%]",
                "location line between %location% and %location% [with density %-number%]",
                "[all] [outline] points of %boundingboxes%");
    }

    private Expression<BoundingBox> boxExpr;
    private Expression<Number> numExpr;
    private Expression<Vector> vectorExpr1, vectorExpr2;
    private Expression<Location> locationExpr1, locationExpr2;

    private int pattern;

    @Override
    protected @Nullable
    Object[] get(@NotNull Event e) {
        Optional<Number> number = Skirtness.getSingle(numExpr, e);
        return switch (pattern) {
            case 0 -> number.map(Number::doubleValue)
                    .map(density -> Arrays.stream(boxExpr.getArray(e))
                    .flatMap(box -> getOutline(box, density))
                    .flatMap(Arrays::stream)
                    .toArray(Vector[]::new))
                    .orElse(null);
            case 1 -> {
                Vector pos1 = vectorExpr1.getSingle(e), pos2 = vectorExpr2.getSingle(e);
                yield (number.isEmpty() || pos1 == null || pos2 == null) ? null : getLine(pos1, pos2, number.get().doubleValue());
            }
            case 2 -> {
                Location pos1 = locationExpr1.getSingle(e), pos2 = locationExpr2.getSingle(e);
                if (number.isEmpty() || pos1 == null || pos2 == null) yield null;
                World world = pos1.getWorld();
                yield world.equals(pos2.getWorld()) ?
                        Arrays.stream(getLine(pos1.toVector(), pos2.toVector(), number.get().doubleValue()))
                        .map(vector -> vector.toLocation(world))
                        .toArray(Location[]::new) : null;
            }
            case 3 -> Arrays.stream(boxExpr.getArray(e))
                    .map(ExprBoundingBoxOutline::getPoints)
                    .flatMap(Arrays::stream)
                    .toArray(Vector[]::new);
            default -> null;
        };
    }

    private static Vector[] getLine(Vector pos1, Vector pos2, double density) {
        int points = (int) (pos1.distance(pos2) * density);
        Vector[] line = new Vector[points];
        pos1.subtract(pos2).multiply(1/(double) points);
        line[0] = pos2;
        for (int i = 1; i <= points; i++)
            line[i] = pos1.clone().multiply(i).add(pos2);
        return line;
    }

    private static Vector[] getPoints(BoundingBox box) {
        return new Vector[] {
                new Vector(box.getMinX(), box.getMinY(), box.getMinZ()),
                new Vector(box.getMaxX(), box.getMinY(), box.getMinZ()),
                new Vector(box.getMinX(), box.getMaxY(), box.getMinZ()),
                new Vector(box.getMinX(), box.getMinY(), box.getMaxZ()),
                new Vector(box.getMaxX(), box.getMaxY(), box.getMaxZ()),
                new Vector(box.getMinX(), box.getMaxY(), box.getMaxZ()),
                new Vector(box.getMaxX(), box.getMinY(), box.getMaxZ()),
                new Vector(box.getMaxX(), box.getMaxY(), box.getMinZ())
        };
    }

    private static Stream<Vector[]> getOutline(BoundingBox box, double density) {
        Vector[] points = getPoints(box);
        return Stream.of(
                getLine(points[0], points[1], density),
                getLine(points[0], points[2], density),
                getLine(points[0], points[3], density),
                getLine(points[7], points[1], density),
                getLine(points[7], points[2], density),
                getLine(points[7], points[4], density),
                getLine(points[6], points[1], density),
                getLine(points[6], points[3], density),
                getLine(points[6], points[4], density),
                getLine(points[5], points[2], density),
                getLine(points[5], points[3], density),
                getLine(points[5], points[4], density)
        );
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return pattern == 2 ? Location.class : Vector.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        String density = numExpr == null ? "" : " with density " + numExpr.toString(e, debug);
        return switch (pattern) {
            case 0 -> "box outline of " + boxExpr.toString(e, debug) + density;
            case 1 -> "vector line between " + vectorExpr1.toString(e, debug) + " and " + vectorExpr2.toString(e, debug) + density;
            case 2 -> "location line between " + locationExpr1.toString(e, debug) + " and " + locationExpr2.toString(e, debug) + density;
            case 3 -> "all outline points of " + boxExpr.toString(e, debug);
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        pattern = matchedPattern;
        switch (matchedPattern) {
            case 0 -> {
                boxExpr = (Expression<BoundingBox>) exprs[0];
                numExpr = (Expression<Number>) exprs[1];
            }
            case 1 -> {
                vectorExpr1 = (Expression<Vector>) exprs[0];
                vectorExpr2 = (Expression<Vector>) exprs[1];
                numExpr = (Expression<Number>) exprs[2];
            }
            case 2 -> {
                locationExpr1 = (Expression<Location>) exprs[0];
                locationExpr2 = (Expression<Location>) exprs[1];
                numExpr = (Expression<Number>) exprs[2];
            }
            case 3 -> boxExpr = (Expression<BoundingBox>) exprs[0];
        }
        return true;
    }
}
