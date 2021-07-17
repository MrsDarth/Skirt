package io.github.mrsdarth.skirt.elements.OtherOther.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import com.google.common.collect.Iterables;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;

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

public class EffGlowColor extends Effect {

    static {
        Skript.registerEffect(EffGlowColor.class,
                "set glow[ing] color of %entities% to %color%[ for %-players%]",
                "(reset|delete|remove) glow[ing] color of %entities%[ for %-players%]");
    }

    private Expression<Entity> entityExpression;
    private Expression<Color> color;
    private Expression<Player> playerExpression;

    private boolean reset;

    @Override
    protected void execute(Event event) {
        try {
            Object glow = null;

            if (!reset) {
                Color glowcolor = color.getSingle(event);
                if (glowcolor == null) return;
                else {
                    Class<?> enumcolor = Reflectness.nmsclass("EnumChatFormat");
                    if (!(glowcolor instanceof SkriptColor)) return;
                    String color = ((SkriptColor) glowcolor).asChatColor().asBungee().getName();
                    glow = enumcolor.getDeclaredMethod("valueOf", String.class).invoke(null, color.toUpperCase());
                }
            }
            Class<?> teampacketclass = Reflectness.nmsclass("PacketPlayOutScoreboardTeam");
            Constructor newteam = teampacketclass.getDeclaredConstructor();

            Player[] players = (playerExpression != null) ? playerExpression.getArray(event) : Iterables.toArray(Bukkit.getOnlinePlayers(), Player.class);


            for (Entity e : entityExpression.getArray(event)) {

                String name = ("§" + String.join("§", e.getUniqueId().toString().split(""))).substring(0, 16);

                Object removepacket = newteam.newInstance(), createpacket = newteam.newInstance();

                Reflectness.setField("i", teampacketclass, removepacket, 1);
                Reflectness.setField("a", teampacketclass, removepacket, name);

                if (!reset) {
                    Reflectness.setField("a", teampacketclass, createpacket, name);
                    Reflectness.setField("g", teampacketclass, createpacket, glow);
                    ((Collection<String>) Reflectness.getField("h", teampacketclass, createpacket)).add(((e instanceof Player) ? e.getName() : e.getUniqueId()).toString());
                }

                for (Player p : players) {
                    Reflectness.sendpacket(p, removepacket);
                    if (!reset) Reflectness.sendpacket(p, createpacket);
                }


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "entity glow color";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        reset = (i == 1);
        entityExpression = (Expression<Entity>) exprs[0];
        color = (Expression<Color>) exprs[1];
        playerExpression = (Expression<Player>) exprs[reset ? 1 : 2];
        return true;
    }

}
