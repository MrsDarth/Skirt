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
import com.google.common.collect.Iterables;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Hide Entity")
@Description({"Hides entities for specific or all players", "will be unhidden if player relogs or goes far away", "hiding a player from themselves will do weird things"})
@Examples("hide entities (all mobs) from player")
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
        Player[] p = (players != null) ? players.getArray(event) : Iterables.toArray(Bukkit.getOnlinePlayers(), Player.class);
        if (hide) {
            Reflectness.hide(entities.getArray(event), p);
        } else {
            unhide(entities.getArray(event), p);
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



    private void unhide(Entity[] entities, Player[] players) {
        for (Entity e : entities) {
            try {
                if (e instanceof Player)
                    Reflectness.refresh((Player) e);
                else {
                    String l = (e instanceof LivingEntity) ? "Living" : "";
                    Object nmsentity = Reflectness.getHandle(e);
                    Object unhidepacket = (Reflectness.nmsclass("PacketPlayOutSpawnEntity" + l).getDeclaredConstructor(Reflectness.nmsclass("Entity" + l))).newInstance(nmsentity);
                    for (Player p : players) {
                        Reflectness.sendpacket(p, unhidepacket);
                        Reflectness.refresh(e, p);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
