package io.github.mrsdarth.skirt.elements.Map.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import io.github.mrsdarth.skirt.elements.Map.sections.SecMapEdit;
import io.github.mrsdarth.skirt.elements.Util.EffectSection;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Map stop editing")
@Description("removes the changes you have made to a map. note that the map will only revert to its original state if you partially edited it.")
@Examples("stop editing {_map}")
@Since("1.2.0")

public class EffStopEdit extends Effect {

    static {
        Skript.registerEffect(EffStopEdit.class,
                "stop edit[ing] [map] [%-map%]");
    }

    @Override
    protected void execute(Event e) {
        MapView map = mapView != null ? mapView.getSingle(e) : SecMapEdit.getCanvas(e, null).getMapView();
        if (map != null) {
            Renderer.clear(map, false);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "stop edit map";
    }

    private Expression<MapView> mapView;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mapView = (Expression<MapView>) expressions[0];
        if (mapView == null && !EffectSection.isCurrentSection(SecMapEdit.class)) {
            Skript.error("You cannot have stop edit without specifying map outside an edit section");
            return false;
        }
        return true;
    }
}
