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
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.elements.Map.sections.SecEditCanvas;
import io.github.mrsdarth.skirt.elements.Util.EffectSection;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.jetbrains.annotations.Nullable;

@Name("Map Pixels")
@Description({"returns pixels of map, can be set to a number if you know the map colors or a skript/rgb color", "map pixels range from 1 to 128"})
@Examples({
        "edit {_map} in {_canvas}",
        "set pixels between (1,1) and (128,128) on {_canvas} to black",
        "set pixel at 100, 100 on {_canvas} to rgb(255,0,0)",
        "set all pixels of {_canvas} to 30"
})
@Since("1.2.0")

public class ExprMapPixel extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprMapPixel.class, Number.class, ExpressionType.COMBINED,
                "pixel at [\\(][x[ ]]%number%,[ ][y[ ]]%number%[\\)] [(of|on) %-mapcanv%]",
                "pixels (1¦between|2¦within) [\\(][x[ ]]%number%,[ ][y[ ]]%number%[\\)] and [\\(][x[ ]]%number%,[ ][y[ ]]%number%[\\)] [(of|on) %-mapcanv%]",
                "all pixels [(of|on) %-mapcanv%]");
    }

    private boolean isSingle, isAll, isLine;
    private Expression<Number> ex1, ey1, ex2, ey2;
    private Expression<MapCanvas> canvas;

    @Nullable
    @Override
    protected Number[] get(Event e) {
        MapCanvas canv = ExprMapCanvas.getCanvas(e, canvas);
        if (canv != null && isSingle) {
            Number
                    x = ex1.getSingle(e),
                    y = ey1.getSingle(e);
            if (x != null && y != null) return CollectionUtils.array(canv.getPixel(x.intValue(), y.intValue()));
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "map pixel";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        canvas = (Expression<MapCanvas>) exprs[exprs.length - 1];
        if (!EffectSection.isCurrentSection(SecEditCanvas.class) && canvas == null) {
            Skript.error("Cannot use pixels outside a map edit section without specifying canvas");
            return false;
        }
        isSingle = i == 0;
        isAll = i == 2;
        if (!isAll) {
            ex1 = (Expression<Number>) exprs[0];
            ey1 = (Expression<Number>) exprs[1];
            if (!isSingle) {
                isLine = parseResult.mark == 1;
                ex2 = (Expression<Number>) exprs[2];
                ey2 = (Expression<Number>) exprs[3];
            }
        }
        return true;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case RESET:
            case DELETE:
                return CollectionUtils.array(Number.class, Color.class);
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        MapCanvas canv = ExprMapCanvas.getCanvas(e, canvas);
        if (delta == null || canv == null) return;
        byte px = -1;
        if (mode == Changer.ChangeMode.SET) {
            if (delta[0] instanceof Color) {
                org.bukkit.Color c = ((Color) delta[0]).asBukkitColor();
                px = MapPalette.matchColor(c.getRed(), c.getGreen(), c.getBlue());
            } else if (delta[0] instanceof Number) px = ((Number) delta[0]).byteValue();
        }
        if (isAll) {
            System.out.println("pixel: " + px);
            for (int x = 0; x < 128; x++) {
                for (int y = 0; y < 128; y++) {
                    canv.setPixel(x, y, px);
                }
            }
        } else {
            Number
                    nx1 = ex1.getSingle(e),
                    ny1 = ey1.getSingle(e);
            if (nx1 == null || ny1 == null) return;
            if (isSingle) {
                canv.setPixel(nx1.intValue() - 1, ny1.intValue() - 1, px);
            } else {

                Number
                        nx2 = ex2.getSingle(e),
                        ny2 = ey2.getSingle(e);
                if (nx2 == null || ny2 == null) return;
                int
                        x0 = nx1.intValue() - 1,
                        y0 = ny1.intValue() - 1,
                        x1 = nx2.intValue() - 1,
                        y1 = ny2.intValue() - 1;
                if (isLine) {
                    double
                            dx, dy,
                            vx = x1 - x0,
                            vy = y1 - y0;
                    int m = (int) Math.sqrt((vx * vx) + (vy * vy));
                    dx = vx /= m;
                    dy = vy /= m;
                    canv.setPixel(x0, y0, px);
                    for (int i = 0; i < m; i++) {
                        canv.setPixel(x0 + (int) vx, y0 + (int) vy, px);
                        vx += dx;
                        vy += dy;
                    }
                } else {
                    int
                            sx = Math.min(x0, x1),
                            mx = Math.max(x0, x1),
                            sy = Math.min(y0, y1),
                            my = Math.max(y0, y1);
                    for (int x = sx; x <= mx; x++) {
                        for (int y = sy; y <= my; y++) {
                            canv.setPixel(x, y, px);
                        }
                    }
                }
            }
        }
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(max, Math.max(value, min));
    }


}
