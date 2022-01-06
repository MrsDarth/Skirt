package io.github.mrsdarth.skirt.paper.elements.skins;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.mrsdarth.skirt.HttpUtils;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.net.URL;
import java.util.Base64;

@Name("Image from skin")
@Description("retrieves an image from a skin and saves to a variable")
@Examples("load image from skin (player's skin) to {_image}")
@Since("2.0.0")
@RequiredPlugins("Paper")

public class EffImageFromSkin extends Effect {

    static {
        Skript.registerEffect(EffImageFromSkin.class,"load image from skin %skin% [in]to %object%");
    }

    private Expression<ProfileProperty> skinExpr;
    private Variable<?> variable;

    @Override
    protected void execute(@NotNull Event e) {
            ProfileProperty skin = skinExpr.getSingle(e);
            if (skin == null) return;
        try {
            Skirtness.setVariable(variable, e, ImageIO.read(new URL(HttpUtils.parseJson(Base64.getDecoder().decode(skin.getValue())).getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString())));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "load image from skin " + skinExpr.toString(e, debug) + " to " + variable.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        if (exprs[1] instanceof Variable<?> var) {
            variable = var;
            skinExpr = (Expression<ProfileProperty>) exprs[0];
            return true;
        }
        Skript.error("object needs to be a variable");
        return false;
    }
}
