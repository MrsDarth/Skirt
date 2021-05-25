package io.github.mrsdarth.skirt.elements.Map.Image.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.awt.Image;
import java.awt.Graphics;


public class EffDrawShape extends Effect {

    static {
        Skript.registerEffect(EffDrawShape.class,
                "draw [a] %color% ([(10¦outline of)] (1¦rectangle|2¦oval)|3¦line) (between|from) [x] %number%, [y] %number% (and|to) [x] %number%, [y] %number% on %image%",
                "clear rectangle (between|from) [x] %number%, [y] %number% (and|to) [x] %number%, [y] %number% on %image%");
    }

    @Override
    protected void execute(Event event) {
        Image image = imageExpression.getSingle(event);
        try {
            Graphics g = image.getGraphics();
            int
                    x1 = X1.getSingle(event).intValue(),
                    y1 = Y1.getSingle(event).intValue(),
                    x2 = X2.getSingle(event).intValue(),
                    y2 = Y2.getSingle(event).intValue(),
                    ox = Math.min(x1, x2),
                    oy = Math.min(x1, x2),
                    width = Math.abs(x1 - x2),
                    height = Math.abs(y1 - y2);
            if (!clear) {
                Color skcolor = skriptcolor.getSingle(event);
                g.setColor(new java.awt.Color(skcolor.asBukkitColor().asRGB()));
                switch (shape) {
                    case 1:
                        if (outline)
                            g.drawRect(ox, oy, width, height);
                        else
                            g.fillRect(ox, oy, width, height);
                        break;
                    case 2:
                        if (outline)
                            g.drawOval(ox, oy, width, height);
                        else
                            g.fillOval(ox, oy, width, height);
                        break;
                    default:
                        g.drawLine(x1, y1, x2, y2);
                }
            } else {
                g.clearRect(ox, oy, width, height);
            }
        } catch (Exception ex) {}
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "draw image shape";
    }

    private Expression<Color> skriptcolor;
    private int shape;
    private boolean outline;
    private Expression<Number> X1, Y1, X2, Y2;
    private Expression<Image> imageExpression;
    private boolean clear;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        clear = i == 1;
        if (!clear) {
            skriptcolor = (Expression<Color>) exprs[0];
            shape = parseResult.mark % 10;
            outline = parseResult.mark > 10;
        }
        X1 = (Expression<Number>) exprs[1-i];
        Y1 = (Expression<Number>) exprs[2-i];
        X2 = (Expression<Number>) exprs[3-i];
        Y2 = (Expression<Number>) exprs[4-i];
        imageExpression = (Expression<Image>) exprs[5-i];
        return true;
    }
}
