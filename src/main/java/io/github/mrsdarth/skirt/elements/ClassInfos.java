package io.github.mrsdarth.skirt.elements;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Direction;
import ch.njol.util.StringUtils;
import ch.njol.util.VectorMath;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import io.github.mrsdarth.skirt.elements.Map.MapStates.MapState;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Statistic;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.Arrays;


public class ClassInfos {
    static {
        Classes.registerClass(new ClassInfo<>(RayTraceResult.class, "raytraceresult")
                .user("ray ?trace( ?result)?s?")
                .name("Ray Trace Result")
                .description("Represent the raytrace")
                .since("1.0.0")
                .parser(new Parser<RayTraceResult>() {

                    @Override
                    @Nullable
                    public RayTraceResult parse(String input, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(RayTraceResult ray, int i) {
                        return ray.toString();
                    }

                    @Override
                    public String toVariableNameString(RayTraceResult ray) {
                        return ("raytrace;" + ray.hashCode());
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "raytrace;-?\\d+";
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(BoundingBox.class, "boundingbox")
                .user("bounding ?box(es)?")
                .name("Bounding Box")
                .description("Represents a bounding box between 2 points")
                .since("1.0.0")
                .parser(new Parser<BoundingBox>() {

                    @Override
                    @Nullable
                    public BoundingBox parse(String input, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(BoundingBox box, int i) {
                        return box.toString();
                    }

                    @Override
                    public String toVariableNameString(BoundingBox box) {
                        return ("box;" + box.toString());
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "box;(.*)";
                    }
                }).serializer(new Serializer<BoundingBox>() {
                    @Override
                    public Fields serialize(BoundingBox b) {
                        Fields f = new Fields();
                        f.putPrimitive("x1", b.getMinX());
                        f.putPrimitive("y1", b.getMinY());
                        f.putPrimitive("z1", b.getMinZ());
                        f.putPrimitive("x2", b.getMaxX());
                        f.putPrimitive("y2", b.getMaxY());
                        f.putPrimitive("z2", b.getMaxZ());
                        return f;
                    }

                    @Override
                    public void deserialize(BoundingBox b, Fields fields) {
                        assert false;
                    }

                    @Override
                    protected BoundingBox deserialize(Fields f) throws StreamCorruptedException {
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
                    public boolean mustSyncDeserialization() {
                        return false;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                })
        );

        Classes.registerClass(new ClassInfo<>(FluidCollisionMode.class, "fluidcollisionmode")
                .user("fluidcollisionmodes?")
                .name("FluidCollisionMode")
                .description("Represents the fluid collision mode in a raytrace",
                        "always - collide with all fluids",
                        "never - ignore fluids",
                        "source_only - collide with source fluid blocks only")
                .usage("always, never, source_only")
                .since("1.0.0")
                .parser(new Parser<FluidCollisionMode>() {

                    @Nullable
                    @Override
                    public FluidCollisionMode parse(String s, ParseContext context) {
                        try {
                            return FluidCollisionMode.valueOf(s.toUpperCase());
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return true;
                    }

                    @Override
                    public String toString(FluidCollisionMode f, int flags) {
                        return f.toString();
                    }

                    @Override
                    public String toVariableNameString(FluidCollisionMode f) {
                        return ("fluidcollisionmode;" + f.toString());
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "fluidcollisionmode;(always|never|source_only)";
                    }
                }));
        Classes.registerClass(new ClassInfo<>(MapView.class, "map")
                .user("maps?")
                .name("Map")
                .description("represents a map view")
                .since("1.2.0")
                .parser(new Parser<MapView>() {
                    @Override
                    public String toString(MapView mapView, int i) {
                        return mapView.toString();
                    }

                    @Override
                    public String toVariableNameString(MapView mapView) {
                        return null;
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return null;
                    }
                }).parser(new Parser<MapView>() {
                    @Override
                    @Nullable
                    public MapView parse(String input, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }


                    @Override
                    public String toString(MapView mapView, int i) {
                        return toVariableNameString(mapView);
                    }

                    @Override
                    public String toVariableNameString(MapView mapView) {
                        return "map;" + mapView.getId();
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "map;\\d+";
                    }
                }).changer(new Changer<MapView>() {
                    @Nullable
                    @Override
                    public Class<?>[] acceptChange(ChangeMode changeMode) {
                        return changeMode == ChangeMode.DELETE ? CollectionUtils.array() : null;
                    }

                    @Override
                    public void change(MapView[] mapViews, @Nullable Object[] objects, ChangeMode changeMode) {
                        Arrays.stream(mapViews).forEach(Renderer::clear);
                    }
                }).serializer(new Serializer<MapView>() {
                    @Override
                    public Fields serialize(MapView mapView) throws NotSerializableException {
                        Fields f = new Fields();
                        f.putPrimitive("id", mapView.getId());
                        return f;
                    }

                    @Override
                    protected MapView deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
                        return Renderer.getMap(fields.getPrimitive("id",int.class));
                    }

                    @Override
                    public void deserialize(MapView mapView, Fields fields) throws StreamCorruptedException, NotSerializableException {
                        assert false;
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return false;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                })

        );
        Classes.registerClass(new ClassInfo<>(BufferedImage.class, "image")
                .user("images?")
                .name("Image")
                .description("represents an image. Can be displayed on maps")
                .since("1.2.0")
                .parser(new Parser<BufferedImage>() {

                    @Nullable
                    @Override
                    public BufferedImage parse(String s, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(BufferedImage image, int i) {
                        return image.toString();
                    }

                    @Override
                    public String toVariableNameString(BufferedImage image) {
                        return "image;" + image.hashCode();
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "image;\\d+";
                    }
                }));
        Classes.registerClass(new ClassInfo<>(MapCursor.class, "mapcursor")
                .user("map ?cursors?")
                .name("Map Cursor")
                .description("represents a cursor in a map")
                .since("1.2.0")
                .parser(new Parser<MapCursor>() {

                    @Nullable
                    @Override
                    public MapCursor parse(String s, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    @SuppressWarnings("deprecation")
                    public String toString(MapCursor cursor, int i) {
                        return (cursor.isVisible() ? "" : "in") + "visible " +
                                toskript(cursor.getType().name()) +
                                " map cursor at " + cursor.getX() + ", " + cursor.getY() +
                                " facing " + Direction.toString(VectorMath.fromYawAndPitch(VectorMath.fromSkriptYaw(22.5f * cursor.getDirection()), 0)).replaceAll(" ?(, |and )?0 meters? \\w+( ,| and)? ?", "") +
                                (cursor.caption() != null ? " named \"" + cursor.getCaption() + "\"" : "");
                    }

                    @Override
                    public String toVariableNameString(MapCursor mapCursor) {
                        return "cursor;" + mapCursor.hashCode();
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "cursor;\\d+";
                    }
                }));
        Classes.registerClass(new ClassInfo<>(MapCursor.Type.class, "mapcursortype")
                .user("map ?cursor ?types?")
                .name("Map Cursor Type")
                .description("represents the type of a map cursor")
                .since("1.2.0")
                .usage(toskript(StringUtils.join(MapCursor.Type.values(), ", ")))
                .parser(new Parser<MapCursor.Type>() {

                    @Nullable
                    @Override
                    public MapCursor.Type parse(String s, ParseContext context) {
                        try {
                            return MapCursor.Type.valueOf(fromskript(s));
                        } catch (Exception ex) {
                            return null;
                        }
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return true;
                    }

                    @Override
                    public String toString(MapCursor.Type type, int i) {
                        return toVariableNameString(type);
                    }

                    @Override
                    public String toVariableNameString(MapCursor.Type type) {
                        return toskript(type.name());
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return ".+";
                    }
                }));
        Classes.registerClass(new ClassInfo<>(MapCanvas.class, "mapcanv")
                .user("map ?canvs?")
                .name("Map Canvas")
                .description("the canvas of a map, this is what allows you to set pixels on")
                .since("1.2.0")
                .parser(new Parser<MapCanvas>() {

                    @Nullable
                    @Override
                    public MapCanvas parse(String s, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(MapCanvas mapCanvas, int i) {
                        return mapCanvas.toString();
                    }

                    @Override
                    public String toVariableNameString(MapCanvas mapCanvas) {
                        return "mapcanvas;" + mapCanvas.hashCode();
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "mapcanvas;\\d+";
                    }
                }));
        Classes.registerClass(new ClassInfo<>(Statistic.class, "statistic")
                .user("stat(istic)?s?")
                .name("Statistic")
                .description("represents a player statistic")
                .since("1.2.0")
                .usage(toskript(StringUtils.join(Statistic.values(), ", ")))
                .parser(new Parser<Statistic>() {

                    @Nullable
                    @Override
                    public Statistic parse(String s, ParseContext context) {
                        try {
                            return Statistic.valueOf(fromskript(s));
                        } catch (Exception ex) {
                            return null;
                        }
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return true;
                    }

                    @Override
                    public String toString(Statistic statistic, int i) {
                        return toVariableNameString(statistic);
                    }

                    @Override
                    public String toVariableNameString(Statistic statistic) {
                        return toskript(statistic.toString());
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return ".+";
                    }
                }));
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
    }

    private static String toskript(String s) {
        return s.replace("_", " ").toLowerCase();
    }

    private static String fromskript(String s) {
        return s.replace(" ", "_").toUpperCase();
    }

}


