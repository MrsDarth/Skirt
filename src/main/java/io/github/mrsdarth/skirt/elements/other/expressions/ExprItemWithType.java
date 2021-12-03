package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprItemWithType extends PropertyExpression<ItemType, ItemType> {

    static {
        Skript.registerExpression(ExprItemWithType.class, ItemType.class, ExpressionType.PROPERTY,
                "%itemtypes% with type %itemtype%");
    }

    private Expression<ItemType> itemTypeExpr;

    @Override
    protected ItemType[] get(@NotNull Event e, ItemType @NotNull [] source) {
        ItemType item = itemTypeExpr.getSingle(e);
        if (item == null) return null;
        Material type = item.getMaterial();
        return get(source, itemType -> {
            ItemStack itemStack = itemType.getRandom().clone();
            itemStack.setType(type);
            return new ItemType(itemStack);
        });
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return getExpr().toString(e, debug) + " with type " + itemTypeExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        setExpr((Expression<? extends ItemType>) exprs[0]);
        itemTypeExpr = (Expression<ItemType>) exprs[1];
        return true;
    }
}
