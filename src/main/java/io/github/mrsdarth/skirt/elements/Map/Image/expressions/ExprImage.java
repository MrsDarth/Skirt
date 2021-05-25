package io.github.mrsdarth.skirt.elements.Map.Image.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ExprImage extends SimpleExpression {

    static {
        Skript.registerExpression(ExprImage.class, Image.class, ExpressionType.COMBINED,
                "[new] image[s] from (1¦file [path]|2¦url) %string%",
                "images (from|in) folder %string%",
                "new [(blank|empty)] image");
    }

    @Nullable
    @Override
    protected Image[] get(Event event) {
        String l = f.getSingle(event);
        if (l == null) return null;
        if (isblank) return new Image[] {new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB)};
        try {
            return isfolder ? imagesFolder(new File(l)) : new Image[]{isfile ? fromFile(new File(l)) : ImageIO.read(new URL(l))};
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isSingle() {
        return isfolder;
    }

    @Override
    public Class getReturnType() {
        return Image.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "image from file/url";
    }

    private Expression<String> f;
    private boolean isfile, isfolder, isblank;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        f = (Expression<String>) exprs[0];
        isfile = parseResult.mark == 1;
        isfolder = i == 1;
        isblank = i == 2;
        return true;
    }

    private Image fromFile(File f) {
        try {
            return ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("could not get image from " + f.getPath());
            return null;
        }
    }

    private Image[] imagesFolder(File f) {
        ArrayList<Image> imagelist = new ArrayList<Image>();
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {
                imagelist.addAll(Arrays.asList(imagesFolder(file)));
            } else {
                imagelist.add(fromFile(file));
            }
        }
        return imagelist.toArray(new Image[imagelist.size()]);
    }
}
