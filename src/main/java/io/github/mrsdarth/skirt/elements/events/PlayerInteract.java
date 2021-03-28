package io.github.mrsdarth.skirt.elements.events;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import ch.njol.skript.util.Getter;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

import org.jetbrains.annotations.Nullable;


public class PlayerInteract extends SkriptEvent {

    static {
        Skript.registerEvent("Player Interact", PlayerInteract.class, PlayerInteractEvent.class, "Player[ block][ ]Interact");

        EventValues.registerEventValue(PlayerInteractEvent.class, String.class, new Getter<String, PlayerInteractEvent>() {
            @Override
            @Nullable
            public String get(PlayerInteractEvent e) {
                return e.getAction().toString();
            }
        }, 0);


    }


    @Override
    public boolean check(final Event e) {
        return true;
    }

    @Override
    public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parser) {
        return true;
    }

    @Override
    public String toString(final @Nullable Event e, final boolean debug) {
        return "Player interact";
    }


}