package io.github.mrsdarth.skirt.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;

import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;


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
                    glow = enumcolor.getDeclaredMethod("valueOf", String.class).invoke(null, ecc(glowcolor.getName()));
                }
            }
            Class<?> teampacketclass = Reflectness.nmsclass("PacketPlayOutScoreboardTeam");
            Constructor newteam = teampacketclass.getDeclaredConstructor();

            Player[] players = (playerExpression != null) ? playerExpression.getArray(event) : Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);


            for (Entity e : entityExpression.getArray(event)) {

                String name = ("§" + String.join("§", e.getUniqueId().toString().split(""))).substring(0, 16);

                Object removepacket = newteam.newInstance(), createpacket = newteam.newInstance();

                Field teamname = teampacketclass.getDeclaredField("a");
                Field mode = teampacketclass.getDeclaredField("i");
                mode.setAccessible(true);
                teamname.setAccessible(true);
                mode.set(removepacket, 1);
                teamname.set(removepacket, name);

                if (!reset) {
                    Field entries = teampacketclass.getDeclaredField("h");
                    Field color = teampacketclass.getDeclaredField("g");
                    entries.setAccessible(true);
                    color.setAccessible(true);
                    teamname.set(createpacket, name);
                    color.set(createpacket, glow);
                    ((Collection<String>) entries.get(createpacket)).add(((e instanceof Player) ? e.getName() : e.getUniqueId()).toString());
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

    private String ecc(String color) {
        switch (color) {
            case "light cyan": return "AQUA";
            case "cyan": return "DARK_AQUA";
            case "brown": return "BLUE";
            case "blue": return "DARK_BLUE";
            case "orange": return "GOLD";
            case "grey": return "GRAY";
            case "dark grey": return "DARK_GRAY";
            case "green": return "DARK_GREEN";
            case "light green": return "GREEN";
            case "red": return "DARK_RED";
            case "pink": return "RED";
            case "magenta": return "LIGHT_PURPLE";
            case "purple": return "DARK_PURPLE";
        }
        return color.toUpperCase().replace(" ","_");
    }

}
