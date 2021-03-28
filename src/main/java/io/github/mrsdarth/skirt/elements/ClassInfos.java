package io.github.mrsdarth.skirt.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;

import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.FluidCollisionMode;

import org.jetbrains.annotations.Nullable;


public class ClassInfos {
    static {
        Classes.registerClass(new ClassInfo<>(RayTraceResult.class, "raytraceresult")
                .user("raytrace(( )?result)?s?")
                .name("raytrace result")
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
                }));

        Classes.registerClass(new ClassInfo<>(BoundingBox.class, "boundingbox")
                .user("bounding( )?box(es)?")
                .name("BoundingBox")
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
                }));

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


    }
}


