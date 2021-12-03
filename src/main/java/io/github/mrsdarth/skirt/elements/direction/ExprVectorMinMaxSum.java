package io.github.mrsdarth.skirt.elements.direction;

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
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.BinaryOperator;


@Name("Vector Min/Max/Sum")
@Description("Returns the maximum, minimum or sum of a list of vectors")
@Examples({"set {_max} to max of vectors (vector(1,2,3) and vector(2,0,1)) #returns vector(2,2,3)",
        "set {_sum} to sum of vectors (vector(1,2,3) and vector(2,0,1)) #returns vector(3,2,4)"})
@Since("1.0.0")

public class ExprVectorMinMaxSum extends SimpleExpression<Vector> {

    private static final BinaryOperator<Vector>[] REDUCERS = CollectionUtils.array(Vector::getMaximum, Vector::getMinimum, Vector::add);

    static {
        Skript.registerExpression(ExprVectorMinMaxSum.class, Vector.class, ExpressionType.COMBINED,
                "vector ((min|1¦max)[imum]|2¦sum) of %vectors%");
    }

    private Expression<Vector> vectorExpr;

    private int pattern;

    @Override
    protected @Nullable
    Vector[] get(@NotNull Event e) {
        return Arrays.stream(vectorExpr.getArray(e))
                .reduce(REDUCERS[pattern])
                .map(CollectionUtils::array)
                .orElse(null);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "vector " + switch (pattern) {
            case 0 -> "min";
            case 1 -> "max";
            case 2 -> "sum";
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        vectorExpr = (Expression<Vector>) exprs[0];
        pattern = parseResult.mark;
        return true;
    }
}