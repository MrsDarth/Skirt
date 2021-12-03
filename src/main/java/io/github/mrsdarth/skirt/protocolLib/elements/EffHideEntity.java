package io.github.mrsdarth.skirt.protocolLib.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import io.github.mrsdarth.skirt.protocolLib.PLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Name("Hide Entity")
@Description({"Hides entities for specific or all players", "will be unhidden if player relogs or goes far away", "hiding a player from themselves will do weird things"})
@Examples("hide entities (all mobs) from player")
@Since("1.0.1")

public class EffHideEntity extends Effect {

    static {
        Skript.registerEffect(EffHideEntity.class, "[(1¦un)]hide entit(y|ies) %entities%[ from %-players%]");
    }

    private Expression<Entity> entityExpr;
    private Expression<Player> playerExpr;

    private boolean hide;

    @Override
    protected void execute(@NotNull Event e) {
        List<Player> receivers = playerExpr == null ? List.copyOf(Bukkit.getOnlinePlayers()) : List.of(playerExpr.getArray(e));
        Entity[] entities = entityExpr.getArray(e);
        if (receivers.isEmpty()) return;
        if (hide) {
            IntStream entityIds = Arrays.stream(entities).mapToInt(Entity::getEntityId);
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
            if (Skript.isRunningMinecraft(1, 17)) entityIds.forEach(packet.getIntLists().writeDefaults().read(0)::add);
            else packet.getIntegerArrays().write(0, entityIds.toArray());
            PLib.sendPacket(packet, receivers);
        } else
            PLib.updateEntities(entities, receivers);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (hide ? "" : "un") + "hide entity " + entityExpr.toString(e, debug) + (playerExpr == null ? "" : " from " + playerExpr.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        hide = parseResult.mark == 0;
        entityExpr = (Expression<Entity>) exprs[0];
        playerExpr = (Expression<Player>) exprs[1];
        return true;
    }
}
