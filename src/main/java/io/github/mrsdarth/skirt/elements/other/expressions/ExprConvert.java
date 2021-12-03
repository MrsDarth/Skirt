package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.WrapperExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Convert")
@Description({"Used for converting variables to their types to allow changes since you cannot do things like set {_block} to air when the variable is a block",
        "can also be used to filter out a specific type from a list"})
@Examples({"delete {_e::*} converted to entities",
        "set ({_blocks::*} converted to blocks) to air"})
@Since("1.1.0")

public class ExprConvert extends WrapperExpression<Object> {

    static {
        Skript.registerExpression(ExprConvert.class, Object.class, ExpressionType.COMBINED,
                "%objects% converted to %*classinfo%");
    }

    private Literal<ClassInfo<?>> classInfoLiteral;


    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return getExpr().getSource().toString(e, debug) + " converted to " + classInfoLiteral.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        classInfoLiteral = ((Literal<ClassInfo<?>>) exprs[1]);
        Expression<?> expr = LiteralUtils.defendExpression(exprs[0]).getConvertedExpression(classInfoLiteral.getSingle().getC());
        if (expr == null) return false;
        setExpr(expr);
        return true;
    }
}
