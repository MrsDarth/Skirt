package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Convert")
@Description({"Used for converting variables to their types to allow changes since you cannot do things like set {_block} to air when the variable is a block",
        "can also be used to filter out a specific type from a list"})
@Examples({"delete {_e::*} converted to entities",
        "set ({_blocks::*} converted to blocks) to air"})
@Since("1.1.0")

public class ExprConvert extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprConvert.class, Object.class, ExpressionType.COMBINED,
                "%objects% converted to %*classinfo%");
    }

    @Nullable
    @Override
    protected Object[] get(Event event) {
        return set.getAll(event);
    }

    @Override
    public boolean isSingle() {
        return set.isSingle();
    }

    @Override
    public Class getReturnType() {
        return set.getReturnType();
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "converted type";
    }

    private Expression<?> set;

    @Override
    public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
        final Expression<?> expr = exprs[0].getConvertedExpression((((Literal<ClassInfo<?>>) exprs[1]).getSingle()).getC());
        if (expr == null)
            return false;
        set = expr;
        return true;
    }
}
