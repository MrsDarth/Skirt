package io.github.mrsdarth.skirt.protocolLib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.AbstractStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.reflect.StructureModifier;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import sun.misc.Unsafe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PLib {


    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();


    private static final BukkitUnwrapper UNWRAPPER = BukkitUnwrapper.getInstance();


    public static Object toNMS(Object o) {
        return UNWRAPPER.unwrapItem(o);
    }



    public static void addListener(PacketListener listener) {
        PROTOCOL_MANAGER.addPacketListener(listener);
    }




    public static void sendPacket(PacketContainer packet, Player player) {
        try { PROTOCOL_MANAGER.sendServerPacket(player, packet); } catch (Exception ignored) {}
    }

    public static void sendPacket(PacketContainer packet, Player[] players) {
        for (Player player: players)
            sendPacket(packet, player);
    }

    public static void sendPacket(PacketContainer packet, Iterable<? extends Player> players) {
        for (Player player: players)
            sendPacket(packet, player);
    }




    public static void updateEntity(Entity entity, List<Player> players) {
        PROTOCOL_MANAGER.updateEntity(entity, players);
    }

    public static void updateEntities(Entity[] entities, List<Player> players) {
        for (Entity entity: entities)
            updateEntity(entity, players);
    }





    private static final Map<Class<?>,StructureModifier<Object>> STRUCTURE_MODIFIERS = new HashMap<>();
    private static final Unsafe UNSAFE = (Unsafe) Reflectness.getField("theUnsafe", Unsafe.class, null);

    public static AbstractStructure unsafeWrap(Class<?> type) {
        try {
            return UNSAFE == null ? null : wrap(UNSAFE.allocateInstance(type));
        } catch (Exception ex) {
            return null;
        }
    }

    public static AbstractStructure wrap(Object o) {
        return new AbstractStructure(o, STRUCTURE_MODIFIERS.computeIfAbsent(o.getClass(), clz -> new StructureModifier<>(clz, false)).withTarget(o)) {};
    }


    public static <T> void writeAll(StructureModifier<T> structureModifier, T value) {
        for (int i = 0; i < structureModifier.size(); i++)
            structureModifier.write(i, value);
    }

}

