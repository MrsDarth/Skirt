package io.github.mrsdarth.skirt.elements.OtherOther.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@Name("Something Weird")
@Description("does something weird")
@Examples("do something weird to player")
@Since("1.1.0")

public class EffWeird extends Effect {

    static {
        Skript.registerEffect(EffWeird.class,
                "do something weird to %players%");
    }

    private Expression<Player> players;

    @Override
    protected void execute(Event event) {
        for (Player p: players.getArray(event)) {
            Reflectness.hide(Stream.of(p), new Player[]{p});
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "weird";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        return true;
    }
}
