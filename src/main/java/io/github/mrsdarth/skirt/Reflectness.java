package io.github.mrsdarth.skirt;


import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Reflectness {

    public static String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> nmsclass(String name) {
        return nmsorcraft("net.minecraft.server", name);
    }

    public static Class<?> craftclass(String name) {
        return nmsorcraft("org.bukkit.craftbukkit", name);
    }

    public static Class<?> nmsorcraft(String p, String name) {
        try {
            return Class.forName(String.join(".", (new String[] {p, ver, name})));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void debugfields(Class<?> theclass, Object obj) {
        try {
            for (Field field: theclass.getDeclaredFields()) {
                field.setAccessible(true);
                System.out.println(field.getName() + " = " + field.get(obj) + ", " + field.getClass());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void setfield(String name, Class<?> theclass, Object obj, Object arg) {
        try {
            Field field = theclass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, arg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Object getfield(String name, Class<?> theclass, Object obj) {
        try {
            Field field = theclass.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Class<?> craftplayer = craftclass("entity.CraftPlayer");
    private static Class<?> craftentity = craftclass("entity.CraftEntity");

    private static Class<?> nmsentity = nmsclass("Entity");
    private static Class<?> packetclass = nmsclass("Packet");

    public static void refresh(Entity e, Player target) {
        try {
            Object nmse = craftentity.getDeclaredMethod("getHandle").invoke(craftentity.cast(e));
            Object dw = nmsentity.getDeclaredMethod("getDataWatcher").invoke(nmse);
            Constructor pacc = Reflectness.nmsclass("PacketPlayOutEntityMetadata").getDeclaredConstructor(int.class, dw.getClass(), boolean.class);
            sendpacket(target, pacc.newInstance(e.getEntityId(), dw, true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendpacket(Player p, Object packet) {
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


}




