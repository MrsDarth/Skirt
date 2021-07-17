package io.github.mrsdarth.skirt.elements.Other.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;


@Name("Timespan conversion")
@Description("returns a timespan in milliseconds ,ticks, seconds, minutes, hours, days, months or years")
@Examples("send \"&acompleted in %{_time} in milliseconds% ms")
@Since("1.0.1")

public class ExprTimespan extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprTimespan.class, Number.class, ExpressionType.COMBINED,
                "%timespans% in (1¦m[illisecond]s|2¦ticks|3¦sec[ond]s|4¦min[ute]s|5¦hours|6¦days|7¦months|8¦years)");
    }


    private Expression<Timespan> timespans;
    private double d;

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public boolean isSingle() {
        return timespans.isSingle();
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        timespans = (Expression<Timespan>) exprs[0];
        d = new double[] {1, 50, 1000, 60000, 3600000, 86400000, 2592000000d, 31536000000d}[parser.mark - 1];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "timespan to number";
    }

    @Override
    @Nullable
    protected Number[] get(Event event) {
        return timespans.stream(event).map(t -> t.getMilliSeconds()/d).toArray(Number[]::new);
    }
}