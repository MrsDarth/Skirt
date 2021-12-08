package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.WrapperExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.ArrayIterator;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

@Name("Multiple of object")
@Description({"returns x of any object", "3 times of stone would return stone, stone and stone"})
@Examples("send join 5 times of \"skirt\" # skirtskirtskirtskirtskirt")
@Since("1.0.1")

@SuppressWarnings("unchecked")
public class ExprMultipleOf<T> extends WrapperExpression<T> {

    static {
        Skript.registerExpression(ExprMultipleOf.class, Object.class, ExpressionType.COMBINED,
                "%number% times of %objects%",
                "%objects% repeated %number% times"
        );
    }

    private Expression<Number> numberExpr;

    @Override
    protected T[] get(@NotNull Event e) {
        int repeat;
        Number number = numberExpr.getSingle(e);
        if (number == null || (repeat = number.intValue()) < 1) return null;
        T[] array = super.getArray(e), newArray;
        if (repeat == 1) return array;
        if (array.length == 1) {
            newArray = Arrays.copyOf(array, repeat);
            Arrays.fill(newArray, 1, repeat, array[0]);
        } else {
            newArray = Arrays.copyOf(array, repeat * array.length);
            for (int i = 1; i < repeat; i++)
                System.arraycopy(array, 0, newArray, i * array.length, array.length);
        }
        return newArray;
    }

    @Override
    public @Nullable Iterator<? extends T> iterator(@NotNull Event e) {
        return new ArrayIterator<>(get(e));
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return numberExpr.toString(e, debug) + " times of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        numberExpr = (Expression<Number>) exprs[matchedPattern];
        setExpr((Expression<? extends T>) exprs[matchedPattern ^ 1]);
        return true;
    }
}