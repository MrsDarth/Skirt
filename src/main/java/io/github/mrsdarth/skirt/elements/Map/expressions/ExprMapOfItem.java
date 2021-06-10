package io.github.mrsdarth.skirt.elements.Map.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Map of Item")
@Description("the map of an item")
@Examples({
        "set map of player's tool to new map from world of player",
        "set map of {_item} to 0 # same as setting it to map from id 0"
})
@Since("1.2.0")

public class ExprMapOfItem extends SimplePropertyExpression<ItemType, MapView> {

    static {
        register(ExprMapOfItem.class, MapView.class, "map", "itemtypes");
    }

    @Override
    protected String getPropertyName() {
        return "map";
    }

    @Nullable
    @Override
    public MapView convert(ItemType item) {
        ItemMeta itemMeta = item.getItemMeta();
        MapMeta mapMeta = (itemMeta instanceof MapMeta) ? ((MapMeta) itemMeta) : null;
        return (mapMeta != null && mapMeta.hasMapView()) ? mapMeta.getMapView() : null;
    }

    @Override
    public Class<? extends MapView> getReturnType() {
        return MapView.class;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case ADD:
            case REMOVE:
                return CollectionUtils.array(Number.class);
            case SET:
                return CollectionUtils.array(Number.class, MapView.class);
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        if (delta == null) return;
        byte f, d = 0;
        ItemMeta itemMeta;
        if (delta[0] instanceof Number) d = ((Number) delta[0]).byteValue();
        for (ItemType item: getExpr().getArray(e)) {
            itemMeta = item.getItemMeta();
            if (!(itemMeta instanceof MapMeta)) break;
            f = 1;
            MapMeta m = (MapMeta) itemMeta;
            switch (mode) {
                case REMOVE:
                    f *= -1;
                case ADD:
                    if (d != 0) m.setMapId(m.getMapId() + (d * f));
                    break;
                case SET:
                    if (d != 0) m.setMapId(d);
                    else m.setMapView((MapView) delta[0]);
            }
            item.setItemMeta(m);
        }
    }
}
