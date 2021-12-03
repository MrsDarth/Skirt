package io.github.mrsdarth.skirt.elements.statistic;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Player Statistics")
@Description("Values for the stats that can be found under \"Statistics\" in the Game menu. Player kills, time played, item broken etc")
@Examples("send \"you have walked %(walk one cm statistic of player) / 100% meters\"")
@Since("1.2.0")

public class ExprStats extends PropertyExpression<OfflinePlayer, Long> {

    static {
        Skript.registerExpression(ExprStats.class, Long.class, ExpressionType.PROPERTY,
                "[%-itemtype/entitydata%] %statistic% stat[istic][s] of %offlineplayers%",
                "%offlineplayers%'[s] [%-itemtype/entitydata%] %statistic% stat[istic][s]");
    }

    private Expression<?> dataExpr;
    private Expression<Statistic> statisticExpr;

    @Override
    protected Long[] get(@NotNull Event e, OfflinePlayer @NotNull [] source) {
        Statistic stat = statisticExpr.getSingle(e);
        Object data = bukkitObject(dataExpr == null ? null : dataExpr.getSingle(e));
        return stat == null ? null : get(source, player -> {
            try {
                if (data instanceof Material material)
                    return (long) player.getStatistic(stat, material);
                else if (data instanceof EntityType entityType)
                    return (long) player.getStatistic(stat, entityType);
                else
                    return (long) player.getStatistic(stat);
            } catch (IllegalArgumentException exception) {
                return null;
            }
        });
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (dataExpr == null ? "" : dataExpr.toString(e, debug) + " ") + statisticExpr.toString(e, debug) + " statistic of " + getExpr().toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int pattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        dataExpr = exprs[pattern];
        statisticExpr = (Expression<Statistic>) exprs[pattern + 1];
        setExpr((Expression<? extends OfflinePlayer>) exprs[2 - pattern]);
        return true;
    }

    @Override
    public @Nullable
    Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, SET -> CollectionUtils.array(Number.class);
            default -> null;
        };
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        Statistic stat = statisticExpr.getSingle(e);
        if (stat == null || delta == null || delta.length == 0 || !(delta[0] instanceof Number number)) return;
        int change = number.intValue();
        Object data = bukkitObject(dataExpr == null ? null : dataExpr.getSingle(e));
        for (OfflinePlayer player: getExpr().getArray(e)) {
            try {
                if (data instanceof Material material) {
                    switch (mode) {
                        case ADD -> player.incrementStatistic(stat, material, change);
                        case REMOVE -> player.decrementStatistic(stat, material, change);
                        case SET -> player.setStatistic(stat, material, change);
                    }
                } else if (data instanceof EntityType entityType) {
                    switch (mode) {
                        case ADD -> player.incrementStatistic(stat, entityType, change);
                        case REMOVE -> player.decrementStatistic(stat, entityType, change);
                        case SET -> player.setStatistic(stat, entityType, change);
                    }
                } else {
                    switch (mode) {
                        case ADD -> player.incrementStatistic(stat, change);
                        case REMOVE -> player.decrementStatistic(stat, change);
                        case SET -> player.setStatistic(stat, change);
                    }
                }
            } catch (IllegalArgumentException ignore) {}
        }
    }

    private static Object bukkitObject(Object skriptObject) {
        if (skriptObject instanceof ItemType itemType)
            return itemType.getMaterial();
        else if (skriptObject instanceof EntityData<?> entityData) {
            Class<?> entityClass = entityData.getType();
            for (EntityType type: EntityType.values()) {
                Class<?> entityType = type.getEntityClass();
                if (entityType != null && entityType.isAssignableFrom(entityClass))
                    return type;
            }
        }
        return null;
    }

}