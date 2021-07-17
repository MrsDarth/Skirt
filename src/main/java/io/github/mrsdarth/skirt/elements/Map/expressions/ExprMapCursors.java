package io.github.mrsdarth.skirt.elements.Map.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.elements.Map.sections.SecMapEdit;
import io.github.mrsdarth.skirt.elements.Util.EffectSection;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.jetbrains.annotations.Nullable;

@Name("Map Cursors")
@Description("returns the map cursors of a map canvas. Can be set, add, remove, reset, delete")
@Examples("add banner red map cursor to map cursors")
@Since("1.2.0")

public class ExprMapCursors extends SimpleExpression<MapCursor> {

    static {
        Skript.registerExpression(ExprMapCursors.class, MapCursor.class, ExpressionType.COMBINED,
                "[all][[of] the] map cursors [(of|on) %-mapcanv%]");
    }

    private Expression<MapCanvas> canvas;

    @Nullable
    @Override
    protected MapCursor[] get(Event event) {
        MapCanvas c = SecMapEdit.getCanvas(event, canvas);
        if (c == null) return null;
        MapCursorCollection collection = c.getCursors();
        int size = collection.size();
        MapCursor[] cursors = new MapCursor[size];
        for (int i = 0; i < size; i++) {
            cursors[i] = collection.getCursor(i);
        }
        return cursors;

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends MapCursor> getReturnType() {
        return MapCursor.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "map cursors";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!EffectSection.isCurrentSection(SecMapEdit.class) && exprs[0] == null) {
            Skript.error("You can only use map cursors without specifying canvas within a map edit section");
            return false;
        }
        canvas = (Expression<MapCanvas>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case ADD:
            case REMOVE:
            case DELETE:
            case RESET:
                return CollectionUtils.array(MapCursor.class);
        }
        return null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        MapCanvas c = SecMapEdit.getCanvas(e, canvas);
        if (c == null) return;
        switch (mode) {
            case SET:
                MapCursorCollection collection0 = new MapCursorCollection();
                for (Object cursor : delta) {
                    if (cursor instanceof MapCursor) collection0.addCursor((MapCursor) cursor);
                }
                c.setCursors(collection0);
                break;
            case ADD:
                MapCursorCollection collection1 = c.getCursors();
                for (Object cursor : delta) {
                    if (cursor instanceof MapCursor) collection1.addCursor((MapCursor) cursor);
                }
                break;
            case REMOVE:
                MapCursorCollection collection2 = c.getCursors();
                for (Object cursor : delta) {
                    if (cursor instanceof MapCursor) collection2.removeCursor((MapCursor) cursor);
                }
            default:
                c.setCursors(new MapCursorCollection());
        }
    }
}


