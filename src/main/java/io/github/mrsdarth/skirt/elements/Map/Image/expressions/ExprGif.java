package io.github.mrsdarth.skirt.elements.Map.Image.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.Main;
import io.github.mrsdarth.skirt.elements.Util.GifDecoder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

@Name("Gif")
@Description("returns the frames of a gif (list of images), or delay of the gif frames (timespan)")
@Examples({
        "set {_link} to \"https://pa1.narvii.com/6700/47d6eaa26e78150f7284f9ff44b24ec789e33ae3_128.gif\"",
        "set {_gif::*} to frames of gif from url {_link}",
        "set {_delay} to frame delay of gif from url {_link}",
        "{_gif::*} and {_delay} is set",
        "loop {_gif::*}:",
        "\tset {_map} to new map from world",
        "\tedit {_map} in {_canvas}",
        "\tdraw image loop-value at 0, 0 on {_canvas}",
        "\tset {_item::%loop-index%} to map item from {_map}",
        "\twait 1 tick",
        "spawn item frame",
        "set {_e} to spawned entity",
        "while {_e} is alive:",
        "\tloop {_item::*}:",
        "\t\tset item of {_e} to loop-value",
        "\t\twait {_delay}"
})
@Since("1.2.1")

public class ExprGif extends PropertyExpression<String, Object> {

    static {
        Skript.registerExpression(ExprGif.class, Object.class, ExpressionType.PROPERTY,
                "[all] frames of gif from (1¦file [path]|2¦url) %string%",
                "frame delay of gif from (1¦file [path]|2¦url) %string%");
    }

    private boolean isfile;
    private boolean isframes;

    @Override
    protected Object[] get(Event event, String[] strings) {
        GifDecoder g = gif(strings[0], isfile);
        if (g == null) return null;
        int size = g.getFrameCount();
        if (size <= 0) return null;
        if (isframes) {
            BufferedImage[] images = new BufferedImage[size];
            for (int i = 0; i < size; i++) {
                images[i] = g.getFrame(i);
            }
            return images;
        } else
            return CollectionUtils.array(new Timespan(g.getDelay(0)));
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return isframes ? BufferedImage.class : Timespan.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "gif frames";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<String>) expressions[0]);
        isfile = parseResult.mark == 1;
        isframes = i == 0;
        return true;
    }

    private GifDecoder gif(String link, boolean isfile) {
        try {
            GifDecoder gif = new GifDecoder();
            gif.read(new FileInputStream(isfile ? new File(link) : cacheImage(new URL(link))));
            return gif;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static File cacheImage(URL url) {
        File file = new File(Main.cache(), url.toString().replaceAll("\\W", ""));
        if (file.exists()) return file;
        if (file.mkdirs()) {
            try {
                file.delete();
                Files.copy(url.openConnection().getInputStream(), file.toPath());
            } catch (Exception ex) {}
        }
        return file.exists() ? file : null;
    }
}
