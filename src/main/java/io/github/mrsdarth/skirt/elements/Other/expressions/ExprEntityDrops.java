package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

@Name("Drops of entity")
@Description("generates the drops of an entity with an optional attacker. This takes into account the looting of the player's tool and luck potion effect")
@Examples({
        "on death:",
        "\tname of victim = \"5x creeper\"",
        "\tdrop 5 mob drops of victim with attacker"
})
@Since("1.2.2")

public class ExprEntityDrops extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprEntityDrops.class, ItemType.class, ExpressionType.COMBINED,
                "[%-number%] (entity|mob|generate[d]) drops of %entities/blocks%[ (with|using) [attacker] %-player%]");
    }

    private Expression<Number> amount;
    private Expression<?> lootable;
    private Expression<Player> attacker;

    @Nullable
    @Override
    protected ItemType[] get(Event event) {
        Number repeat = amount != null ? amount.getSingle(event) : 1;
        if (repeat == null) return null;
        int it = repeat.intValue();
        ArrayList<ItemStack> items = new ArrayList<>();
        HumanEntity killer = attacker != null ? attacker.getSingle(event) : null;
        for (Object e: lootable.getArray(event)) {
            if (!(e instanceof Lootable && ((Lootable) e).hasLootTable())) continue;
            LootTable lootTable = ((Lootable) e).getLootTable();
            LootContext lootContext = new LootContext.Builder((e instanceof Block ? ((Block) e).getLocation() : ((Entity) e).getLocation()))
                    .killer(killer)
                    .lootedEntity((e instanceof Entity) ? ((Entity) e) : null)
                    .build();
            for (int i = 0; i < it; i++) {
                items.addAll(lootTable.populateLoot(new Random(), lootContext));
            }
        }
        return items.stream().map(ItemType::new).toArray(ItemType[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "entity drops";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        amount = (Expression<Number>) exprs[0];
        lootable = exprs[1];
        attacker = (Expression<Player>) exprs[2];
        return true;
    }
}
