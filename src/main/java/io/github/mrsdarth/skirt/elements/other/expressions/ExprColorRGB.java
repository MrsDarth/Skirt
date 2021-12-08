package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("RGB of Color")
@Description("get or change the red, green, blue component or the entire rgb integer of a color")
@Examples("add 1 to red of {_color}")
@Since("2.0.0")

public class ExprColorRGB extends SimplePropertyExpression<Color, Long> {

    static {
        register(ExprColorRGB.class, Long.class, "(red|1¦green|2¦blue|3¦rgb)", "colors");
    }

    private int pattern;

    @Override
    protected @NotNull String getPropertyName() {
        return switch (pattern) {
            case 0 -> "red";
            case 1 -> "green";
            case 2 -> "blue";
            case 3 -> "rgb";
            default -> throw new IllegalStateException();
        };
    }

    @Override
    public Long convert(Color color) {
        org.bukkit.Color bukkitColor = color.asBukkitColor();
        return (long) switch (pattern) {
            case 0 -> bukkitColor.getRed();
            case 1 -> bukkitColor.getGreen();
            case 2 -> bukkitColor.getBlue();
            case 3 -> bukkitColor.asRGB();
            default -> throw new IllegalStateException();
        };
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        pattern = matchedPattern;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable
    Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return Changer.ChangerUtils.acceptsChange(getExpr(), Changer.ChangeMode.SET, Color.class) ? switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Number.class);
            default -> null;
        } : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        if (ArrayUtils.isEmpty(delta) || !(delta[0] instanceof Number number)) return;
        int change = number.intValue();
        getExpr().change(e, Converters.convert(getExpr().getArray(e), Color.class, color -> {
            org.bukkit.Color bukkitColor = color.asBukkitColor();
            int newValue = switch (mode) {
                case SET -> change;
                case ADD -> convert(color).intValue() + change;
                case REMOVE -> convert(color).intValue() - change;
                default -> throw new IllegalStateException();
            };
            int fit = newValue & 0xFF;
            return switch (pattern) {
                case 0 -> new ColorRGB(fit, bukkitColor.getGreen(), bukkitColor.getBlue());
                case 1 -> new ColorRGB(bukkitColor.getRed(), fit, bukkitColor.getBlue());
                case 2 -> new ColorRGB(bukkitColor.getRed(), bukkitColor.getGreen(), fit);
                case 3 -> {
                   org.bukkit.Color newColor = org.bukkit.Color.fromRGB(newValue & 0xFFFFFF);
                   yield new ColorRGB(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
                }
                default -> throw new IllegalStateException();
            };
        }), Changer.ChangeMode.SET);
    }
}
