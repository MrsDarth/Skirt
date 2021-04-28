package io.github.mrsdarth.skirt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.skript.util.AABB;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import java.util.ArrayList;

@Name("Bounding Box Blocks")
@Description("returns blocks within a bounding box given a world")
@Examples({"set {_b::*} to blocks within bounding box of player expanded by 0.01",
        "if ({_b::*} where [input is solid]) is set:",
        "\tsend \"%player% is touching a block!\""})
@Since("1.0.1")

public class ExprBoundingBoxBlocks extends SimpleExpression {

    static {
        Skript.registerExpression(ExprBoundingBoxBlocks.class,Block.class, ExpressionType.COMBINED,
                "blocks within [[bounding] box] %boundingboxes%[ in %world%]");
    }

    private Expression<BoundingBox> boxes;
    private Expression<World> w;

    @Nullable
    @Override
    protected Block[] get(Event event) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        World world = w.getSingle(event);
        if (world == null) return null;
        for (BoundingBox b: boxes.getArray(event)) {
            blocks.addAll(Lists.newArrayList(new AABB(b.getMin().toLocation(world),b.getMax().toLocation(world))));
        }
        return blocks.toArray(new Block[blocks.size()]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class getReturnType() {
        return Block.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "bounding box blocks";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        boxes = (Expression<BoundingBox>) exprs[0];
        w = (Expression<World>) exprs[1];
        return true;
    }
}
