package io.github.mrsdarth.skirt.elements.OtherOther.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.Reflectness;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

@Name("Custom Inventory Name")
@Description("get or change the name of a custom chest inventory. Does not work on other inventories")
@Examples({
        "set {_inv} to chest inventory with 1 row named \"Skirt\"",
        "send \"%name of {_inv}%\" # <none>",
        "send custom inventory name of {_inv} # Skirt"
})
@Since("1.2.3")

public class ExprInventoryName extends SimplePropertyExpression<Inventory, String> {

    private static final Method getInventory = getInventory();

    private static Method getInventory() {
        try {
            return Reflectness.craftclass("inventory.CraftInventory").getDeclaredMethod("getInventory");
        } catch (Exception ignored) {
            return null;
        }
    }

    static {
        register(ExprInventoryName.class, String.class, "custom inventory name", "inventories");
    }


    @Override
    protected String getPropertyName() {
        return "custom inventory name";
    }

    @Nullable
    @Override
    public String convert(Inventory inventory) {
        try {
            return Reflectness.getField("title", getInventory.invoke(inventory)).toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(String.class) : null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        String title = delta[0] instanceof String ? delta[0].toString() : null;
        if (title == null) return;
        for (Inventory inventory: getExpr().getArray(e))
            try {
                Object inv = getInventory.invoke(inventory);
                Reflectness.setField("title", inv, title);
                Reflectness.setField("adventure$title", inv, Component.text(title));
            } catch (Exception ignored) {}
    }
}
