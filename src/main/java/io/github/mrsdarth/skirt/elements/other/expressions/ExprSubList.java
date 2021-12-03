package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.WrapperExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;

@SuppressWarnings("unchecked")
public class ExprSubList<T> extends WrapperExpression<T> {

    static {
        Skript.registerExpression(ExprSubList.class, Object.class, ExpressionType.COMBINED,
                "sub[ ]list of %objects% from ind(ice[s]|ex[es]) %number% to %number%",
                "(first|1¦last) %number% elements of %objects%");
    }

    private Expression<Number> numberExpr1, numberExpr2;

    private int pattern;

    @Override
    protected T[] get(@NotNull Event e) {
        Number n1 = numberExpr1.getSingle(e), n2 = numberExpr2 == null ? null : numberExpr2.getSingle(e);
        T[] array = super.get(e);
        return switch (pattern) {
            case 0 -> (n1 == null || n2 == null) ? null : Arrays.copyOfRange(array, max(n1.intValue() - 1, 0), max(n2.intValue() - 1, 0));
            case 1 -> n1 == null ? null : Arrays.copyOf(array, min(max(n1.intValue() - 1, 0), array.length));
            case 2 -> n1 == null ? null : Arrays.copyOfRange(array, array.length - min(max(n1.intValue() - 1, 0), array.length), array.length);
            default -> null;
        };
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return pattern == 0 ? "sublist of " + getExpr().toString(e, debug) + " from index " + numberExpr1.toString(e, debug) + " to " + numberExpr2.toString(e, debug) :
                (pattern == 1 ? "first " : "last ") + numberExpr1.toString(e, debug) + " elements of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr((Expression<? extends T>) exprs[matchedPattern]);
        if (matchedPattern == 0) {
            numberExpr1 = (Expression<Number>) exprs[1];
            numberExpr2 = (Expression<Number>) exprs[2];
        } else
            numberExpr1 = (Expression<Number>) exprs[0];
        pattern = matchedPattern + parseResult.mark;
        return true;
    }
}
