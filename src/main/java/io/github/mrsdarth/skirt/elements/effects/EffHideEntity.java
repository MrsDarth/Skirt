package io.github.mrsdarth.skirt.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;

import io.github.mrsdarth.skirt.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

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

    private Class packetclass = Main.nmsclass("Packet");
    private Class hidepacket = Main.nmsclass("PacketPlayOutEntityDestroy");

    private Class craftplayer = Main.craftclass("entity.CraftPlayer");



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


    private void sendp(Player p, Object packet) {
        try {
            Object entityplayer = (craftplayer.getMethod("getHandle").invoke(craftplayer.cast(p)));
            Field pc = entityplayer.getClass().getDeclaredField("playerConnection");
            pc.setAccessible(true);
            Object fpc = pc.get(entityplayer);
            fpc.getClass().getDeclaredMethod("sendPacket", packetclass).invoke(fpc, packet);
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void hide(Player[] players, Entity[] entities) {
        for (Entity e: entities) {
            if (!(e instanceof Player)) try {
                Constructor newp = hidepacket.getDeclaredConstructor(int[].class);
                Object packet = newp.newInstance(new int[] {e.getEntityId()});
                for (Player p : players) {
                    sendp(p, packet);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void unhide(Player[] players, Entity[] entities) {
        for (Entity e: entities) {
            if (!(e instanceof Player)) try {
                String l = (e instanceof LivingEntity) ? "Living" : "";
                Object craftentity = (Main.craftclass("entity.Craft" + l + "Entity").cast(e));
                Object nmsentity = craftentity.getClass().getDeclaredMethod("getHandle").invoke(craftentity);
                Object unhidepacket = (Main.nmsclass("PacketPlayOutSpawnEntity" + l).getDeclaredConstructor(Main.nmsclass("Entity" + l))).newInstance(nmsentity);
                for (Player p: players) {
                    sendp(p, unhidepacket);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
