package io.github.mrsdarth.skirt.protocolLib.elements.MapStates;

import ch.njol.skript.Skript;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import io.github.mrsdarth.skirt.Reflectness;
import io.github.mrsdarth.skirt.elements.map.Maps;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class MapState {

    private final PacketContainer mapPacket;

    private MapState(int mapId) {
        this.mapPacket = new PacketContainer(PacketType.Play.Server.MAP);
        this.mapPacket.getModifier().writeDefaults();
        this.mapPacket.getIntegers().write(0, mapId);
    }

    private void setPixelData(byte[] pixelData) {
        if (Skript.isRunningMinecraft(1, 17)) {
            StructureModifier<Object> fields = this.mapPacket.getModifier();
            Class<?> worldMap = fields.getField(fields.size() - 1).getType();
            fields.withType(worldMap).write(0, Reflectness.newInstance(worldMap.getConstructors()[0], 0, 0, 128, 128, pixelData));
        } else {
            this.mapPacket.getIntegers()
                    .write(1, 0)
                    .write(2, 0)
                    .write(3, 128)
                    .write(4, 128);
            this.mapPacket.getByteArrays().write(0, pixelData);
        }
    }

    public MapState(MapView map, BufferedImage image, int startX, int startY) {
        this(map.getId());
        byte[] pixelData = new byte[0x4000];
        Maps.imageToMapPixels(image, startX, startY, (x, y, color) -> pixelData[y * 128 + x] = color);
        setPixelData(pixelData);
    }

    public MapState(MapCanvas canvas) {
        this(canvas.getMapView().getId());
        setPixelData(Maps.getBuffer(canvas));
    }


    public PacketContainer getMapPacket() {
        return mapPacket;
    }
}

    /*
    private static final Class<?>
            packetClass = Reflectness.nmsClass("PacketPlayOutMap"),
            mapIconClass = Reflectness.nmsClass("MapIcon"),
            iconTypeClass = Reflectness.nmsClass("MapIcon$Type"),
            chatMessageClass = Reflectness.craftClass("util.CraftChatMessage");

    private static final Field idField = packetClass.getDeclaredFields()[0];

    private static final Constructor<?> packetConstructor, mapIconConstructor = mapIconClass.getDeclaredConstructors()[0];

    static {
        Constructor<?> packetC = null, mapIconC = null;
        try {
            packetC = packetClass.getDeclaredConstructor(int.class, byte.class, boolean.class, boolean.class, Collection.class, byte[].class, int.class, int.class, int.class, int.class);
            idField.setAccessible(true);
            Classes.registerClass(new ClassInfo<>(MapState.class, "mapstate")
                    .user("map ?states?")
                    .name("Map State")
                    .description("Represents a capture of a map (like taking a screenshot) that can be sent to players to display. Useful for showing multiple images on a single map, much faster than draw image on map effect")
                    .since("1.2.3")
                    .parser(new Parser<MapState>() {

                        @Nullable
                        @Override
                        public MapState parse(String s, ParseContext context) {
                            return null;
                        }

                        @Override
                        public boolean canParse(ParseContext context) {
                            return false;
                        }

                        @Override
                        public String toString(MapState mapState, int i) {
                            return mapState.toString();
                        }

                        @Override
                        public String toVariableNameString(MapState mapState) {
                            return "mapstate;" + mapState.hashCode();
                        }

                        @Override
                        public String getVariableNamePattern() {
                            return ".+";
                        }
                    }));
        } catch (Exception ignore) {}
        packetConstructor = packetC;
    }

    private final Object mapPacket;

    @SuppressWarnings("deprecation")
    public static byte[] imageToBytes(BufferedImage image, int x, int y) {
        int size = x * y;
        int[] rgb = new int[size];
        byte[] pixels = new byte[size];
        image.getRGB(0, 0, x, y, rgb, 0, x);
        for (int i = 0; i < size; i++)
            pixels[i] = MapPalette.matchColor(new Color(rgb[i], true));
        return pixels;

    }

    public static int decodeImage(int x, int y, int width) {
        return y * width + x;
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

   // public MapState(MapView mapView, BufferedImage image, int x, int y) {
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
    }*/

