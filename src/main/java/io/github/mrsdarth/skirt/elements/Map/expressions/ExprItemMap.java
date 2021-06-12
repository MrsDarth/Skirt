package io.github.mrsdarth.skirt.elements.Map.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Item from map")
@Description("returns a filled map item with the specified map")
@Examples("give player item with map from id 0")
@Since("1.2.0")

public class ExprItemMap extends PropertyExpression<MapView, ItemType> {

    static {
        Skript.registerExpression(ExprItemMap.class, ItemType.class, ExpressionType.PROPERTY,
                "[[filled] map] item[s] (of|from|with) %maps%");
    }

    @Override
    protected ItemType[] get(Event event, MapView[] mapViews) {
        ItemType[] items = new ItemType[mapViews.length];
        int i = 0;
        for (MapView map : mapViews) {
            ItemType item = new ItemType(Material.FILLED_MAP);
            MapMeta mapMeta = (MapMeta) item.getItemMeta();
            mapMeta.setMapView(map);
            item.setItemMeta(mapMeta);
            items[i++] = item;
        }
        return items;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "item from map";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends MapView>) expressions[0]);
        return true;
    }
}
