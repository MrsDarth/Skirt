package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Values of Type")
@Description("get all the values of a type eg. statistic, inventory type, firework type")
@Examples("set biome at player to random biome of all values of type biome")
@Since("2.0.0")

@SuppressWarnings("unchecked")
public class ExprValuesOfType<T extends Enum<T>> extends SimpleExpression<T> {

    static {
        Skript.registerExpression(ExprValuesOfType.class, Object.class, ExpressionType.SIMPLE,
                "[all] values of type %*classinfo%");
    }

    private Literal<ClassInfo<? extends T>> classInfoLiteral;

    @Override
    protected @Nullable
    T[] get(@NotNull Event e) {
        return classInfoLiteral.getSingle().getC().getEnumConstants();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends T> getReturnType() {
        return classInfoLiteral.getSingle().getC();
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "all values of type " + classInfoLiteral.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        classInfoLiteral = (Literal<ClassInfo<? extends T>>) exprs[0];
        if (classInfoLiteral.getSingle().getC().isEnum())
            return true;
        Skript.error("this type does not have values");
        return false;
    }
}
