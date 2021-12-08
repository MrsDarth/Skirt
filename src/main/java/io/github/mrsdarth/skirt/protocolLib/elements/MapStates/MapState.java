package io.github.mrsdarth.skirt.protocolLib.elements.MapStates;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import io.github.mrsdarth.skirt.Reflectness;
import io.github.mrsdarth.skirt.elements.map.Maps;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

public class MapState {

    static {
        Classes.registerClass(new ClassInfo<>(MapState.class, "mapstate")
                .name("Map State")
                .user("map ?states?")
                .description("represents a state of a map that can be sent to the player to display changes on a map, useful as it is faster than drawing images often on a map")
                .since("1.2.3")
                .parser(new Parser<>() {
                    @Override
                    public @Nullable MapState parse(@NotNull String s, @NotNull ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(MapState mapState, int flags) {
                        return "map state " + mapState.getId();
                    }

                    @Override
                    public @NotNull String toVariableNameString(MapState mapState) {
                        return "mapstate:" + mapState.hashCode();
                    }

                    @Override
                    public @NotNull String getVariableNamePattern() {
                        return "mapstate:.+";
                    }
                })
        );
    }

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

    public int getId() {
        return mapPacket.getIntegers().read(0);
    }
}