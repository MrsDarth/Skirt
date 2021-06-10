package io.github.mrsdarth.skirt.elements.Other.events;

import ch.njol.skript.aliases.ItemType;
import ch.njol.util.Checker;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import ch.njol.skript.util.Getter;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

import org.jetbrains.annotations.Nullable;


public class EvtPlayerBlockInteract extends SkriptEvent {

    static {
        Skript.registerEvent("Player Block Interact", EvtPlayerBlockInteract.class, PlayerInteractEvent.class,
                "[player] block interact[ (with|on) %itemtypes%]")

                .description("Called when a player interacts with a block or item",
                        "event-string returns the action involved:",
                        "LEFT_CLICK_AIR: Left-clicking the air",
                        "LEFT_CLICK_BLOCK: Left-clicking a block",
                        "RIGHT_CLICK_AIR: Right-clicking the air",
                        "RIGHT_CLICK_BLOCKRight-clicking a block",
                        "PHYSICAL: Stepping onto or into a block: Jumping on soil, Standing on pressure plate, Triggering redstone ore, Triggering tripwire")
                .examples("on player interact:",
                        "\tif event-block is a chest:",
                        "\t\tif event-string is \"RIGHT_CLICK_BLOCK\":",
                        "\t\t\tbroadcast \"%player% interacted with a chest\"")
                .since("1.0.0");

        EventValues.registerEventValue(PlayerInteractEvent.class, String.class, new Getter<String, PlayerInteractEvent>() {
            @Override
            @Nullable
            public String get(PlayerInteractEvent e) {
                return e.getAction().toString();
            }
        }, 0);


    }

    private Literal<ItemType> type;

    @Override
    public boolean check(final Event e) {
        Block b = ((PlayerInteractEvent) e).getClickedBlock();
        return type == null || type.check(e, new Checker<ItemType>() {
            @Override
            public boolean check(ItemType itemData) {
                return itemData.isOfType(b);
            }
        });
    }

    @Override
    public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parser) {
        type = (Literal<ItemType>) args[0];
        return true;
    }

    @Override
    public String toString(final @Nullable Event e, final boolean debug) {
        return "player block interact";
    }


}