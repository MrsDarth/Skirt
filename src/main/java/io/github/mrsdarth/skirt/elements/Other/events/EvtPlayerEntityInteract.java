package io.github.mrsdarth.skirt.elements.Other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.Checker;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class EvtPlayerEntityInteract extends SkriptEvent {

    static {
        Skript.registerEvent("Player Entity Interact", EvtPlayerEntityInteract.class, PlayerInteractAtEntityEvent.class,
                "[player] entity interact[ at %entitydatas%]")

                .description("Called when player right clicks on a entity. event-vector returns the point where the entity was clicked at")
                .examples("on player entity interact at zombie:")
                .since("1.2.0");

        EventValues.registerEventValue(PlayerInteractAtEntityEvent.class, Vector.class,
                new Getter<Vector, PlayerInteractAtEntityEvent>() {
            @Nullable
            @Override
            public Vector get(PlayerInteractAtEntityEvent e) {
                return e.getClickedPosition();
            }
        }, 0);
    }

    private Literal<EntityData<?>> type;

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        type = (Literal<EntityData<?>>) literals[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        Entity e = ((PlayerInteractAtEntityEvent) event).getRightClicked();
        return type == null || type.check(event, new Checker<EntityData<?>>() {
            @Override
            public boolean check(EntityData<?> entityData) {
                return entityData.isInstance(e);
            }
        });
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "player entity interact";
    }
}
