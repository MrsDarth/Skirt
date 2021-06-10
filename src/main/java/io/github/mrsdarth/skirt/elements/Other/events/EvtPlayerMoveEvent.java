package io.github.mrsdarth.skirt.elements.Other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.Nullable;

public class EvtPlayerMoveEvent extends SkriptEvent {

    static {
        if (!Skript.classExists("ch.njol.skript.events.EvtMove"))
            Skript.registerEvent("Player Move Event", EvtPlayerMoveEvent.class, PlayerMoveEvent.class, "player move");
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "on player move:";
    }
}
