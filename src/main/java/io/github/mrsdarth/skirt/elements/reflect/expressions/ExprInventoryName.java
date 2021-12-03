package io.github.mrsdarth.skirt.elements.reflect.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.Reflectness;
import io.papermc.paper.adventure.PaperAdventure;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Custom Inventory Name")
@Description("get or change the name of a custom inventory. May not work for real inventories")
@Examples({
        "set {_inv} to chest inventory with 1 row named \"Skirt\"",
        "send custom inventory name of {_inv} # Skirt"
})
@Since("1.2.3")

public class ExprInventoryName extends SimplePropertyExpression<Inventory, String> {

    static {
        register(ExprInventoryName.class, String.class, "custom inventory name", "inventories");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "custom inventory name";
    }

    @Nullable
    @Override
    public String convert(Inventory inventory) {
        Object craftInventory = Reflectness.getField("inventory", inventory);
        return craftInventory == null ? null : Reflectness.getField("title", craftInventory).toString();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(String.class) : null;
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        String title = (delta != null && delta.length != 0 && delta[0] instanceof String string) ? string : null;
        if (title == null) return;
        for (Inventory inventory: getExpr().getArray(e)) {
            Object craftInventory = Reflectness.getField("inventory", inventory);
            if (craftInventory != null) {
                Reflectness.setField("title", craftInventory, title);
                Reflectness.setField("adventure$title", craftInventory, PaperAdventure.LEGACY_SECTION_UXRC.deserialize(title));
            }
        }
    }
}
