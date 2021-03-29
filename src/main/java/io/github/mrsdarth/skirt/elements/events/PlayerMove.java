package io.github.mrsdarth.skirt.elements.events;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;

@Name("Player Move Event")
@Description("Called when any player moves")
@Examples({"on player move:",
        "\tsend \"%player% moved from %getFrom% to %getTo%\" to {staff::*}"})
@Since("1.0.0")

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