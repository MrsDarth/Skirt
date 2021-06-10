package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Multiple of object")
@Description({"returns x of any object","3 times of stone would return stone, stone and stone"})
@Examples({"set {_text::*} to 5 times of \"hi\"","send join {_text::*}","#outputs hihihihihi"})
@Since("1.0.1")

public class ExprMultipleOf<T> extends SimpleExpression<T> {

    static {
        Skript.registerExpression(ExprMultipleOf.class, Object.class, ExpressionType.COMBINED,
                "%number% time[s] of %objects%"
        );
    }

    private ExprMultipleOf<?> source;

    private Expression<Number> num;
    @Nullable
    private Expression<Object> obj;
    private Class<? extends T>[] types;
    private Class<T> superType;

    public ExprMultipleOf() {
        this(null, (Class<? extends T>) Object.class);
    }

    private ExprMultipleOf(ExprMultipleOf<?> source, Class<? extends T>... types) {
        this.source = source;
        if (source != null) {
            this.num = source.num;
            this.obj = source.obj;
        }
        this.types = types;
        this.superType = (Class<T>) Utils.getSuperType(types);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        obj = (Expression<Object>) exprs[1];
        num = (Expression<Number>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected T[] get(Event e) {
        Number a = num.getSingle(e);
        if (a == null) return null;
        List<Object> result = new ArrayList<>(), repeat = Arrays.asList(obj.getArray(e));
        for (int i = 0; i < a.intValue(); i++) {
            result.addAll(repeat);
        }
        try {
            return Converters.convertArray(result.toArray(), types, superType);
        } catch (ClassCastException e1) {
            return (T[]) Array.newInstance(superType, 0);
        }
    }

    @Override
    public boolean isSingle() {
        return obj.isSingle() && (num instanceof Literal) && (((Literal<Number>) num).getSingle().intValue() == 1);
    }

    @Override
    public Class<? extends T> getReturnType() {
        return superType;
    }

    @Override
    public <R> Expression<? extends R> getConvertedExpression(Class<R>... to) {
        return new ExprMultipleOf<>(this, to);
    }

    @Override
    public Expression<?> getSource() {
        return source == null ? this : source;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "multiple of object";
    }

}
