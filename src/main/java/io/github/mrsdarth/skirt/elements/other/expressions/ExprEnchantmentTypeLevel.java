package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.EnchantmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Enchantment Type level")
@Description("returns the level of an enchantment type. enchantment level of sharpness 10 returns 10")
@Examples("send enchantment level of protection 8")
@Since("2.0.0")

public class ExprEnchantmentTypeLevel extends SimplePropertyExpression<EnchantmentType, Long> {

    static {
        register(ExprEnchantmentTypeLevel.class, Long.class, "enchant[ment] level", "enchantmenttypes");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "enchantment level";
    }

    @Override
    public @Nullable Long convert(EnchantmentType enchantmentType) {
        return (long) enchantmentType.getLevel();
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }
}
