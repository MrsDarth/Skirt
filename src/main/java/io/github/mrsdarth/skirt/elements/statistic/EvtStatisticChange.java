package io.github.mrsdarth.skirt.elements.statistic;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtStatisticChange extends SkriptEvent {

    static {
        Skript.registerEvent("Stats change", EvtStatisticChange.class, PlayerStatisticIncrementEvent.class,
                "[player] stat[istic][s] (change|increase)[ of %statistics%]")
                .description("Called when a player statistic changes value. event-number returns the change amount while past and future event-number returns the value before and after the event")
                .examples("# jump event without paper",
                        "on player stats change of jump:",
                        "\tsend \"you jumped!\" to player")
                .since("1.2.0");
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, Number.class, new Getter<>() {
            @Override
            public Number get(PlayerStatisticIncrementEvent e) {
                return e.getPreviousValue();
            }
        }, -1);
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, Number.class, new Getter<>() {
            @Override
            public Number get(PlayerStatisticIncrementEvent e) {
                return e.getNewValue();
            }
        }, 1);
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, Number.class, new Getter<>() {
            @Override
            public Number get(PlayerStatisticIncrementEvent e) {
                return e.getNewValue() - e.getPreviousValue();
            }
        }, 0);
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, ItemStack.class, new Getter<>() {
            @Nullable
            @Override
            public ItemStack get(PlayerStatisticIncrementEvent e) {
                Material m = e.getMaterial();
                return m != null ? new ItemStack(m) : null;
            }
        }, 0);
        EventValues.registerEventValue(PlayerStatisticIncrementEvent.class, EntityData.class, new Getter<>() {
            @Nullable
            @Override
            public EntityData<?> get(PlayerStatisticIncrementEvent e) {
                EntityType type = e.getEntityType();
                return type != null && type.getEntityClass() != null ? EntityData.fromClass(type.getEntityClass()) : null;
            }
        }, 0);
    }

    private Literal<Statistic> statisticLiteral;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, @NotNull ParseResult parseResult) {
        statisticLiteral = (Literal<Statistic>) args[0];
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return statisticLiteral == null || statisticLiteral.check(event, ((PlayerStatisticIncrementEvent) event).getStatistic()::equals);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "player stats change";
    }
}
