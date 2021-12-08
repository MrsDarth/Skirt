package io.github.mrsdarth.skirt.paper.elements.skins;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Skin from texture")
@Description("returns a skin from a value and optional signature, signature is required for player skins but not for skulls")
@Examples("set skin of player to skin from value \"texture here\" and signature \"signature\"")
@Since("2.0.0")
@RequiredPlugins("Paper")

public class ExprSkinFromValue extends SimpleExpression<ProfileProperty> {

    static {
        Skript.registerExpression(ExprSkinFromValue.class, ProfileProperty.class, ExpressionType.COMBINED,
                "skin (from|with) value %string% [[and] signature %-string%]");
    }

    Expression<String> stringExpr1, stringExpr2;

    @Override
    protected @Nullable
    ProfileProperty[] get(@NotNull Event e) {
        String value = stringExpr1.getSingle(e), signature = stringExpr2 == null ? null : stringExpr2.getSingle(e);
        return value == null ? null : CollectionUtils.array(new ProfileProperty("textures", value, signature));
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ProfileProperty> getReturnType() {
        return ProfileProperty.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "skin from value " + stringExpr1.toString(e, debug) + (stringExpr2 == null ? "" : " and signature " + stringExpr2.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        stringExpr1 = (Expression<String>) exprs[0];
        stringExpr2 = (Expression<String>) exprs[1];
        return true;
    }
}
