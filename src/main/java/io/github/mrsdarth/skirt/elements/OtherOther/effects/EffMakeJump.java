package io.github.mrsdarth.skirt.elements.OtherOther.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

@Name("Make Jump")
@Description("makes the specified entities jump")
@Examples("make all living entities jump")
@Since("1.2.3")

public class EffMakeJump extends Effect {

    static {
        Skript.registerEffect(EffMakeJump.class, "(make|force) %livingentities% [to] jump");
    }

    private Expression<LivingEntity> entity;

    @Override
    protected void execute(Event event) {
        Method jump;
        try {
            jump = Reflectness.nmsclass("EntityLiving").getDeclaredMethod("jump");
        } catch (Exception ex) { return; }
        jump.setAccessible(true);
        entity.stream(event).filter(e -> !(e instanceof Player) && e.isOnGround()).forEach(
                e -> {
                    try {
                        jump.invoke(Reflectness.getHandle(e));
                    } catch (Exception ignored) {}
                }
        );

    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "make entity jump";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        entity = (Expression<LivingEntity>) expressions[0];
        return true;
    }
}
