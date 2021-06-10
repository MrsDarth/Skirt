package io.github.mrsdarth.skirt.elements.Map.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.map.MapView;

@Name("Map is Virtual")
@Description("whether the map is managed by a plugin or is a natural minecraft map")
@Examples("if map from id 0 is virtual:")
@Since("1.2.0")

public class CondVirtual extends PropertyCondition<MapView> {

    static {
        register(CondVirtual.class, "virtual", "maps");
    }

    @Override
    public boolean check(MapView mapView) {
        return mapView.isVirtual();
    }

    @Override
    protected String getPropertyName() {
        return "virtual";
    }
}
