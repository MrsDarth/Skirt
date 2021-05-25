package io.github.mrsdarth.skirt.elements.Map.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;


import org.bukkit.event.Event;
import org.bukkit.event.server.MapInitializeEvent;
import org.jetbrains.annotations.Nullable;

public class EvtMapInit extends SkriptEvent {

    static {
        Skript.registerEvent("map initialize", EvtMapInit.class, MapInitializeEvent.class,
                "map init[iali(z|s)e]");
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
        return "map initialize";
    }
}
