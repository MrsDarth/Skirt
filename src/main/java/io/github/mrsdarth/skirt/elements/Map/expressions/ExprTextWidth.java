package io.github.mrsdarth.skirt.elements.Map.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.Nullable;

@Name("Text width")
@Description("returns the width of a text in pixels")
@Examples("set {_w} to text width of message")
@Since("1.2.2")

public class ExprTextWidth extends SimplePropertyExpression<String, Number> {

    static {
        register(ExprTextWidth.class, Number.class, "(chat|text) width", "strings");
    }

    @Override
    protected String getPropertyName() {
        return "chat width";
    }

    @Nullable
    @Override
    public Number convert(String s) {
        return MinecraftFont.Font.getWidth(s);
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }
}
