package io.github.mrsdarth.skirt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Multiple of object")
@Description({"returns x of any object","3 times of stone would return stone, stone and stone"})
@Examples({"set {_text::*} to 5 times of \"hi\"","send join {_text::*}","#outputs hihihihihi"})
@Since("1.0.1")

public class ExprMultipleOf extends SimpleExpression {

    static {
        Skript.registerExpression(ExprMultipleOf.class, Object.class, ExpressionType.COMBINED,
                "%number% times of %objects%");
    }

    private Expression<Number> num;
    private Expression<Object> obj;

    @Nullable
    @Override
    protected Object[] get(Event event) {
        if (num.getSingle(event) == null || obj.getArray(event).length == 0) return null;
        int times = num.getSingle(event).intValue();
        List objects = Arrays.asList(obj.getArray(event));
        ArrayList all = new ArrayList();
        for (int i = 0; i < times; i++) {
            all.addAll(objects);
        }
        return all.toArray();

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "multiple of object";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        num = (Expression<Number>) exprs[0];
        obj = (Expression<Object>) exprs[1];
        return true;
    }
}
