package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.EnchantmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprEnchantmentTypeLevel extends SimplePropertyExpression<EnchantmentType, Long> {

    static {
        register(ExprEnchantmentTypeLevel.class, Long.class, "enchant[ment] level", "enchantmenttypes");
    }

    @Override
    protected String getPropertyName() {
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
