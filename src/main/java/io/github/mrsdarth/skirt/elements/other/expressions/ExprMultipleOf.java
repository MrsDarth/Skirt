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
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Multiple of object")
@Description({"returns x of any object", "3 times of stone would return stone, stone and stone"})
@Examples({"set {_text::*} to 5 times of \"hi\"", "send join {_text::*}", "#outputs hihihihihi"})
@Since("1.0.1")

@SuppressWarnings("unchecked")
public class ExprMultipleOf<T> extends WrapperExpression<T> {

    static {
        Skript.registerExpression(ExprMultipleOf.class, Object.class, ExpressionType.COMBINED,
                "%number% time[s] of %objects%"
        );
    }

    private Expression<Number> numberExpr;

    @Override
    protected T[] get(@NotNull Event e) {
        Number repeat = numberExpr.getSingle(e);
        if (repeat == null) return null;
        T[] array = super.getArray(e), newArray = Reflectness.newArray(getReturnType(), repeat.intValue() * array.length);
        for (int i = 0; i < repeat.intValue(); i++)
            System.arraycopy(array, 0, newArray, i * array.length, array.length);
        return newArray;
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
        numberExpr = (Expression<Number>) exprs[0];
        setExpr((Expression<? extends T>) exprs[1]);
        return true;
    }
}