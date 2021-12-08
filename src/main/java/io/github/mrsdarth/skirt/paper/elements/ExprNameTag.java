package io.github.mrsdarth.skirt.paper.elements;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Name("NameTag")
@Description("get or set the name tag of a player limit is 16 characters")
@Examples({"command /disguise <player>:",
        "\ttrigger:",
        "\t\tset skin of player to arg",
        "\t\tset name tag of player to name of arg"})
@Since("2.0.0")
@RequiredPlugins("Paper")

public class ExprNameTag extends SimplePropertyExpression<Player, String> {

    static {
        register(ExprNameTag.class, String.class, "name[ ]tag", "players");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "name tag";
    }

    @Override
    public @Nullable String convert(Player player) {
        return player.getPlayerProfile().getName();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @Nullable
    Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET ? CollectionUtils.array(String.class) : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        Consumer<PlayerProfile> editProfile;
        if (mode == Changer.ChangeMode.SET) {
            if (ArrayUtils.isEmpty(delta) || !(delta[0] instanceof String name))
                return;
            editProfile = playerProfile -> playerProfile.setName(name.substring(0, 16));
        } else
            editProfile = playerProfile -> {
                playerProfile.setName(null);
                playerProfile.complete();
            };
        for (Player player: getExpr().getArray(e)) {
            PlayerProfile profile = player.getPlayerProfile();
            editProfile.accept(profile);
            player.setPlayerProfile(profile);
        }
    }
}
