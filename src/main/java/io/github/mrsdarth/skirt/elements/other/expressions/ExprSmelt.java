package io.github.mrsdarth.skirt.elements.other.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@Name("Smelted")
@Description("returns smelt result of items, returns the same item if it cannot be smelted")
@Examples({"on mine:",
        "\tgive player smelted drops of event-block"})
@Since("1.1.0")


public class ExprSmelt extends SimplePropertyExpression<ItemType, ItemType> {

    static {
        Skript.registerExpression(ExprSmelt.class, ItemType.class, ExpressionType.PROPERTY,
                "smelt[ed] [result of] %itemtypes%",
                "%itemtypes% smelted [result]");
    }


    private ItemStack smelted(ItemStack item) {
        Material type = item.getType();
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext())
            if (it.next() instanceof FurnaceRecipe recipe && recipe.getInput().getType().equals(type)) {
                ItemStack result = item.clone();
                result.setType(recipe.getResult().getType());
                return result;
            }
        return item;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "smelt result";
    }

    @Nullable
    @Override
    public ItemType convert(ItemType itemType) {
        return new ItemType(smelted(itemType.getRandom()));
    }
}
