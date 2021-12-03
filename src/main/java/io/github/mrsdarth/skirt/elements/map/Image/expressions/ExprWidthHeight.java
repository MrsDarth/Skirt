package io.github.mrsdarth.skirt.elements.map.Image.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

@Name("Image width / height")
@Description("returns the width or height of an image")
@Examples("set {_width} to width of {_image}")
@Since("1.2.0")

public class ExprWidthHeight extends SimplePropertyExpression<BufferedImage, Long> {

    static {
        register(ExprWidthHeight.class, Long.class, "(width|1¦height)", "images");
    }

    private boolean isWidth;

    @Override
    protected @NotNull String getPropertyName() {
        return isWidth ? "width" : "height";
    }

    @Override
    public @Nullable Long convert(BufferedImage image) {
        return (long) (isWidth ? image.getWidth() : image.getHeight());
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        isWidth = matchedPattern == 0;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }
}
