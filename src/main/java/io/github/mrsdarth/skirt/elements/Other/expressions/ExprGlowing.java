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
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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
        if (!item.getItemMeta().hasEnchants()) {

            try {
                Class cstack = Reflectness.craftclass("inventory.CraftItemStack");
                Class istack = Reflectness.nmsclass("ItemStack");
                Class taglist = Reflectness.nmsclass("NBTTagList");
                Class compound = Reflectness.nmsclass("NBTTagCompound");
                Class nbtbase = Reflectness.nmsclass("NBTBase");

                Constructor tlist = taglist.getConstructor();
                Constructor tcompound = compound.getConstructor();

                Method toNMS = cstack.getDeclaredMethod("asNMSCopy", ItemStack.class);
                Method gettag = istack.getDeclaredMethod("getOrCreateTag");
                Method settag = istack.getDeclaredMethod("setTag", compound);
                Method mirror = cstack.getDeclaredMethod("asCraftMirror", istack);
                Method tset = compound.getDeclaredMethod("set", String.class, nbtbase);
                Method add = taglist.getDeclaredMethod("add", int.class, nbtbase);

                Object nmsitem = toNMS.invoke(null, item);
                Object etag = tlist.newInstance();
                add.invoke(etag, 0, tcompound.newInstance());
                Object nbt = gettag.invoke(nmsitem, null);
                tset.invoke(nbt, "Enchantments", etag);
                settag.invoke(nmsitem, nbt);
                return (ItemStack) mirror.invoke(null, nmsitem);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return item;
    }

    @Override
    protected ItemType[] get(Event event, ItemType[] itemTypes) {
        return get(itemTypes, i -> {
            ItemType result = new ItemType();
            for (ItemStack item : i.getAll()) {
                result.add(new ItemData(glowing(item)));
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
        return "glowing item";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends ItemType>) expressions[0]);
        return true;
    }
}
