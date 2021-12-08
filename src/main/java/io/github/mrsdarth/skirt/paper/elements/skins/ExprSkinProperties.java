package io.github.mrsdarth.skirt.paper.elements.skins;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Converters;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("texture of skin")
@Description("get or set the value and signature of a skin")
@Examples("set value of skin of player's tool to \"texture\"")
@Since("2.0.0")
@RequiredPlugins("Paper")

public class ExprSkinProperties extends SimplePropertyExpression<ProfileProperty, String> {

    static {
        register(ExprSkinProperties.class, String.class, "([texture] value|1¦signature)", "skins");
    }

    private boolean isValue;

    @Override
    protected @NotNull String getPropertyName() {
        return isValue ? "value" : "signature";
    }

    @Override
    public String convert(ProfileProperty skin) {
        return isValue ? skin.getValue() : skin.getSignature();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        isValue = parseResult.mark == 0;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable
    Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return mode == Changer.ChangeMode.SET && Changer.ChangerUtils.acceptsChange(getExpr(), mode, getReturnType()) ? CollectionUtils.array(String.class) : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        if (ArrayUtils.isEmpty(delta) || !(delta[0] instanceof String change)) return;
        getExpr().change(e, Converters.convert(getExpr().getArray(e), ProfileProperty.class, isValue ?
                skin -> new ProfileProperty(skin.getName(), change, skin.getSignature()) :
                skin -> new ProfileProperty(skin.getName(), skin.getValue(), change)), mode);
    }
}
