package io.github.mrsdarth.skirt.elements.events;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

import org.jetbrains.annotations.Nullable;


public class PlayerMove extends SkriptEvent {

    static {
        Skript.registerEvent("Player Move", PlayerMove.class, PlayerMoveEvent.class, "Play[er][ ]Move[ment]");
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
        return "Player Move";
    }

}