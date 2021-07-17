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
import io.github.mrsdarth.skirt.elements.Map.sections.SecMapEdit;
import io.github.mrsdarth.skirt.elements.Util.EffectSection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Name("Map Draw")
@Description({
        "draw text or images on a map.",
        "for draw text, map colors are in the format §<number>; however you can still use hex and regular color codes and it will convert for you",
        "for draw image, only part of the image will be displayed if it goes over the 128 pixel size. You can use the image expression to resize"
})
@Examples("draw text \"<##123456>hello\" at (0,0) on {canvas}")
@Since("1.2.0")

public class EffMapDraw extends Effect {

    static {
        Skript.registerEffect(EffMapDraw.class,
                "draw image %image% at [\\(][x[ ]]%number%,[ ][y[ ]]%number%[\\)] [on %-mapcanv%]",
                "draw text %string% at [\\(][x[ ]]%number%,[ ][y[ ]]%number%[\\)] [on %-mapcanv%]");
    }

    private Expression<Number> x, y;
    private Expression<BufferedImage> img;
    private Expression<String> txt;
    private Expression<MapCanvas> canvas;
    private int pattern;

    @Override
    protected void execute(Event event) {
        MapCanvas c = SecMapEdit.getCanvas(event, canvas);
        if (c == null) return;
        Number nx = x.getSingle(event), ny = y.getSingle(event);
        if (nx == null || ny == null) return;
        int
                cx = nx.intValue(),
                cy = ny.intValue();
        if (pattern == 0) {
            Image image = img.getSingle(event);
            if (image != null) c.drawImage(cx, cy, image);
        } else {
            String text = txt.getSingle(event);
            if (text != null)
            c.drawText(cx, cy, MinecraftFont.Font, mapformat(mapformathex(text)));
        }

    }


    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "change map";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!EffectSection.isCurrentSection(SecMapEdit.class) && (exprs[3] == null)) {
            Skript.error("You can only change maps without specifying canvas within a map edit section");
            return false;
        }
        x = (Expression<Number>) exprs[1];
        y = (Expression<Number>) exprs[2];
        canvas = (Expression<MapCanvas>) exprs[3];
        if (i == 0) img = (Expression<BufferedImage>) exprs[0];
        else txt = (Expression<String>) exprs[0];
        pattern = i;
        return true;
    }


    @SuppressWarnings("deprecation")
    private String mapformat(String text) {
        return sidesplit(text, "§(\\d(?!\\d{0,2};)|[a-fA-F])", s -> ("§" + MapPalette.matchColor(ChatColor.getByChar(s.replace("§", "").charAt(0)).getColor()) + ";"));
    }

    @SuppressWarnings("deprecation")
    private String mapformathex(String text) {
        return sidesplit(text, "§x(§[\\da-fA-F]){6}", s -> ("§" + MapPalette.matchColor(ChatColor.of(s.replace("§", "").replace('x', '#')).getColor()) + ";"));
    }

    private String sidesplit(String text, String p, Function<String, String> mapper) {
        String[] split = (" " + text).split("((?<=" + p + ")|(?= " + p + "))");
        split[0] = null;
        int size = split.length;
        for (int i = 1; i < size; i++)
            if (i % 2 != 0) split[i] = mapper.apply(split[i]);
        return String.join("", split).substring(1);
    }

}
