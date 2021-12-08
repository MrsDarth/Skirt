package io.github.mrsdarth.skirt.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Name("Update inventory")
@Description("updates a player's inventory. Useful to get rid of ghost items")
@Examples("update inventory of player")
@Since("1.0.0")

public class EffUpdateInventory extends Effect {

    static {
        Skript.registerEffect(EffUpdateInventory.class, "update inventor(y|ies) (of|for) %players%", "update %players%'[s] inventor(y|ies)");
    }

    private Expression<Player> playerExpr;

    @Override
    protected void execute(@NotNull Event e) {
        for (Player player: playerExpr.getArray(e))
            player.updateInventory();
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "update inventory of " + playerExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        playerExpr = (Expression<Player>) exprs[0];
        return true;
    }
}
