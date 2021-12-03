package io.github.mrsdarth.skirt.nbt.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemData;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Glowing item")
@Description({"returns a glowing item", "empty enchants instead of enchanting and hiding flags"})
@Examples("give player glinted stone")
@Since("1.0.1")

public class ExprGlowing extends PropertyExpression<ItemType, ItemType> {

    static {
        Skript.registerExpression(ExprGlowing.class, ItemType.class, ExpressionType.PROPERTY,
                "(glowing|shiny|glinted) %itemtypes%");
    }


    private ItemStack glowing(ItemStack item) {
        if (item.getItemMeta().hasEnchants())
            return item;
        NBTCompound nbtItem = NBTItem.convertItemtoNBT(item);
        nbtItem.getOrCreateCompound("tag").getCompoundList("Enchantments").addCompound(new NBTContainer());
        return NBTItem.convertNBTtoItem(nbtItem);

    }

    @Override
    protected ItemType @NotNull [] get(@NotNull Event event, ItemType @NotNull [] source) {
        return get(source, itemType -> {
            ItemType item = new ItemType();
            item.setAll(itemType.isAll());
            itemType.containerIterator().forEachRemaining(itemStack -> item.add(new ItemData(glowing(itemStack))));
            return item;
        });
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "glowing " + getExpr().toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr((Expression<? extends ItemType>) exprs[0]);
        return true;
    }
}
