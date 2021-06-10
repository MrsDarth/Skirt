package io.github.mrsdarth.skirt.elements.Map.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import io.github.mrsdarth.skirt.elements.Util.EffectSection;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.jetbrains.annotations.Nullable;

@Name("Edit Canvas")
@Description("starts editing a mapcanvas. This allows you to set pixels / edit without having to specify the canvas every time.")
@Examples({
        "edit map canvas {_canvas}:",
        "\tset all pixels to red",
        "\tdraw image {_image} at 0, 0"})
@Since("1.2.0")

public class SecEditCanvas extends EffectSection {

    static {
        Skript.registerCondition(SecEditCanvas.class,
                "edit [map] [canvas] %mapcanv%");
    }

    private Expression<MapCanvas> canvas;
    private boolean remove;

    @Override
    protected void execute(Event e) {
        MapCanvas c = canvas.getSingle(e);
        if (c != null) {
            Renderer.edit(e, c);
            runSection(e);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "edit map";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!hasSection() || checkIfCondition()) return false;
        canvas = (Expression<MapCanvas>) exprs[0];
        loadSection(true);
        return true;
    }
}
