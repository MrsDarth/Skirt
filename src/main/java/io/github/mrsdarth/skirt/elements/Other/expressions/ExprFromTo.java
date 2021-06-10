package io.github.mrsdarth.skirt.elements.Other.expressions;


import ch.njol.util.coll.CollectionUtils;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;


import ch.njol.skript.ScriptLoader;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;

@Name("Get From/To")
@Description("Gets the location from and location to in a player or entity move event, can be set")
@Examples({"on player move:",
        "\tsend \"%player% moved from %getFrom% to %getTo%\" to {staff::*}"})
@Since("1.0.0")

public class ExprFromTo extends SimpleExpression<Location> {

    static {
        Skript.registerExpression(ExprFromTo.class, Location.class, ExpressionType.COMBINED,
                "(get|loc[ation])[ ](1¦From|2¦To)",
                "[the] (1¦(old|former|past|previous)|2¦(new|future|after)) location");
    }


    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    private boolean from, delay;
    private final String[] ft = {"From", "To"};

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        if (ScriptLoader.isCurrentEvent(PlayerMoveEvent.class) || ScriptLoader.isCurrentEvent(EntityMoveEvent.class)) {
            delay = isDelayed == Kleenean.TRUE;
            int mark = parser.mark;
            from = (mark == 1);
            return true;
        } else {
            Skript.error("Location From/To can only be used in a player or entity move event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return ("get From/To");
    }

    @Override
    @Nullable
    protected Location[] get(Event e) {
        return CollectionUtils.array((e instanceof PlayerMoveEvent) ? (from ?
                ((PlayerMoveEvent) e).getFrom() : ((PlayerMoveEvent) e).getTo()) : (from ?
                ((EntityMoveEvent) e).getFrom() : ((EntityMoveEvent) e).getTo()));
    }

    @Override
    public void change(Event event, Object[] delta, ChangeMode mode){
        if (delay) {
            Skript.error("Can't change getFrom/To after event already passed", ErrorQuality.SEMANTIC_ERROR);
            return;
        }
        if (delta != null) {
            Location loc = (Location) delta[0];
            if (event instanceof PlayerMoveEvent) {
                PlayerMoveEvent e = (PlayerMoveEvent) event;
                if (from) {
                    e.setFrom(loc);
                } else {
                    e.setTo(loc);
                }
            } else {
                EntityMoveEvent e = (EntityMoveEvent) event;
                if (from) {
                    e.setFrom(loc);
                } else {
                    e.setTo(loc);
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(final ChangeMode mode) {
        if (mode == ChangeMode.SET)
            return CollectionUtils.array(Location.class);
        return null;
    }
}