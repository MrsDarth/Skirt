package io.github.mrsdarth.skirt.protocolLib.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.AbstractStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.github.mrsdarth.skirt.protocolLib.PLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Name("Color Entity Glow")
@Description({"Allows you to change the glow color of entities for specific or all players",
        "Gets reset for players who relog",
        "Only visible if the entity is already glowing"})
@Examples({"command rainbow:",
        "\ttrigger:",
        "\t\tset {_colors::*} to pink, red, orange, yellow, lime, cyan, indigo, purple and magenta\n",
        "\t\tset glowing of player to true",
        "\t\tloop 100 times:",
        "\t\t\tloop {_colors::*}:",
        "\t\t\t\tset glow color of player to loop-value-2",
        "\t\t\t\twait 5 ticks"})
@Since("1.1.0")
@RequiredPlugins("ProtocolLib")

public class EffGlowColor extends Effect {

    static {
        Skript.registerEffect(EffGlowColor.class,
                "set glow[ing] color of %entities% to %color%[ for %-players%]",
                "(reset|delete|remove) glow[ing] color of %entities%[ for %-players%]");
    }

    private Expression<Entity> entityExpr;
    private Expression<Color> colorExpr;
    private Expression<Player> playerExpr;

    private boolean hasColor;

    @Override
    protected void execute(@NotNull Event e) {
        List<Player> receivers = playerExpr == null ? List.copyOf(Bukkit.getOnlinePlayers()) : List.of(playerExpr.getArray(e));
        if (receivers.isEmpty()) return;
        for (Entity entity : entityExpr.getArray(e)) {
            String team = "Skirt-" + entity.getUniqueId().toString().replace("-", "").substring(0, 10);
            PLib.sendPacket(teamPacket(team, 1), receivers);
            if (hasColor) {
                Color color = colorExpr.getSingle(e);
                if (color instanceof SkriptColor skriptColor)
                    PLib.sendPacket(createTeam(entity, team, skriptColor.asChatColor().ordinal()), receivers);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (hasColor ? "" : "re") + "set glow color of " + entityExpr.toString(e, debug) + (colorExpr == null ? "" : " to " + colorExpr.toString(e, debug)) + (playerExpr == null ? "" : " for " + playerExpr.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        entityExpr = (Expression<Entity>) exprs[0];
        playerExpr = (Expression<Player>) exprs[exprs.length - 1];
        hasColor = matchedPattern == 0;
        if (hasColor) {
            colorExpr = (Expression<Color>) exprs[1];
            return !colorExpr.toString().startsWith("rgb");
        }
        return true;
    }

    private static PacketContainer teamPacket(String team, int mode) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getStrings().write(0, team);
        packet.getIntegers().write(0, mode);
        return packet;
    }

    private static PacketContainer createTeam(Entity entity, String team, int colorOrdinal) {
        PacketContainer createTeam = teamPacket(team, 0);
        createTeam.getSpecificModifier(Collection.class).write(0, new ArrayList<>(List.of(entity instanceof Player player ? player.getName() : entity.getUniqueId().toString())));
        StructureModifier<Object> genericModifier = createTeam.getModifier();
        genericModifier.writeDefaults();
        StructureModifier<Object> modifier;
        if (Skript.isRunningMinecraft(1, 17)) {
            try {
                StructureModifier<Object> teamModifier = createTeam.getModifier().withType(Optional.class);
                Class<?> teamClass = (Class<?>) ((ParameterizedType) teamModifier.getField(0).getGenericType()).getActualTypeArguments()[0];
                AbstractStructure teamObject = PLib.unsafeWrap(teamClass);
                if (teamObject == null) return null;
                teamModifier.write(0, teamObject.getHandle());
                PLib.writeAll(teamObject.getChatComponents(), WrappedChatComponent.fromText(""));
                PLib.writeAll(teamObject.getStrings(), "always");
                modifier = teamObject.getModifier();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else
            modifier = genericModifier;
        StructureModifier<Object> colorModifier = modifier.withType(Enum.class);
        colorModifier.write(0, colorModifier.getField(0).getType().getEnumConstants()[colorOrdinal]);
        return createTeam;
    }

}

