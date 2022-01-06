package io.github.mrsdarth.skirt.elements.raytrace;


import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


@Name("Ray Trace Results")
@Description("The results of a ray trace")
@Examples("if hit face of {_ray} = north:")
@Since("1.0.0")


public class ExprRayTraceResults extends SimplePropertyExpression<RayTraceResult, Object> {

    static {
        register(ExprRayTraceResults.class, Object.class,
                "hit (block[s]|1¦(vector|position)[s]|2¦location|3¦[block[ ]]fac(e|ing)[s]|4¦entit(y|ies))", "raytraceresults"
        );
    }

    private int pattern;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean kleenean, ParseResult parseResult) {
        pattern = parseResult.mark;
        return super.init(exprs, matchedPattern, kleenean, parseResult);
    }

    @Override
    protected @NotNull String getPropertyName() {
        return switch (pattern) {
            case 0 -> "hit block";
            case 1 -> "hit position";
            case 2 -> "hit location";
            case 3 -> "hit block face";
            case 4 -> "hit entity";
            default -> throw new IllegalStateException();
        };
    }

    @Nullable
    @Override
    public Object convert(RayTraceResult ray) {
        return switch (pattern) {
            case 0 -> ray.getHitBlock();
            case 1 -> ray.getHitPosition();
            case 2 -> Optional.ofNullable(ray.getHitBlock()).map(Block::getWorld).or(() -> Optional.ofNullable(ray.getHitEntity()).map(Entity::getWorld)).map(world -> ray.getHitPosition().toLocation(world)).orElse(null);
            case 3 -> Optional.ofNullable(ray.getHitBlockFace()).map(face -> new Direction(face, 1)).orElse(null);
            case 4 -> ray.getHitEntity();
            default -> null;
        };
    }


    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (pattern) {
            case 0 -> Block.class;
            case 1 -> Vector.class;
            case 2 -> Location.class;
            case 3 -> Direction.class;
            case 4 -> Entity.class;
            default -> throw new IllegalStateException();
        };
    }

}
