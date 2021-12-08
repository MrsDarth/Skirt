package io.github.mrsdarth.skirt.elements.map.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Text width")
@Description("returns the width of a text in pixels. returns nothing if there is invalid character")
@Examples("set {_w} to text width of message")
@Since("1.2.2")

public class ExprTextWidth extends SimplePropertyExpression<String, Long> {

    static {
        register(ExprTextWidth.class, Long.class, "(chat|text) width", "strings");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "chat width";
    }

    @Nullable
    @Override
    public Long convert(String s) {
        try {
            return (long) MinecraftFont.Font.getWidth(s);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

}