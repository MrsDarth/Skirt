package io.github.mrsdarth.skirt.elements.other.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Movement location")
@Description("Gets the location from and location to in a player move event, can be set")
@Examples({"on player move:",
        "\tsend \"%player% moved from %past move location% to %future move location%\" to {staff::*}"})
@Since("1.0.0")

public class ExprFromTo extends SimpleExpression<Location> {

    static {
        Skript.registerExpression(ExprFromTo.class, Location.class, ExpressionType.SIMPLE,
                "[event-][move[ment] ]location");
    }

    @Override
    protected @Nullable
    Location[] get(@NotNull Event e) {
        return e instanceof PlayerMoveEvent moveEvent ? CollectionUtils.array(getTime() == -1 ? moveEvent.getFrom() : moveEvent.getTo()) : null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "movement location";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        return getParser().isCurrentEvent(PlayerMoveEvent.class) && getTime() != 0;
    }

    @Override
    public @Nullable
    Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        return mode == ChangeMode.SET ? CollectionUtils.array(Location.class) : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, @NotNull ChangeMode mode) {
        if (delta != null && delta.length != 0 && delta[0] instanceof Location loc && e instanceof PlayerMoveEvent moveEvent) {
            if (getTime() == -1)
                moveEvent.setFrom(loc);
            else
                moveEvent.setTo(loc);
        }
    }
}