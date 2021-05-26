package io.github.mrsdarth.skirt.elements.Other.expressions;


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
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@Name("Smelted")
@Description("returns smelt result of items, returns the same item if it cannot be smelted")
@Examples({"on mine:",
        "\tgive player smelted drops of event-block"})
@Since("1.1.0")


public class ExprSmelt extends PropertyExpression<ItemType,ItemType> {

    static {
        Skript.registerExpression(ExprSmelt.class, ItemType.class, ExpressionType.PROPERTY,
                "smelt[ed] [result of] %itemtypes%");
    }


    private ItemStack smelted(ItemStack item) {
        Recipe recipe;
        Material type = item.getType();
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext();) {
            recipe = it.next();
            if ((recipe instanceof FurnaceRecipe)
                    &&
                    (((FurnaceRecipe) recipe).getInput().getType().equals(type)))
                return recipe.getResult();
        } return item;
    }


    @Override
    protected ItemType[] get(Event event, ItemType[] itemTypes) {
        return get(itemTypes, i -> {
            ItemType result = new ItemType();
            for (ItemStack item: i.getAll()) {
                result.add(new ItemData(smelted(item)));
            }
            result.setAll(true);
            return result;
        });
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "smelted items";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends ItemType>) expressions[0]);
        return true;
    }
}
