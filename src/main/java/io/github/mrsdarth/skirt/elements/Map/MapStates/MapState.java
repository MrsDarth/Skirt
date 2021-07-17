package io.github.mrsdarth.skirt.elements.Map.MapStates;

import io.github.mrsdarth.skirt.Reflectness;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import io.github.mrsdarth.skirt.elements.Map.expressions.ExprScaleCentreId;
import org.bukkit.entity.Player;
import org.bukkit.map.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MapState {


    private static final Class<?>
            packetClass = Reflectness.nmsclass("PacketPlayOutMap"),
            mapIconClass = Reflectness.nmsclass("MapIcon"),
            iconTypeClass = Reflectness.nmsclass("MapIcon$Type"),
            chatMessageClass = Reflectness.craftclass("util.CraftChatMessage");

    private static final Field idField = packetClass.getDeclaredFields()[0];

    private static final Constructor<?> packetConstructor, mapIconConstructor = mapIconClass.getDeclaredConstructors()[0];

    static {
        Constructor<?> packetC = null, mapIconC = null;
        try {
            packetC = packetClass.getDeclaredConstructor(int.class, byte.class, boolean.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class);
            idField.setAccessible(true);
        } catch (Exception ignore) {}
        packetConstructor = packetC;
    }

    private final Object mapPacket;

    @SuppressWarnings("deprecation")
    private static byte[] imageToBytes(BufferedImage image, int x, int y) {
        int size = x * y;
        int[] rgb = new int[size];
        byte[] pixels = new byte[size];
        image.getRGB(0, 0, x, y, rgb, 0, x);
        for (int i = 0; i < size; i++) {
            pixels[i] = MapPalette.matchColor(new Color(rgb[i], true));
        } return pixels;

    }

    private static Iterable<MapCursor> fromCursorCollection(MapCursorCollection cursors) {
        int size = cursors.size();
        return () -> new Iterator<MapCursor>() {
            int i = 0;
            @Override
            public boolean hasNext() {
                return i < size;
            }
            @Override
            public MapCursor next() {
                return cursors.getCursor(i++);
            }
        };
    }

    @SuppressWarnings("deprecation")
    private static Collection<?> icons(Iterable<MapCursor> cursors) {
        return StreamSupport.stream(cursors.spliterator(), false)
                    .filter(MapCursor::isVisible)
                    .map(cursor -> {
                        try {
                            return mapIconConstructor.newInstance(
                                    iconTypeClass.getDeclaredMethod("a", byte.class).invoke(null, cursor.getRawType()),
                                    cursor.getX(), cursor.getY(), cursor.getDirection(),
                                    chatMessageClass.getDeclaredMethod("fromStringOrNull", String.class).invoke(null, cursor.getCaption())
                            );
                        } catch (Exception ex) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
    }

    public MapState(MapView mapView, BufferedImage image, int x, int y) {
        int
                width = Math.min(128 - x, image.getWidth()),
                height = Math.min(128 - y, image.getHeight());
        Object packet = null;
        try {
            packet = packetConstructor.newInstance(mapView.getId(), ExprScaleCentreId.scale(mapView), true, mapView.isLocked(), new ArrayList<>(),
                    imageToBytes(image,width,height), x, y, width, height);
        } catch (Exception ignored) {}
        this.mapPacket = packet;
    }

    public MapState(MapView mapView, Player p) {
        Object packet = null;
        try {
            Object data = mapView.getClass().getDeclaredMethod("render", p.getClass()).invoke(mapView, p);
            packet = packetConstructor.newInstance(mapView.getId(),ExprScaleCentreId.scale(mapView), true, mapView.isLocked(), icons((ArrayList<MapCursor>) Reflectness.getField("cursors", data)), Reflectness.getField("buffer", data), 0, 0, 128, 128);
        } catch (Exception ignored) {}
        this.mapPacket = packet;
    }

    public MapState(MapCanvas canvas, boolean base) {
        Object packet = null;
        MapView mapView = canvas.getMapView();
        try {
            packet = packetConstructor.newInstance(mapView.getId(), ExprScaleCentreId.scale(mapView), true, mapView.isLocked(), icons(fromCursorCollection(canvas.getCursors())), Reflectness.getField((base ? "base" : "buffer"), canvas), 0, 0, 128, 128);
        } catch (Exception ignored) {}
        this.mapPacket = packet;
    }

    public MapView getMapView() {
        try {
            return Renderer.getMap(idField.getInt(mapPacket));
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public MapState setMapView(MapView map) {
        Reflectness.setField(idField, this.mapPacket, map.getId());
        return this;
    }


    public void sendToPlayer(Player p) {
        Reflectness.sendpacket(p, mapPacket);
    }

}
