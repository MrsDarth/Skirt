package io.github.mrsdarth.skirt.elements.Map.Cursors;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.elements.Map.expressions.ExprMapPixel;
import io.github.mrsdarth.skirt.elements.Other.expressions.ExprVecFromDir;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.Nullable;

@Name("New Map Cursor")
@Description("creates a new map cursor")
@Examples("add red x map cursor named \"Treasure Map\" to map cursors of {canvas}")
@Since("1.2.0")

public class ExprNewCursor extends SimpleExpression<MapCursor> {

    static {
        Skript.registerExpression(ExprNewCursor.class, MapCursor.class, ExpressionType.COMBINED,
                "[new] [(visible|1¦invisible)] %mapcursortype% map cursor [at [\\(][x[ ]]%-number%,[ ][y[ ]]%-number%[\\)]] [(facing|with rotation) %-direction/number%] [(named|with (name|caption)) %-string%]");
    }

    private boolean visible;
    private Expression<MapCursor.Type> type;
    private Expression<Number> ex, ey, rot;
    private Expression<?> direction;
    private Expression<String> name;

    @Nullable
    @Override
    protected MapCursor[] get(Event e) {
        MapCursor.Type cursortype = type.getSingle(e);
        if (cursortype == null) return null;
        Number
                nx = ex != null ? ex.getSingle(e) : 0,
                ny = ey != null ? ey.getSingle(e) : 0;
        byte
                x = nx != null ? nx.byteValue() : 0,
                y = ny != null ? ny.byteValue() : 0;
        byte d = 0;
        if (direction != null) {
            Object dir = direction.getSingle(e);
            if (dir instanceof Direction) {
                Location l = ExprVecFromDir.zero();
                d = (byte) (Math.round((l.setDirection((((Direction) dir).getDirection(l))).getYaw() + 180) / 22.5) % 16);
            } else if (dir instanceof Number) d = (byte) (ExprMapPixel.clamp(((Number) dir).intValue(), 0, 15));
        }
        return CollectionUtils.array(new MapCursor(x, y, d, cursortype, visible, name != null ? name.getSingle(e) : null));
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MapCursor> getReturnType() {
        return MapCursor.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new cursor";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        visible = parseResult.mark != 1;
        type = (Expression<MapCursor.Type>) exprs[0];
        ex = (Expression<Number>) exprs[1];
        ey = (Expression<Number>) exprs[2];
        direction = exprs[3];
        name = (Expression<String>) exprs[4];
        return true;
    }
}
