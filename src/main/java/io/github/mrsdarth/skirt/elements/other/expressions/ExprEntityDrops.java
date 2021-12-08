package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Random;
import java.util.stream.IntStream;

@Name("Drops of entity")
@Description("generates the drops of an entity with an optional attacker. This takes into account the looting of the player's tool and luck potion effect")
@Examples({"on death:",
        "\tname of victim = \"5x creeper\"",
        "\tdrop 5 mob drops of victim using attacker"})
@Since("1.2.2")

public class ExprEntityDrops extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprEntityDrops.class, ItemType.class, ExpressionType.COMBINED,
                "[%-number% [of]] (entity|mob|generate[d]) drops of %entities/blocks%[ (with|using) [attacker] %-player%]");
    }

    private Expression<Number> numberExpr;
    private Expression<?> targetExpr;
    private Expression<Player> playerExpr;

    @Override
    protected @Nullable
    ItemType[] get(@NotNull Event e) {
        Number repeat = numberExpr == null ? 1 : numberExpr.getSingle(e);
        Object o = targetExpr.getSingle(e);
        LootTable lootTable;
        if (repeat == null || !(o instanceof Lootable lootable && (lootTable = lootable.getLootTable()) != null)) return null;
        LootContext lootContext = new LootContext.Builder(lootable instanceof Block block ? block.getLocation() : ((Entity) lootable).getLocation())
                .killer(playerExpr == null ? null : playerExpr.getSingle(e))
                .lootedEntity(lootable instanceof Entity entity ? entity : null)
                .build();
        Random random = new Random();
        return IntStream.range(0, repeat.intValue())
                .mapToObj(i -> lootTable.populateLoot(random, lootContext))
                .flatMap(Collection::stream)
                .map(ItemType::new)
                .toArray(ItemType[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (numberExpr == null ? "" : numberExpr.toString(e, debug) + " ") + "entity drops of " + targetExpr.toString(e, debug) + (playerExpr == null ? "" : " with attacker " + playerExpr.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        numberExpr = (Expression<Number>) exprs[0];
        targetExpr = exprs[1];
        playerExpr = (Expression<Player>) exprs[2];
        return true;
    }
}
