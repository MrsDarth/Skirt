package io.github.mrsdarth.skirt.elements.reflect.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.utility.MinecraftReflection;
import io.github.mrsdarth.skirt.Reflectness;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.protocolLib.PLib;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

@Name("Make Jump")
@Description("makes the specified entities jump")
@Examples("make all living entities jump")
@Since("1.2.3")

public class EffMakeJump extends Effect {

    private static Method jump;

    static {
        if (Skirtness.hasProtocolLib()) {
            Class<?> livingEntityClass = Reflectness.classForName(MinecraftReflection.getEntityClass().getName() + "Living");
            if (livingEntityClass != null) {
                jump = Reflectness.getMethod(livingEntityClass, "jump");
                Skript.registerEffect(EffMakeJump.class, "(make|force) %livingentities% [to] jump");
            }
        }
    }

    private Expression<LivingEntity> livingEntityExpr;

    @Override
    protected void execute(@NotNull Event e) {
        for (LivingEntity livingEntity: livingEntityExpr.getArray(e))
            Reflectness.invoke(jump, PLib.toNMS(livingEntity));
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "make entity jump";
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        livingEntityExpr = (Expression<LivingEntity>) exprs[0];
        return true;
    }
}
