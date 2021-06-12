package io.github.mrsdarth.skirt.elements.OtherOther.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Move entity")
@Description({"Similar to teleporting but keeps their momentum and works even if there are passengers on the entity",
        "the location must be in the same world as the entity"})
@Examples("move player 10 above player")
@Since("1.1.0")

public class EffMoveEntity extends Effect {

    static {
        Skript.registerEffect(EffMoveEntity.class,
                "(move|set location of) %entities% (to|%directions%) %location%");
    }

    private Expression<Entity> entities;
    private Expression<Location> loc;

    @Override
    protected void execute(Event event) {
        Location l = loc.getSingle(event);
        if (l == null) return;
        for (Entity e : entities.getArray(event)) {
            Reflectness.move(e, l);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "move entity";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        entities = (Expression<Entity>) exprs[0];
        loc = Direction.combine((Expression<? extends Direction>) exprs[1], (Expression<? extends Location>) exprs[2]);
        return true;
    }
}
