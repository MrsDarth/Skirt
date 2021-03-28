package io.github.mrsdarth.skirt.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffUpdateInventory extends Effect {

    static {
        Skript.registerEffect(EffUpdateInventory.class, "update inventor(y|ies) (of|for) %players%", "update %players%'[s] inventor(y|ies)");
    }

    private Expression<Player> players;

    @Override
    protected void execute(Event event) {
        for (Player p: players.getArray(event)) {
            p.updateInventory();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "update inventory";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        return true;
    }
}
