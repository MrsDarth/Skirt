package io.github.mrsdarth.skirt.elements.Map.Image.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

@Name("Image width / height")
@Description("returns the width or height of an image")
@Examples("set {_width} to width of {_image}")
@Since("1.2.0")

public class ExprWidthHeight extends SimplePropertyExpression<BufferedImage, Number> {

    static {
        register(ExprWidthHeight.class, Number.class, "(1¦width|2¦height)", "images");
    }

    private boolean isWidth;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        super.init(exprs, matchedPattern, isDelayed, parseResult);
        isWidth = parseResult.mark == 1;
        return true;
    }

    @Override
    protected String getPropertyName() {
        return isWidth ? "width" : "height";
    }

    @Nullable
    @Override
    public Number convert(BufferedImage bufferedImage) {
        return isWidth ? bufferedImage.getWidth() : bufferedImage.getHeight();
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
