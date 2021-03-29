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

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Player interact event")
@Description({"Called when a player interacts with a block or item",
        "event-string returns the action involved:",
        "LEFT_CLICK_AIR: Left-clicking the air",
        "LEFT_CLICK_BLOCK: Left-clicking a block",
        "RIGHT_CLICK_AIR: Right-clicking the air",
        "RIGHT_CLICK_BLOCKRight-clicking a block",
        "PHYSICAL: Stepping onto or into a block: Jumping on soil, Standing on pressure plate, Triggering redstone ore, Triggering tripwire"})

@Examples({"on player interact:",
        "\tif event-block is a chest:",
        "\t\tif event-string is \"RIGHT_CLICK_BLOCK\":",
        "\t\t\tbroadcast \"%player% interacted with a chest\""})
@Since("1.0.0")


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