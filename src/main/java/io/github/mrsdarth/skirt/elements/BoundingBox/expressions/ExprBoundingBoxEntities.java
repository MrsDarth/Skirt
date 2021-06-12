package io.github.mrsdarth.skirt.elements.BoundingBox.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Predicate;

@Name("Bounding Box Entities")
@Description("gets entities within a bounding box")
@Examples("kill all zombies within box {_box} in \"world\"")
@Since("1.1.0")

public class ExprBoundingBoxEntities extends SimpleExpression {

    static {
        Skript.registerExpression(ExprBoundingBoxEntities.class, Entity.class, ExpressionType.COMBINED,
                "[all] %entitydatas% within [[bounding] box] %boundingboxes%[ in %world%]");
    }

    @Nullable
    @Override
    protected Entity[] get(Event event) {
        World w = world.getSingle(event);
        if (w == null) return null;
        EntityData<?>[] entitytypes = types.getArray(event);
        Predicate<Entity> filter = e -> {
            for (EntityData<?> type : entitytypes) {
                if (type.isInstance(e)) return true;
            }
            return false;
        };
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (BoundingBox box : boxes.getArray(event)) {
            entities.addAll(w.getNearbyEntities(box, filter));
        }
        return entities.toArray(new Entity[entities.size()]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "bounding box entities";
    }


    private Expression<EntityData> types;
    private Expression<BoundingBox> boxes;
    private Expression<World> world;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        types = (Expression<EntityData>) exprs[0];
        boxes = (Expression<BoundingBox>) exprs[1];
        world = (Expression<World>) exprs[2];
        return true;
    }


}
