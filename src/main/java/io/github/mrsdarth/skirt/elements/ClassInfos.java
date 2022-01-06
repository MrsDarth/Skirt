package io.github.mrsdarth.skirt.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumSerializer;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Utils;
import ch.njol.util.VectorMath;
import ch.njol.yggdrasil.Fields;
import io.github.mrsdarth.skirt.Reflectness;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.elements.map.Maps;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Statistic;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.stream.Collectors;


public class ClassInfos {

    static {

        //Bounding Box

        ClassInfo<BoundingBox> boundingBoxClassInfo = new ClassInfo<>(BoundingBox.class, "boundingbox")
                .user("bounding ?box(es)?")
                .name("Bounding Box")
                .description("Represents a bounding box between 2 points")
                .since("1.0.0")
                .parser(new SimpleParser<>() {
                    @Override
                    public @NotNull String toVariableNameString(BoundingBox box) {
                        return box.toString();
                    }
                })
                .serializer(new SimpleSerializer<>() {
                    @Override
                    protected BoundingBox deserialize(@NotNull Fields f) throws StreamCorruptedException {
                        return new BoundingBox(
                                f.getPrimitive("x1", double.class),
                                f.getPrimitive("y1", double.class),
                                f.getPrimitive("z1", double.class),
                                f.getPrimitive("x2", double.class),
                                f.getPrimitive("y2", double.class),
                                f.getPrimitive("z2", double.class)
                        );
                    }

                    @Override
                    public @NotNull Fields serialize(BoundingBox box) {
                        Fields f = new Fields();
                        f.putPrimitive("x1", box.getMinX());
                        f.putPrimitive("y1", box.getMinY());
                        f.putPrimitive("z1", box.getMinZ());
                        f.putPrimitive("x2", box.getMaxX());
                        f.putPrimitive("y2", box.getMaxY());
                        f.putPrimitive("z2", box.getMaxZ());
                        return f;
                    }
                });

        Classes.registerClass(boundingBoxClassInfo);

        if (Skirtness.isSkriptv2_6()) boundingBoxClassInfo.cloner(BoundingBox::clone);


        // RayTrace


        Classes.registerClass(new ClassInfo<>(RayTraceResult.class, "raytraceresult")
                .user("ray ?trace( ?result)?s?")
                .name("Ray Trace Result")
                .description("Represent the raytrace")
                .since("1.0.0")
                .parser(new SimpleParser<>() {
                    @Override
                    public @NotNull String toVariableNameString(RayTraceResult rayTraceResult) {
                        return rayTraceResult.toString();
                    }
                })
        );


        Classes.registerClass(new EnumClassInfo<>(FluidCollisionMode.class, "fluidcollisionmode")
                .user("fluid ?collision ?modes?")
                .name("FluidCollisionMode")
                .description("Represents the fluid collision mode in a raytrace", "always - collide with all fluids", "never - ignore fluids", "source_only - collide with source fluid blocks only")
                .since("1.0.0")
        );


        // Map


        Classes.registerClass(new ClassInfo<>(MapView.class, "map")
                .user("maps?")
                .name("Map")
                .description("represents a map view with an id")
                .since("1.2.0")
                .parser(new SimpleParser<>() {
                    @Override
                    public @NotNull String toVariableNameString(MapView map) {
                        return "map " + map.getId();
                    }
                })
                .serializer(new SimpleSerializer<>() {
                    @Override
                    protected MapView deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        int id = fields.getPrimitive("id", int.class);
                        MapView map = Maps.getMaps().get(id);
                        if (map == null)
                            throw new StreamCorruptedException("Missing Map");
                        return map;
                    }

                    @Override
                    public @NotNull Fields serialize(MapView map) {
                        Fields fields = new Fields();
                        fields.putPrimitive("id", map.getId());
                        return fields;
                    }
                })
        );

        // dumb
        String[][] plurals = (String[][]) Reflectness.getField("plurals", Utils.class, null);
        if (plurals == null)
            throw new IllegalStateException();
        plurals[15] = new String[]{"canvas", "canvases"};

        Classes.registerClass(new ClassInfo<>(MapCanvas.class, "mapcanvas")
                .user("map ?canvas(es)?")
                .name("Map Canvas")
                .description("the canvas of a map, this is what allows you to set pixels on")
                .since("1.2.0")
                .parser(new SimpleParser<>() {
                    @Override
                    public @NotNull String toVariableNameString(MapCanvas canvas) {
                        return canvas.toString() + "[map " + canvas.getMapView().getId() + "]";
                    }
                })
        );


        Classes.registerClass(new ClassInfo<>(MapCursor.class, "mapcursor")
                .user("map ?cursors?")
                .name("Map Cursor")
                .description("represents a cursor in a map")
                .since("1.2.0")
                .parser(new SimpleParser<>() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public @NotNull String toVariableNameString(MapCursor cursor) {
                        return (cursor.isVisible() ? "" : "in") + "visible " + Classes.toString(cursor.getType()) + " map cursor at (" + cursor.getX() + ", " + cursor.getY() + ") facing " + Direction.toString(VectorMath.fromYawAndPitch(VectorMath.fromSkriptYaw(22.5f * cursor.getDirection()), 0)).replaceAll(" ?(, |and )?0 meters? \\w+( ,| and)? ?", "") + (cursor.caption() == null ? "" : " named \"" + cursor.getCaption() + "\"");
                    }
                })
        );


        Classes.registerClass(new EnumClassInfo<>(MapCursor.Type.class, "mapcursortype")
                .user("map ?cursor ?types?")
                .name("Map Cursor Type")
                .description("represents the type of a map cursor")
                .since("1.2.0")
        );


        // Image


        Classes.registerClass(new ClassInfo<>(BufferedImage.class, "image")
                .user("images?")
                .name("Image")
                .description("represents an image. Can be used to send as chat message, upload skins or be displayed on maps")
                .since("1.2.0")
                .parser(new SimpleParser<>() {
                    @Override
                    public @NotNull String toVariableNameString(BufferedImage image) {
                        return "image@" + image.hashCode() + "[" + image.getWidth() + "x" + image.getHeight() + "]";
                    }
                })
        );


        // Statistic


        Classes.registerClass(new EnumClassInfo<>(Statistic.class, "statistic")
                .user("stat(istic)?s?")
                .name("Statistic")
                .description("represents a player statistic")
                .since("1.2.0")
        );

    }

    private static abstract class SimpleParser<T> extends Parser<T> {
        @Override
        public T parse(@NotNull String s, @NotNull ParseContext context) {
            return null;
        }
        @Override
        public boolean canParse(@NotNull ParseContext context) {
            return false;
        }
        @Override
        public @NotNull String toString(T o, int flags) {
            return toVariableNameString(o);
        }
        @Override
        public @NotNull String getVariableNamePattern() {
            return ".*";
        }
    }

    private static abstract class SimpleSerializer<T> extends Serializer<T> {
        @Override
        protected boolean canBeInstantiated() {
            return false;
        }
        @Override
        public void deserialize(T o, @NotNull Fields fields) {

        }
        @Override
        public boolean mustSyncDeserialization() {
            return true;
        }
        protected abstract T deserialize(@NotNull Fields fields) throws StreamCorruptedException, NotSerializableException;
    }

    private static class EnumClassInfo<T extends Enum<T>> extends ClassInfo<T> {

        public EnumClassInfo(Class<T> c, String codeName) {
            super(c, codeName);
            usage(Arrays.stream(c.getEnumConstants()).map(this::toSkript).collect(Collectors.joining(", ")));
            parser(new EnumParser(c));
            serializer(new EnumSerializer<>(c));
        }

        private String toSkript(T t) {
            return t.name().toLowerCase().replace('_', ' ');
        }

        private class EnumParser extends Parser<T> {
            private final Class<T> enumClass;
            public EnumParser(Class<T> enumClass) {
                this.enumClass = enumClass;
            }
            @Override
            public @Nullable
            T parse(@NotNull String string, @NotNull ParseContext context) {
                try {
                    return Enum.valueOf(enumClass, string.toUpperCase().replace(' ', '_'));
                } catch (Exception ex) {
                    return null;
                }
            }
            @Override
            public @NotNull String toString(T t, int flags) {
                return toSkript(t);
            }
            @Override
            public @NotNull String toVariableNameString(T t) {
                return t.name();
            }
            @Override
            public @NotNull String getVariableNamePattern() {
                return ".*";
            }
        }
    }


}


