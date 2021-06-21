package io.github.mrsdarth.skirt.elements.Map.Image.expressions;


import ch.njol.skript.Skript;
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
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

@Name("Image")
@Description("get an image from a file, url, folder")
@Examples({
        "command freeop:",
        "\ttrigger:",
        "\t\tset {_image} to image from url \"https://disforge.com/assets/icons/772869055417417768.png\"",
        "\t\t{_image} is set",
        "\t\tset {_map} to new map from world",
        "\t\tedit {_map} and store canvas in {_canvas}",
        "\t\tdraw image {_image} at 0, 0 on {_canvas}",
        "\t\tset player's tool to map item from {_map}"
})
@Since("1.2.0, 1.2.1 (gif frames)")

public class ExprImage extends SimpleExpression {

    static {
        Skript.registerExpression(ExprImage.class, BufferedImage.class, ExpressionType.COMBINED,
                "[new] image from (1¦file [path]|2¦url) %string%",
                "images (from|in) folder %string%",
                "%images% resized[ to[\\(][x[ ]]%-number%,[ ][y[ ]]%-number%[\\)]]",
                "sub[ ]image of %images% from [\\(][x[ ]]%number%,[ ][y[ ]]%number%[\\)] to [\\(][x[ ]]%number%,[ ][y[ ]]%number%[\\)]",
                "new [(blank|empty)] image [with dimensions [\\(][x[ ]]%-number%,[ ][y[ ]]%-number%[\\)]]");
    }

    @Nullable
    @Override
    protected BufferedImage[] get(Event event) {
        switch (pattern) {
            case 2:
                Number x = ex != null ? ex.getSingle(event) : 128, y = ey != null ? ey.getSingle(event) : 128;
                if (x == null || y == null) return null;
                BufferedImage[]
                        bufferedImages = img.getArray(event),
                        dimg = new BufferedImage[bufferedImages.length];
                int width = x.intValue(), height = y.intValue();
                for (int i = 0; i < bufferedImages.length; i++) {
                    dimg[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = dimg[i].createGraphics();
                    g2d.drawImage(bufferedImages[i].getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);
                    g2d.dispose();
                }
                return dimg;
            case 3:
                Number
                        x0 = ex.getSingle(event),
                        y0 = ey.getSingle(event),
                        x1 = ex1.getSingle(event),
                        y1 = ey1.getSingle(event);
                if (x0 == null || y0 == null || x1 == null || y1 == null) return null;
                int
                        ix0 = x0.intValue(),
                        iy0 = y0.intValue(),
                        ix1 = x1.intValue(),
                        iy1 = y1.intValue(),
                        ox = Math.min(ix0, ix1),
                        oy = Math.min(iy0, iy1),
                        w = Math.abs(ix0 - ix1),
                        h = Math.abs(iy0 - iy1);
                BufferedImage[]
                        subs = img.getArray(event),
                        sub = new BufferedImage[subs.length];
                for (int i = 0; i < subs.length; i++) {
                    sub[i] = subs[i].getSubimage(ox, oy, w, h);
                }
                return sub;
            case 4:
                Number b = ex != null ? ex.getSingle(event) : 128, len = ey != null ? ey.getSingle(event) : 128;
                if (b == null || len == null) return null;
                return CollectionUtils.array(new BufferedImage(b.intValue(), len.intValue(), BufferedImage.TYPE_INT_RGB));
            default:
                String l = file.getSingle(event);
                if (l == null) return null;
                try {
                    return isfolder ? imagesFolder(new File(l)) : CollectionUtils.array(
                            isfile ? fromFile(new File(l)) : ImageIO.read(new URL(l)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
        }
    }

    @Override
    public boolean isSingle() {
        return (img != null) ? img.isSingle() : (!isfolder && pattern != 5);
    }

    @Override
    public Class getReturnType() {
        return BufferedImage.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "image from file/url";
    }

    private Expression<String> file;
    private Expression<BufferedImage> img;
    private Expression<Number> ex, ey, ex1, ey1;
    private boolean isfile, isfolder;
    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        pattern = i;
        switch (i) {
            case 0:
            case 1:
                file = (Expression<String>) exprs[0];
                break;
            case 4:
                ex = (Expression<Number>) exprs[0];
                ey = (Expression<Number>) exprs[1];
                break;
            case 3:
                ex1 = (Expression<Number>) exprs[3];
                ey1 = (Expression<Number>) exprs[4];
            case 2:
                img = (Expression<BufferedImage>) exprs[0];
                ex = (Expression<Number>) exprs[1];
                ey = (Expression<Number>) exprs[2];
        }
        isfile = parseResult.mark == 1;
        isfolder = i == 1;
        return true;
    }

    private static BufferedImage fromFile(File f) {
        try {
            return ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("could not get image from " + f.getPath());
            return null;
        }
    }

    public static BufferedImage[] imagesFolder(File f) {
        ArrayList<BufferedImage> imagelist = new ArrayList<>();
        File[] flist = f.listFiles();
        Arrays.sort(flist);
        for (File file : flist) {
            if (file.isDirectory()) {
                imagelist.addAll(Arrays.asList(imagesFolder(file)));
            } else {
                imagelist.add(fromFile(file));
            }
        }
        return imagelist.toArray(new BufferedImage[imagelist.size()]);
    }
}
