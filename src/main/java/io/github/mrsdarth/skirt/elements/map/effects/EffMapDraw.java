package io.github.mrsdarth.skirt.elements.map.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.elements.map.Maps;
import io.github.mrsdarth.skirt.skriptv2_6.sections.SecMapEdit;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

@Name("Map Draw")
@Description({
        "draw text or image on a map.",
        "for draw text, map colors are in the format §<number>; however you can still use hex and regular color codes and it will convert for you",
        "for draw image, only part of the image will be displayed if it goes over the 128 pixel size. You can use the image expression to resize"
})
@Examples("draw text \"<##123456>hello\" at (0,0) on {canvas}")
@Since("1.2.0")

public class EffMapDraw extends Effect {

    static {
        Skript.registerEffect(EffMapDraw.class,
                "draw image %image% [at " + Maps.coordPattern(true) + "] [on %-mapcanv%]",
                "draw text %string% at " + Maps.coordPattern(false) + " [on %-mapcanv%]");
    }

    private SecMapEdit mapEditSection;

    private Expression<BufferedImage> imageExpr;
    private Expression<Number> numberExpr1, numberExpr2;
    private Expression<MapCanvas> canvasExpr;
    private Expression<String> stringExpr;

    private boolean drawImage;

    @Override
    protected void execute(@NotNull Event e) {
        MapCanvas canvas = canvasExpr == null ? mapEditSection.getCanvas() : canvasExpr.getSingle(e);
        Number x = numberExpr1 == null ? 0 : numberExpr1.getSingle(e), y = numberExpr2 == null ? 0 : numberExpr2.getSingle(e);
        if (canvas == null || x == null || y == null) return;
        if (drawImage) {
            BufferedImage image = imageExpr.getSingle(e);
            if (image != null)
                Maps.imageToMapPixels(image, x.intValue(), y.intValue(), canvas::setPixel);
        } else {
            String text = stringExpr.getSingle(e);
            if (text != null)
                canvas.drawText(x.intValue(), y.intValue(), MinecraftFont.Font, Maps.toMapFormat(text));
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "draw " + (drawImage ? "image " + imageExpr.toString(e, debug) : "text " + stringExpr.toString(e, debug)) + " at (" + numberExpr1.toString(e, debug) + ", " + numberExpr2.toString(e, debug) + ") on " + canvasExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        if (!Skirtness.isSkriptv2_6())
            mapEditSection = getParser().getCurrentSection(SecMapEdit.class);
        canvasExpr = (Expression<MapCanvas>) exprs[3];
        if (canvasExpr == null && mapEditSection == null) {
            Skript.error("You need to specify a map canvas when outside a map edit section");
            return false;
        }
        if (drawImage = (matchedPattern == 0))
            imageExpr = (Expression<BufferedImage>) exprs[0];
        else
            stringExpr = (Expression<String>) exprs[0];
        numberExpr1 = (Expression<Number>) exprs[1];
        numberExpr2 = (Expression<Number>) exprs[2];
        return true;
    }
}
