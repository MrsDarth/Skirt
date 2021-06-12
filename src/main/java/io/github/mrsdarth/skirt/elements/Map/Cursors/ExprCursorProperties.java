package io.github.mrsdarth.skirt.elements.Map.Cursors;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.VectorMath;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.elements.Other.expressions.ExprVecFromDir;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.Nullable;

@Name("Map Cursor Properties")
@Description("the properties of a map cursor, can be changed")
@Examples({
        "set {_cursor} to new white pointer map cursor",
        "add {_cursor} to map cursors of {canvas}",
        "loop 100 times:",
        "\tadd 1 to cursor rotation of {_cursor}"
})
@Since("1.2.0")

public class ExprCursorProperties extends SimplePropertyExpression<MapCursor, Object> {

    static {
        register(ExprCursorProperties.class, Object.class, "cursor (1¦type|2¦visibility|3¦x|4¦y|5¦direction|6¦rotation|7¦(name|caption))", "mapcursors");
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        super.init(exprs, matchedPattern, isDelayed, parseResult);
        pattern = parseResult.mark;
        return true;
    }

    @Override
    protected String getPropertyName() {
        switch (pattern) {
            case 1:
                return "type";
            case 2:
                return "visibility";
            case 3:
                return "x";
            case 4:
                return "y";
            case 5:
                return "direction";
            case 6:
                return "rotation";
            case 7:
                return "name";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public Object convert(MapCursor cursor) {
        switch (pattern) {
            case 1:
                return cursor.getType();
            case 2:
                return cursor.isVisible();
            case 3:
                return cursor.getX();
            case 4:
                return cursor.getY();
            case 5:
                return VectorMath.fromYawAndPitch(VectorMath.fromSkriptYaw(22.5f * cursor.getDirection()), 0);
            case 6:
                return cursor.getDirection();
            case 7:
                return cursor.getCaption();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getReturnType() {
        switch (pattern) {
            case 1:
                return MapCursor.Type.class;
            case 2:
                return Boolean.class;
            case 3:
            case 4:
            case 6:
                return Number.class;
            case 5:
                return Direction.class;
            case 7:
                return String.class;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        Class<?> type = getReturnType();
        switch (mode) {
            case ADD:
            case REMOVE:
                return (type.equals(Number.class)) ? CollectionUtils.array(Number.class) : null;
            case RESET:
            case DELETE:
                if (pattern != 7) break;
            case SET:
                return CollectionUtils.array(type);
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        for (MapCursor cursor : getExpr().getArray(e)) {
            boolean rd = (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE);
            if (cursor == null || (!rd && delta == null)) return;
            boolean add = mode == Changer.ChangeMode.ADD;
            if ((add || mode == Changer.ChangeMode.REMOVE) && (delta[0] instanceof Number)) {
                int change = ((Number) delta[0]).intValue() * (add ? 1 : -1);
                Number value =
                        (pattern == 3) ? cursor.getX() + change :
                                (pattern == 4) ? cursor.getY() + change :
                                        (cursor.getDirection() + change) % 16;
                change(e, CollectionUtils.array(value), Changer.ChangeMode.SET);
            } else {
                switch (pattern) {
                    case 1:
                        cursor.setType((MapCursor.Type) delta[0]);
                        break;
                    case 2:
                        cursor.setVisible((Boolean) delta[0]);
                        break;
                    case 3:
                        cursor.setX(((Number) delta[0]).byteValue());
                        break;
                    case 4:
                        cursor.setY(((Number) delta[0]).byteValue());
                        break;
                    case 5:
                        Location l = ExprVecFromDir.zero();
                        cursor.setDirection((byte) (Math.round((l.setDirection((((Direction) delta[0]).getDirection(l))).getYaw() + 180) / 22.5) % 16));
                        break;
                    case 6:
                        cursor.setDirection(((Number) delta[0]).byteValue());
                        break;
                    case 7:
                        if (rd) cursor.setCaption(null);
                        else cursor.setCaption((String) delta[0]);
                }
            }
        }
    }
}
