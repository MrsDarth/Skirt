package io.github.mrsdarth.skirt.elements.Other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.Checker;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EvtStatisticChange extends SkriptEvent {

    static {
        Skript.registerEvent("Stats change", EvtStatisticChange.class, PlayerStatisticIncrementEvent.class,
                "[player] stat[istic][s] (change|increase)[ of %statistics%]")
                .description("Called when a player statistic changes value")
                .examples(
                        "# jump event without paper",
                        "on player stats change of jump:",
                        "\tsend \"you jumped!\" to player")
                .since("1.2.0");
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, Number.class, new Getter<Number, PlayerStatisticIncrementEvent>() {
            @Nullable
            @Override
            public Number get(PlayerStatisticIncrementEvent e) {
                return e.getPreviousValue();
            }
        }, -1);
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, Number.class, new Getter<Number, PlayerStatisticIncrementEvent>() {
            @Nullable
            @Override
            public Number get(PlayerStatisticIncrementEvent e) {
                return e.getNewValue();
            }
        }, 1);
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, ItemStack.class, new Getter<ItemStack, PlayerStatisticIncrementEvent>() {
            @Nullable
            @Override
            public ItemStack get(PlayerStatisticIncrementEvent e) {
                Material m = e.getMaterial();
                return m != null ? new ItemStack(m) : null;
            }
        }, 0);
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, EntityData.class, new Getter<EntityData, PlayerStatisticIncrementEvent>() {
            @Nullable
            @Override
            public EntityData get(PlayerStatisticIncrementEvent e) {
                EntityType type = e.getEntityType();
                return type != null && type.getEntityClass() != null ? EntityData.fromClass(type.getEntityClass()) : null;
            }
        }, 0);
    }

    private Literal<Statistic> stat;

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        stat = (Literal<Statistic>) literals[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        Statistic stats = ((PlayerStatisticIncrementEvent) event).getStatistic();
        return stat == null || stat.check(event, new Checker<Statistic>() {
            @Override
            public boolean check(Statistic statistic) {
                return statistic == stats;
            }
        });
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "player stats change";
    }
}
