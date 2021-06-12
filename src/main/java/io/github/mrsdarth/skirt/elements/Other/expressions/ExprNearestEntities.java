package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Name("Nearest Entities")
@Description("get the nearest x entities to a location sorted")
@Examples("give diamond to nearest player from {prize}")
@Since("1.1.0")

public class ExprNearestEntities extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprNearestEntities.class, Entity.class, ExpressionType.COMBINED,
                "[the] nearest [%-number%] %entitydatas% (from|of|to) %location%");
    }

    @Nullable
    @Override
    protected Entity[] get(Event event) {
        Number num = amount != null ? amount.getSingle(event) : 1;
        Location l = loc.getSingle(event);
        if (l == null || num == null) return null;
        int a = num.intValue();
        List<Entity> entities = Arrays.asList(EntityData.getAll(type.getAll(event), Entity.class, CollectionUtils.array(l.getWorld())));
        Collections.sort(entities, new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return (int) (l.distanceSquared(o1.getLocation()) - l.distanceSquared(o2.getLocation()));
            }
        });
        return entities.stream().limit(a).collect(Collectors.toList()).toArray(new Entity[a]);
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "Nearest entities";
    }

    private boolean isSingle;
    private Expression<Number> amount;
    private Expression<EntityData> type;
    private Expression<Location> loc;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        amount = (Expression<Number>) exprs[0];
        isSingle = (amount == null) || (amount instanceof Literal && ((Literal<Number>) amount).getSingle().intValue() == 1);
        type = (Expression<EntityData>) exprs[1];
        loc = (Expression<Location>) exprs[2];
        return true;
    }
}
