package io.github.mrsdarth.skirt.elements.other.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Name("Timespan conversion")
@Description("returns a timespan in milliseconds ,ticks, seconds, minutes, hours, days, months or years")
@Examples("send \"completed in %{_time} in milliseconds% ms")
@Since("1.0.1")

public class ExprTimespanConversion extends PropertyExpression<Timespan, Number> {

    private static final double[] DIVISORS = new double[] { 1, 50, 1000, 60000, 3600000, 86400000, 2592000000d, 31536000000d };

    static {
        Skript.registerExpression(ExprTimespanConversion.class, Number.class, ExpressionType.COMBINED,
                "%timespans% in (m[illisecond]s|1¦ticks|2¦sec[ond]s|3¦min[ute]s|4¦hours|5¦days|6¦months|7¦years)");
    }

    private int pattern;

    @Override
    protected Number @NotNull [] get(@NotNull Event e, Timespan @NotNull [] source) {
        return get(source, time -> time.getMilliSeconds() / DIVISORS[pattern]);
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return getExpr().toString(e, debug) + " in " + switch (pattern) {
            case 0 -> "milliseconds";
            case 1 -> "ticks";
            case 2 -> "seconds";
            case 3 -> "minutes";
            case 4 -> "hours";
            case 5 -> "days";
            case 6 -> "months";
            case 7 -> "years";
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr((Expression<? extends Timespan>) exprs[0]);
        pattern = parseResult.mark;
        return true;
    }
}