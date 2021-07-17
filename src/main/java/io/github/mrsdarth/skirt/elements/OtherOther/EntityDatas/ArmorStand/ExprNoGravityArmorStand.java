package io.github.mrsdarth.skirt.elements.OtherOther.EntityDatas.ArmorStand;

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
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Skirt Armor Stand")
@Description("use this entity type to spawn an armor stand that floats with no gravity. Only works on 1.16.5 for now")
@Examples("spawn skirt armor stand at player")
@Since("1.2.3")


public class ExprNoGravityArmorStand extends SimpleExpression<EntityData> {

    static {
        if (Skript.classExists("net.minecraft.server.v1_16_R3.EntityArmorStand")) {
            Skript.registerExpression(ExprNoGravityArmorStand.class, EntityData.class, ExpressionType.SIMPLE,
                    "(skirt|no gravity) armor stand");
        }
    }

    @Nullable
    @Override
    protected EntityData[] get(Event event) {
        return CollectionUtils.array(new ArmorStandData());
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends EntityData> getReturnType() {
        return EntityData.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "skirt armor stand";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
