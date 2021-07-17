package io.github.mrsdarth.skirt.elements.Map.MapStates;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Map State Map")
@Description("Allows you to get or change the map of a map state to be played on different maps")
@Examples("set map of map state {_map state} to 0 # or just map from id 0")
@Since("1.2.3")

public class ExprMapViewOfMapState extends SimplePropertyExpression<MapState, MapView> {

    static {
        Skript.registerExpression(ExprMapViewOfMapState.class, MapView.class, ExpressionType.PROPERTY,
                "map of map state %mapstates%",
                "map state %mapstates%'[s] map");
    }


    @Override
    protected String getPropertyName() {
        return "map";
    }

    @Nullable
    @Override
    public MapView convert(MapState mapState) {
        return mapState.getMapView();
    }

    @Override
    public Class<? extends MapView> getReturnType() {
        return MapView.class;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(MapView.class, Number.class) : null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        MapView mapView = delta[0] instanceof MapView ? (MapView) delta[0] : delta[0] instanceof Number ? Renderer.getMap(((Number) delta[0]).intValue()) : null;
        if (mapView == null) return;
        for (MapState m: getExpr().getArray(e))
            m.setMapView(mapView);
    }
}
