package io.github.mrsdarth.skirt.elements.BoundingBox.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


@Name("Create Bounding Box")
@Description("Creates a bounding box, read [this](https://github.com/MrsDarth/skirt/wiki/BoundingBox) for more info.")
@Examples({"set {_box} to new bounding box with centre vector(0,0,0) with dimensions 1, 2, 3",
        "# creates a bounding box between vector 1, 2, 3 and vector -1, -2, -3"})
@Since("1.0.0")

public class ExprBoundingBox extends SimpleExpression<BoundingBox> {

    static {
        Skript.registerExpression(ExprBoundingBox.class, BoundingBox.class, ExpressionType.COMBINED,
                "(bounding|hit)[ ]box of %entities%",
                "(bounding|hit)[ ]box of %blocks%",
                "[new ]bounding box (from|between) %vector% (and|to) %vector%",
                "[new ]bounding box with centre %vector% with dimensions [x ]%number%, [y ]%number%, [z ]%number%",
                "%boundingboxes%[ with all directions] expanded by %number%",
                "intersection[ box] between %boundingbox% and %boundingbox%");
    }


    @Override
    public Class<? extends BoundingBox> getReturnType() {
        return BoundingBox.class;
    }

    @Override
    public boolean isSingle() {
        switch (pattern) {
            case 0:
                return entity.isSingle();
            case 1:
                return block.isSingle();
            case 4:
                return box.isSingle();
        }
        return true;
    }

    private Expression<Entity> entity;
    private Expression<Block> block;
    private Expression<Vector> vec1;
    private Expression<Vector> vec2;
    private Expression<Number> x;
    private Expression<Number> y;
    private Expression<Number> z;
    private Expression<BoundingBox> box;
    private Expression<BoundingBox> box2;
    private Expression<Number> expansion;
    private int pattern;
    private boolean bool;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        switch (pattern) {
            case 0:
                entity = (Expression<Entity>) exprs[0];
                break;
            case 1:
                block = (Expression<Block>) exprs[0];
                break;
            case 2:
                vec1 = (Expression<Vector>) exprs[0];
                vec2 = (Expression<Vector>) exprs[1];
                break;
            case 3:
                vec1 = (Expression<Vector>) exprs[0];
                x = (Expression<Number>) exprs[1];
                y = (Expression<Number>) exprs[2];
                z = (Expression<Number>) exprs[3];
                break;
            case 4:
                box = (Expression<BoundingBox>) exprs[0];
                expansion = (Expression<Number>) exprs[1];
                break;
            case 5:
                box = (Expression<BoundingBox>) exprs[0];
                box2 = (Expression<BoundingBox>) exprs[1];
        }
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Bounding Box";
    }

    @Override
    @Nullable
    protected BoundingBox[] get(Event event) {
        ArrayList<BoundingBox> boxes = new ArrayList<BoundingBox>();
        int size;
        switch (pattern) {
            case 0:
                Entity[] entities = entity.getArray(event);
                for (Entity e : entities) {
                    boxes.add(e.getBoundingBox());
                }
                return boxes.toArray(new BoundingBox[boxes.size()]);
            case 1:
                Block[] blocks = block.getArray(event);
                for (Block b : blocks) {
                    boxes.add(b.getBoundingBox());
                }
                return boxes.toArray(new BoundingBox[boxes.size()]);
            case 2:
                Vector l1 = vec1.getSingle(event);
                Vector l2 = vec2.getSingle(event);
                if ((l1 != null) && (l2 != null)) {
                    return CollectionUtils.array(BoundingBox.of(l1, l2));
                }
                return null;
            case 3:
                Vector centre = vec1.getSingle(event);
                Number xpos = x.getSingle(event);
                Number ypos = y.getSingle(event);
                Number zpos = z.getSingle(event);
                if (centre != null && xpos != null && ypos != null && zpos != null) {
                    return CollectionUtils.array(BoundingBox.of(centre, xpos.doubleValue(), ypos.doubleValue(), zpos.doubleValue()));
                }
                return null;
            case 4:
                Number expand = expansion.getSingle(event);
                if (expand != null) {
                    double e = expand.doubleValue();
                    ArrayList<BoundingBox> boxes1 = new ArrayList<BoundingBox>();
                    for (BoundingBox b : box.getArray(event)) {
                        boxes1.add(b.clone().expand(e));
                    }
                    return boxes1.toArray(new BoundingBox[boxes1.size()]);
                }
            case 5:
                BoundingBox b1 = box.getSingle(event);
                BoundingBox b2 = box2.getSingle(event);
                if (b1 != null && b2 != null) {
                    return CollectionUtils.array(b1.clone().intersection(b2));
                }
        }
        return null;
    }


}