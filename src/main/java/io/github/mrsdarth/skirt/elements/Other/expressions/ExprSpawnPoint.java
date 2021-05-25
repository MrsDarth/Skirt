package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Bed Spawn Point")
@Description("returns the player's bed spawn point")
@Examples("delete player's spawn point")
@Since("1.1.0")

public class ExprSpawnPoint extends SimplePropertyExpression<Player, Location> {

    static {
        register(ExprSpawnPoint.class, Location.class, "[bed] spawn point", "players");
    }

    @Override
    protected String getPropertyName() {
        return "bed spawn point";
    }

    @Nullable
    @Override
    public Location convert(Player player) {
        return player.getBedSpawnLocation();
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case RESET:
            case DELETE:
                return CollectionUtils.array(Location.class);
        }
        return null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        Location loc = delta != null ? (Location) delta[0] : null;
        for (Player p: getExpr().getArray(e)) {
            p.setBedSpawnLocation(loc);
        }
    }

}
