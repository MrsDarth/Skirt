package io.github.mrsdarth.skirt.elements.Map.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import io.github.mrsdarth.skirt.elements.Map.sections.SecEditCanvas;
import io.github.mrsdarth.skirt.elements.Util.EffectSection;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.jetbrains.annotations.Nullable;

@Name("Map Canvas")
@Description("returns the canvas in a canvas edit section. Quite a useless expression since you do not need to specify canvases in there")
@Examples({"edit canvas {_canvas}:",
        "\tsend \"%map canvas%\""})
@Since("1.2.0")

public class ExprMapCanvas extends SimpleExpression<MapCanvas> {

    static {
        Skript.registerExpression(ExprMapCanvas.class, MapCanvas.class, ExpressionType.SIMPLE,
                "[the] map canvas");
    }

    @Nullable
    @Override
    protected MapCanvas[] get(Event event) {
        return CollectionUtils.array(getCanvas(event, null));
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MapCanvas> getReturnType() {
        return MapCanvas.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "map canvas";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (EffectSection.isCurrentSection(SecEditCanvas.class)) {
            Skript.error("Cannot use map canvas expression outside an edit section");
            return false;
        }
        return true;
    }

    public static MapCanvas getCanvas(Event e, @Nullable Expression<MapCanvas> canvas) {
        return (canvas != null) ? canvas.getSingle(e) : Renderer.getCanvas(e);
    }

}
