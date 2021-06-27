package io.github.mrsdarth.skirt.elements.Statistics;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Player Statistics")
@Description("Values for the stats that can be found under \"Statistics\" in the Game menu. Player kills, time played, item broken etc")
@Examples("send \"you have walked %(walk one cm statistic of player) / 100% meters\"")
@Since("1.2.0")

public class ExprStats extends PropertyExpression<Player, Number> {

    static {
        Skript.registerExpression(ExprStats.class, Number.class, ExpressionType.PROPERTY,
                "[%-itemtype/entitydata%] %statistic% stat[istic][s] of %players%",
                "%players%'[s] [%-itemtype/entitydata%] %statistic% stat[istic][s]");
    }

    private Expression<Statistic> stats;
    private Expression<?> type;


    @Override
    protected Number[] get(Event event, Player[] players) {
        Statistic stat = stats.getSingle(event);
        if (stat == null) return null;
        return get(players, new Converter<Player, Number>() {
            @Nullable
            @Override
            public Number convert(Player player) {
                Object o = fromskript(event, type);
                System.out.println(o);
                if (o == null) return null;
                else if (o instanceof Material && isMaterial(stat)) return player.getStatistic(stat, (Material) o);
                else if (o instanceof EntityType && isEntity(stat)) player.getStatistic(stat, (EntityType) o);
                else if (isNone(stat)) return player.getStatistic(stat);
                return null;
            }
        });
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "player statistics";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        type = exprs[i];
        stats = (Expression<Statistic>) exprs[i + 1];
        setExpr((Expression<? extends Player>) exprs[i == 0 ? 2 : 0]);
        return true;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case REMOVE:
            case ADD:
            case RESET:
                return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        Number n = mode != Changer.ChangeMode.RESET ? ((Number) delta[0]) : 0;
        Statistic stat = stats.getSingle(e);
        if (n == null || stat == null) return;
        int value = n.intValue();
        Object o = fromskript(e, stats);
        for (Player p : getExpr().getArray(e)) {
            switch (mode) {
                case ADD:
                    increment(p, stat, value, o);
                    break;
                case REMOVE:
                    decrement(p, stat, value, o);
                    break;
                case SET:
                    setstats(p, stat, value, o);
                    break;
                default:
                    setstats(p, stat, 0, o);
            }
        }
    }

    private Object fromskript(Event e, @Nullable Expression<?> expr) {
        if (expr == null) return "L";
        Object o = expr.getSingle(e);
        if (o instanceof ItemType) return ((ItemType) o).getMaterial();
        else if (o instanceof EntityData) {
            Class<?> entityclass = ((EntityData) o).getType();
            for (EntityType entityType : EntityType.values()) {
                if (entityType.getEntityClass().isAssignableFrom(entityclass)) return entityType;
            }
        }
        return null;
    }

    private boolean isMaterial(Statistic stat) {
        Statistic.Type type = stat.getType();
        return type == Statistic.Type.BLOCK || type == Statistic.Type.ITEM;
    }

    private boolean isEntity(Statistic stat) {
        return stat.getType() == Statistic.Type.ENTITY;
    }

    private boolean isNone(Statistic stat) {
        return stat.getType() == Statistic.Type.UNTYPED;
    }

    private void increment(Player p, Statistic stat, int inc, @Nullable Object o) {
        if (o != null) {
            if (o instanceof Material && isMaterial(stat)) p.incrementStatistic(stat, (Material) o, inc);
            else if (o instanceof EntityType && isEntity(stat)) p.incrementStatistic(stat, (EntityType) o, inc);
            else if (isNone(stat)) p.incrementStatistic(stat, inc);
        }
    }

    private void decrement(Player p, Statistic stat, int inc, @Nullable Object o) {
        if (o != null) {
            if (o instanceof Material && isMaterial(stat)) p.decrementStatistic(stat, (Material) o, inc);
            else if (o instanceof EntityType && isEntity(stat)) p.decrementStatistic(stat, (EntityType) o, inc);
            else if (isNone(stat)) p.decrementStatistic(stat, inc);
        }
    }

    private void setstats(Player p, Statistic stat, int inc, @Nullable Object o) {
        if (o != null) {
            if (o instanceof Material && isMaterial(stat)) p.setStatistic(stat, (Material) o, inc);
            else if (o instanceof EntityType && isEntity(stat)) p.setStatistic(stat, (EntityType) o, inc);
            else if (isNone(stat)) p.setStatistic(stat, inc);
        }
    }


}
