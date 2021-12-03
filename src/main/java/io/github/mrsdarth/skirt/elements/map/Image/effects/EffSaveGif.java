package io.github.mrsdarth.skirt.elements.map.Image.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.HttpUtils;
import io.github.mrsdarth.skirt.elements.map.Image.ImageUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Optional;

@Name("Gif")
@Description({"saves a gif to a variable, saving to `{_gif::*}` will result in {_gif::*} containing the images and {_gif::delay::*} containing the delays (timespan)"})
@Examples({})
@Since("2.0.0")

public class EffSaveGif extends AsyncEffect {

    static {
        Skript.registerEffect(EffSaveGif.class, "save gif from (1¦file [path]| url) %string% to %objects%");
    }

    private Expression<String> stringExpr;
    private Variable<?> variable;

    private boolean url;

    private String path;

    @Override
    protected void execute(@NotNull Event e) {
        try {
            ImageInputStream imageInputStream;
            if (url) {
                Optional<InputStream> body = HttpUtils.getRequest(path, HttpResponse.BodyHandlers.ofInputStream()).map(HttpResponse::body);
                if (body.isEmpty()) return;
                imageInputStream = new MemoryCacheImageInputStream(body.get());
            } else {
                imageInputStream = new FileImageInputStream(new File(path));
            }

            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/gif").next();
            reader.setInput(imageInputStream, true);
            Element streamMetadata = (Element) reader.getStreamMetadata().getAsTree("javax_imageio_gif_stream_1.0");
            IIOMetadataNode lsd = (IIOMetadataNode) streamMetadata.getElementsByTagName("LogicalScreenDescriptor").item(0);
            int
                    width = Integer.parseInt(lsd.getAttribute("logicalScreenWidth")),
                    height = Integer.parseInt(lsd.getAttribute("logicalScreenHeight"));
            BufferedImage current = null, last = null;
            String varName = variable.getName().toString(e);
            boolean local = variable.isLocal();
            for (int i = 0;; i++) {
                try {
                    IIOMetadataNode imageMetadata = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0");
                    IIOMetadataNode desc = (IIOMetadataNode) imageMetadata.getElementsByTagName("ImageDescriptor").item(0);
                    int
                            left = Integer.parseInt(desc.getAttribute("imageLeftPosition")),
                            top = Integer.parseInt(desc.getAttribute("imageTopPosition"));
                    IIOMetadataNode gce = (IIOMetadataNode) imageMetadata.getElementsByTagName("GraphicControlExtension").item(0);
                    int delayMillis = gce.hasAttribute("delayTime") ? Integer.parseInt(gce.getAttribute("delayTime")) * 10 : -1;
                    String disposalMethod = gce.getAttribute("disposalMethod");
                    if (disposalMethod.equals("restoreToPrevious") && last != null) current = last;
                    else if (current == null) current = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    ImageUtils.drawImage(reader.read(i), current, left, top);
                    String indexSuffix = Variable.SEPARATOR + (i + 1);
                    Variables.setVariable(varName + indexSuffix, last = current, e, local);
                    Variables.setVariable(varName + Variable.SEPARATOR + "delay" + indexSuffix, new Timespan(delayMillis), e, local);
                    if (!(disposalMethod.equals("none") || disposalMethod.equals("doNotDispose"))) current = null;
                } catch (IndexOutOfBoundsException exception) {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected @Nullable TriggerItem walk(Event e) {
        return (path = stringExpr.getSingle(e)) == null ? getNext() : super.walk(e);
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "save gif from " + (url ? "url " : "file path ") + stringExpr.toString(e, debug) + " to " + variable.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        url = parseResult.mark == 0;
        stringExpr = (Expression<String>) exprs[0];
        if (exprs[1] instanceof Variable<?> variable && variable.isList()) {
            this.variable = variable;
            return true;
        }
        Skript.error("objects needs to be a list variable");
        return false;
    }
}
