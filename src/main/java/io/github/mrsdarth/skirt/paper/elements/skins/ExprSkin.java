package io.github.mrsdarth.skirt.paper.elements.skins;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.mrsdarth.skirt.Skirtness;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ExprSkin extends SimplePropertyExpression<Object, ProfileProperty> {

    static {
        register(ExprSkin.class, ProfileProperty.class, "skin [texture]", "players/blocks/itemtypes");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "skin";
    }

    @Override
    public @Nullable ProfileProperty convert(Object o) {
        if (o instanceof Player player) return Skins.getSkin(player);
        if (o instanceof Skull skull) return Skins.getSkin(skull.getPlayerProfile());
        if (o instanceof ItemType itemType && itemType.getItemMeta() instanceof SkullMeta skullMeta) return Skins.getSkin(skullMeta.getPlayerProfile());
        return null;
    }

    @Override
    public @NotNull Class<? extends ProfileProperty> getReturnType() {
        return ProfileProperty.class;
    }

    @Override
    public @Nullable
    Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(OfflinePlayer.class, ProfileProperty.class) : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {

        if (ArrayUtils.isEmpty(delta)) return;

        Consumer<ProfileProperty> setSkin = skin -> {
            PlayerProfile profile = Bukkit.createProfile("");
            profile.setProperty(skin);
            for (Object o : getExpr().getArray(e)) {
                if (o instanceof Player player) Skins.setSkin(player, skin);
                else if (o instanceof ItemType itemType) {
                    if (itemType.getItemMeta() instanceof SkullMeta skullMeta) {
                        skullMeta.setPlayerProfile(profile);
                        itemType.setItemMeta(skullMeta);
                    }
                } else if (o instanceof Skull skull) skull.setPlayerProfile(profile);
            }
        };

        if (delta[0] instanceof OfflinePlayer player)
            CompletableFuture.supplyAsync(() -> Skins.getSkin(player)).thenAcceptAsync(setSkin, Skirtness.getMainThreadExecutor());
        else if (delta[0] instanceof ProfileProperty profileProperty)
            setSkin.accept(profileProperty);
    }
}
