package io.github.mrsdarth.skirt.elements.map.Image.effects;
/*
import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.ExprSpawnerType;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirt;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import io.github.mrsdarth.skirt.elements.Map.effects.EffMapDraw;
import io.github.mrsdarth.skirt.elements.Map.expressions.ExprItemMap;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Name("Split Images")
@Description({
        "Maps an image split to 128x128 (map canvas size) to a list variable in the form {list::x::y::*}",
        "images should be the same size",
        "auto mapping will automatically draw the image on an unedited map and store the map item with the image drawn",
        "this is a delayed effect due to its chungusness",
        "example:",
        "{image::*} is set to 200 x 100 images",
        "it will be rounded up to 256 x 128 and split into 2 x 1 images",
        "the image quality will not be affected. Excess pixels will just be transparent",
        "`split {image::*} onto {split::*}` will result in:",
        "{split::x} = 2",
        "{split::y} = 1",
        "because it returns 2 x 1",
        "{split::1::1::*} = 128x128 image at (1, 1) which is the upper left corner",
        "{split::2::1::*} = 128x128 image at (2, 1)"
})
@Examples({
        "set {_gif::*} to frames of gif from url \"https://media.tenor.com/images/704d8ce6b217648ccb286b8ebc33268d/tenor.gif\"",
        "split {_gif::*} onto {_images::*}"
})
@Since("1.2.4")

public class EffSplitImage extends Effect {

    static {
        Skript.registerEffect(EffSplitImage.class, "split %images% onto %objects% [(1¦with auto mapping)]");
    }

    private Variable<?> variable;
    private Expression<BufferedImage> images;
    private boolean autoMap;

    private final ArrayList<MapView> mapViews = new ArrayList<>();

    public static BufferedImage drawImage(BufferedImage image, BufferedImage on) {
        return drawImage(image, on, 0, 0);
    }

    public static BufferedImage drawImage(BufferedImage image, BufferedImage on, int x, int y) {
        Graphics2D g = on.createGraphics();
        g.drawImage(image, x, y, null);
        g.dispose();
        return on;
    }

    private Object nextObject(BufferedImage image) {
        return autoMap ? nextItem(image) : image;
    }

    private ItemType nextItem(BufferedImage image) {
        MapView next = nextMap();
        new Renderer(next, Renderer.Mode.EDIT, canvas -> EffMapDraw.drawImage(image, canvas, 0, 0, 128, 128));
        mapViews.add(next);
        return ExprItemMap.filled_map(next);
    }

    private MapView nextMap() {
        MapView map = Renderer.firstUnedited();
        return map != null ? map : Bukkit.createMap(Skirt.getMainWorld());
    }

    private static String getNameOfListVar(Variable<?> var, Event e) {
        String name = var.getName().toString(e);
        return name.substring(0, name.length()-3);
    }


    @Override
    protected void execute(Event event) {}

    @Nullable
    @Override
    protected TriggerItem walk(Event event) {
        TriggerItem next = getNext();
        String var = getNameOfListVar(variable, event);
        boolean local = variable.isLocal();
        BufferedImage[] images = this.images.getArray(event);
        int size = images.length;
        if (size == 0 || images[0].getWidth() == 0 || images[0].getHeight() == 0) return next;
        Object localVars = Variables.removeLocals(event);
        HashMap<String, Object> map = new HashMap<>();
        Skirt.runAsync(() -> {
            String ll = Variable.SEPARATOR;
            int
                    grid = 128,
                    width = images[0].getWidth(),
                    height = images[0].getHeight(),
                    w = (int) Math.ceil(width / (double)grid),
                    h = (int) Math.ceil(height / (double)grid),
                    lengthX = w * grid,
                    lengthY = h * grid,
                    OffsetX = (lengthX - width)/2,
                    OffsetY = (lengthY - height)/2;
            map.put(var + ll + "x", w);
            map.put(var + ll + "y", h);
            for (int i = 0; i < size; i++) {
                BufferedImage resized = drawImage(images[i], new BufferedImage(lengthX, lengthY, images[i].getType()), OffsetX, OffsetY);
                for (int x = 0; x < w; x++) {
                    int sx = x * grid;
                    for (int y = 0; y < h; y++)
                        map.put(var + ll + (x + 1) + ll + (y + 1) + ll + (i + 1), nextObject(resized.getSubimage(sx, (y * grid), grid, grid)));
                }
            }
            Skirt.runLater(() -> {
                if (autoMap) Renderer.AttemptRender(mapViews, Bukkit.getOnlinePlayers());
                Skirt.runLater(() -> {
                    if (localVars != null) Variables.setLocalVariables(event, localVars);
                    for (Map.Entry<String, Object> variable: map.entrySet())
                        Variables.setVariable(variable.getKey(), variable.getValue(), event, local);
                    if (next != null) TriggerItem.walk(next, event);
                }, 1);
            }, 1);
        });
        return null;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "split image onto %variables%";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (exprs[1] instanceof Variable) {
            variable = (Variable<?>) exprs[1];
            if (variable.isList()) {
                images = (Expression<BufferedImage>) exprs[0];
                autoMap = parseResult.mark == 1;
                return true;
            }
        }
        return false;
    }


}
*/