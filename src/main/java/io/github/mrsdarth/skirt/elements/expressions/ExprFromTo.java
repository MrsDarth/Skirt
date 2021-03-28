package io.github.mrsdarth.skirt.elements.expressions;


import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;


import ch.njol.skript.ScriptLoader;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import org.jetbrains.annotations.Nullable;


public class ExprFromTo extends SimpleExpression<Location> {

    static {
        Skript.registerExpression(ExprFromTo.class, Location.class, ExpressionType.COMBINED, "(get|loc[ation])[ ](1¦From|2¦To)");
    }


    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    private boolean from;
    private final String[] ft = {"From", "To"};

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        if (ScriptLoader.isCurrentEvent(PlayerMoveEvent.class)) {
            int mark = parser.mark;
            from = (mark == 1);
            return true;
        } else {
            Skript.error("Location From/To can only be used in a player move event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return ("get From/To");
    }

    @Override
    @Nullable
    protected Location[] get(Event event) {
        PlayerMoveEvent evt = (PlayerMoveEvent) event;
        Location loc = (from) ? evt.getFrom() : evt.getTo();
        return new Location[] {loc};
    }

    @Override
    public void change(Event event, Object[] delta, ChangeMode mode){
        if (delta != null) {
            Location loc = (Location) delta[0];
            PlayerMoveEvent e = (PlayerMoveEvent) event;
            if (from) {
                e.setFrom(loc);
            } else {
                e.setTo(loc);
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