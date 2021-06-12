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
        Skript.registerEvent("Map initialize", EvtMapInit.class, MapInitializeEvent.class,
                "map init[iali(z|s)e]")

                .description("event gets called when a map is created for the first time and starts to render, eg. map from world expression or player getting a filled map for the first time")
                .examples("on map initialize:")
                .since("1.2.0");
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
