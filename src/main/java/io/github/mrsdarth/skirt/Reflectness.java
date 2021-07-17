package io.github.mrsdarth.skirt;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Reflectness {

    public static Class<?> nmsclass(String name) {
        return nmsorcraft("net.minecraft.server", name);
    }

    public static Class<?> craftclass(String name) {
        return nmsorcraft("org.bukkit.craftbukkit", name);
    }

    public static Class<?> nmsorcraft(String p, String name) {
        try {
            String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName(p + "." + ver + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

//    public static void debugfields(Class<?> theclass, Object obj) {
//        try {
//            for (Field field: theclass.getDeclaredFields()) {
//                field.setAccessible(true);
//                System.out.println(field.getName() + " = " + field.get(obj) + ", " + field.getClass());
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public static Object getHandle(Object o) {
        try {
            return o.getClass().getDeclaredMethod("getHandle").invoke(o);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Object playerconnection(Player p) {
        return getField("playerConnection", nmsplayer, getHandle(p));
    }

    public static void setField(Field field, Object obj, Object arg) {
        try {
            field.setAccessible(true);
            field.set(obj, arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setField(String name, Class<?> theclass, Object obj, Object arg) {
        try {
            setField(theclass.getDeclaredField(name), obj, arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setField(String name, Object obj, Object arg) {
        setField(name, obj.getClass(), obj, arg);
    }


    public static Object getField(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Object getField(String name, Class<?> theclass, Object obj) {
        try {
            return getField(theclass.getDeclaredField(name), obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getField(String name, Object obj) {
        return getField(name, obj.getClass(), obj);
    }

    private static final Class<?> nmsentity = nmsclass("Entity");
    private static final Class<?> nmsplayer = nmsclass("EntityPlayer");
    private static final Class<?> connectionclass = nmsclass("PlayerConnection");

    private static final Class<?> packetclass = nmsclass("Packet");

    public static void refresh(Entity e, Player target) {
        try {
            Object dw = nmsentity.getDeclaredMethod("getDataWatcher").invoke(getHandle(e));
            Constructor<?> packet = Reflectness.nmsclass("PacketPlayOutEntityMetadata").getDeclaredConstructor(int.class, dw.getClass(), boolean.class);
            sendpacket(target, packet.newInstance(e.getEntityId(), dw, true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void refresh(Player p) {
        try {
            Method refresh = p.getClass().getDeclaredMethod("refreshPlayer");
            refresh.setAccessible(true);
            refresh.invoke(p);
        } catch (Exception ignored) {}
    }

    public static void move(Entity e, Location loc) {
        move(e, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public static void hide(Entity[] entities, Player[] players) {
        try {
            Class<?> hidepacket = nmsclass("PacketPlayOutEntityDestroy");
            Object packet = hidepacket.getDeclaredConstructor(int[].class).newInstance(Arrays.stream(entities).mapToInt(Entity::getEntityId).toArray());
            for (Player p : players) {
                sendpacket(p, packet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void move(Entity e, double x, double y, double z, float yaw, float pitch) {
        try {
            if (e instanceof Player) {
                Player p = (Player) e;
                Set<Object> flags = new HashSet<>(Arrays.asList(Reflectness.nmsclass("PacketPlayOutPosition$EnumPlayerTeleportFlags").getEnumConstants()));
                Method move = connectionclass.getDeclaredMethod("internalTeleport", double.class, double.class, double.class, float.class, float.class, Set.class);
                move.setAccessible(true);
                move.invoke(playerconnection(p), x, y, z, yaw, pitch, flags);
            } else {
                Method setloc = nmsentity.getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
                setloc.invoke(getHandle(e), x, y, z, yaw, pitch);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendpacket(Player p, Object packet) {
        try {
            Object pc = playerconnection(p);
            pc.getClass().getDeclaredMethod("sendPacket", packetclass).invoke(pc, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}




