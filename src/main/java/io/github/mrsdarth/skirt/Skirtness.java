package io.github.mrsdarth.skirt;

import ch.njol.skript.lang.Expression;
import ch.njol.util.Math2;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class Skirtness {

    private static final Skirt plugin = JavaPlugin.getPlugin(Skirt.class);

    private static final Server server = plugin.getServer();

    private static final World main = server.getWorlds().get(0);

    public static World getMainWorld() {
        return main;
    }

    private static final boolean
            paper       = Reflectness.classExists("com.destroystokyo.paper.PaperConfig"),
            protocolLib = Reflectness.classExists("com.comphenix.protocol.ProtocolLibrary"),
            nbt         = Reflectness.classExists("de.tr7zw.changeme.nbtapi.NBTCompound");

    public static boolean hasNBT() {
        return nbt;
    }

    public static boolean hasPaper() {
        return paper;
    }

    public static boolean hasProtocolLib() {
        return protocolLib;
    }

    private static final Executor mainThreadExecutor = server.getScheduler().getMainThreadExecutor(plugin);

    public static Executor getMainThreadExecutor() {
        return mainThreadExecutor;
    }

    public static void runSync(Runnable runnable) {
        mainThreadExecutor.execute(runnable);
    }

    private static final Listener listener = new Listener() {};

    @SuppressWarnings("unchecked")
    public static <T extends Event> void registerEvent(Class<T> eventClass, Consumer<T> eventHandler) {
        server.getPluginManager().registerEvent(eventClass, listener, EventPriority.NORMAL, (l, event) -> eventHandler.accept((T) event), plugin);
    }

    public static <T> T enumAt(Class<T> enumClass, int ordinal) {
        T[] enumConstants = enumClass.getEnumConstants();
        return enumConstants[Math2.mod(ordinal, enumConstants.length)];
    }

    public static <T> Optional<T> getSingle(Expression<T> expr, Event e) {
        return Optional.ofNullable(expr.getSingle(e));
    }

}
