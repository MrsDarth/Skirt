package io.github.mrsdarth.skirt.elements.boundingbox.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.AABB;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.StreamSupport;

@Name("Bounding Box Blocks")
@Description("returns blocks within a bounding box given a world")
@Examples({"if blocks within bounding box of player expanded by 0.01 is not air:",
        "\tsend \"%player% is touching a block!\""})
@Since("1.0.1")

public class ExprBoundingBoxBlocks extends SimpleExpression<Block> {

    static {
        Skript.registerExpression(ExprBoundingBoxBlocks.class, Block.class, ExpressionType.COMBINED,
                "blocks within [bounding] box %boundingboxes%[ in %world%]");
    }

    private Expression<BoundingBox> boxExpr;
    private Expression<World> worldExpr;

    @Override
    protected @Nullable
    Block[] get(@NotNull Event e) {
        return Skirtness.getSingle(worldExpr, e).map(world -> Arrays.stream(boxExpr.getArray(e))
                .map(box -> new AABB(world, box.getMin(), box.getMax()))
                .flatMap(aabb -> StreamSupport.stream(aabb.spliterator(), false))
                .toArray(Block[]::new)).orElse(null);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "blocks within bounding box " + boxExpr.toString(e, debug) + " in " + worldExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        boxExpr = (Expression<BoundingBox>) exprs[0];
        worldExpr = (Expression<World>) exprs[1];
        return true;
    }
}
