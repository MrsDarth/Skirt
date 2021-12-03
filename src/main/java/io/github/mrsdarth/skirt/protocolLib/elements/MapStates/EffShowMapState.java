package io.github.mrsdarth.skirt.protocolLib.elements.MapStates;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.protocolLib.PLib;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Show Map State")
@Description("sends a map state to a player")
@Examples("show map state {_map state} to player")
@Since("1.2.3")

public class EffShowMapState extends Effect {

    static {
        Skript.registerEffect(EffShowMapState.class,
                "(show|send) map[ ]state %mapstate% to %players%",
                "refresh %players%['[s]] view of %maps%");
    }

    private Expression<MapState> mapStateExpr;
    private Expression<MapView> mapExpr;
    private Expression<Player> playerExpr;

    private boolean refresh;

    @Override
    protected void execute(@NotNull Event e) {
        Player[] players = playerExpr.getArray(e);
        if (refresh)
            for (Player player: players)
                for (MapView mapView: mapExpr.getArray(e))
                    player.sendMap(mapView);
        else
            for (MapState mapState: mapStateExpr.getArray(e))
                PLib.sendPacket(mapState.getMapPacket(), players);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return refresh ? "refresh " + playerExpr.toString(e, debug) + "'s view of " + mapExpr.toString(e, debug) : "send map state " + mapStateExpr.toString(e, debug) + " to " + playerExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        playerExpr = (Expression<Player>) exprs[matchedPattern ^ 1];
        refresh = matchedPattern == 1;
        if (refresh) mapStateExpr = (Expression<MapState>) exprs[0];
        else mapExpr = (Expression<MapView>) exprs[1];
        return true;
    }
}
