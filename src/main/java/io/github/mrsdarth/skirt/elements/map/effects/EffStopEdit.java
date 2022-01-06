package io.github.mrsdarth.skirt.elements.map.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.elements.map.SkirtRenderer;
import io.github.mrsdarth.skirt.skriptv2_6.sections.SecMapEdit;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Map stop editing")
@Description("removes the changes you have made to a map. note that the map will only revert to its original state if you partially edited it.")
@Examples("stop editing {_map}")
@Since("1.2.0")

public class EffStopEdit extends Effect {

    static {
        Skript.registerEffect(EffStopEdit.class, "stop editing [map] [%-maps%]");
    }

    private SecMapEdit mapEditSection;

    private Expression<MapView> mapExpr;

    @Override
    protected void execute(@NotNull Event e) {
        if (mapExpr == null) {
            MapCanvas canvas = mapEditSection == null ? null : mapEditSection.getCanvas();
            if (canvas != null) stopEditing(canvas.getMapView());
        } else {
            for (MapView map: mapExpr.getArray(e))
                stopEditing(map);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "stop editing " + (mapExpr == null ? "map" : mapExpr.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        if (!Skirtness.isSkriptv2_6())
            mapEditSection = getParser().getCurrentSection(SecMapEdit.class);
        mapExpr = (Expression<MapView>) exprs[0];
        if (mapExpr == null && mapEditSection == null) {
            Skript.error("You need to specify a map canvas when outside a map edit section");
            return false;
        }
        return true;
    }

    private static void stopEditing(MapView mapView) {
        for (MapRenderer renderer: mapView.getRenderers())
            if (renderer instanceof SkirtRenderer)
                mapView.removeRenderer(renderer);
    }

}
