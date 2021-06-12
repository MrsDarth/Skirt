package io.github.mrsdarth.skirt.elements.OtherOther.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@Name("Hide Entity")
@Description({"Hides entities for specific or all players", "will be unhidden if player relogs or goes far away", "hiding a player from themselves will do weird things"})
@Examples("hide entity (all entities) from player")
@Since("1.0.1")

public class EffHideEntity extends Effect {

    static {
        Skript.registerEffect(EffHideEntity.class,
                "[(1¦un)]hide [entit(y|ies)] %entities%[ from %-players%]");
    }

    private Expression<Entity> entities;
    private Expression<Player> players;
    private boolean hide;


    @Override
    protected void execute(Event event) {
        Player[] p = (players != null) ? players.getArray(event) : Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
        if (hide) {
            hide(p, entities.getArray(event));
        } else {
            unhide(p, entities.getArray(event));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "hide entity";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        entities = (Expression<Entity>) exprs[0];
        players = (Expression<Player>) exprs[1];
        hide = (parseResult.mark != 1);
        return true;
    }


    private void hide(Player[] players, Entity[] entities) {
        Reflectness.hide(
                Stream.of(entities)
                        .filter(e -> !(e instanceof Player)),
                players);
    }

    private void unhide(Player[] players, Entity[] entities) {
        for (Entity e : entities) {
            if (!(e instanceof Player)) try {
                String l = (e instanceof LivingEntity) ? "Living" : "";
                Object nmsentity = Reflectness.handle(Reflectness.craftclass("entity.Craft" + l + "Entity"), e);
                Object unhidepacket = (Reflectness.nmsclass("PacketPlayOutSpawnEntity" + l).getDeclaredConstructor(Reflectness.nmsclass("Entity" + l))).newInstance(nmsentity);
                for (Player p : players) {
                    Reflectness.sendpacket(p, unhidepacket);
                    Reflectness.refresh(e, p);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
