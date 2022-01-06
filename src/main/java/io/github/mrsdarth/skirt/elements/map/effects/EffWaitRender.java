package io.github.mrsdarth.skirt.elements.map.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.elements.map.SkirtRenderer;
import io.github.mrsdarth.skirt.skriptv2_6.sections.SecMapEdit;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Wait for render")
@Description({
        "waits until the specified canvas is rendered again. Renders happen almost every tick.",
        "Useful when you want every action to be seen."})
@Examples({"loop 100 times:",
        "\tadd 1 to cursor rotation of {_cursor}",
        "\twait for {_canvas} to render"})
@Since("1.2.0")

public class EffWaitRender extends AsyncEffect {

    static {
        Skript.registerEffect(EffWaitRender.class,
                "wait for [next] render",
                "wait for %mapcanv% to render");
    }

    private Expression<MapCanvas> canvasExpr;

    private SecMapEdit mapEditSection;

    @Override
    protected void execute(@NotNull Event e) {
        MapCanvas canvas = canvasExpr == null ? mapEditSection.getCanvas() : canvasExpr.getSingle(e);
        if (canvas != null) SkirtRenderer.getRenderer(canvas).getCanvas().join();
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "wait for " + (canvasExpr == null ? "next render" : canvasExpr.toString(e, debug) + " to render");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        if (matchedPattern == 0) {
            if (!Skirtness.isSkriptv2_6())
                mapEditSection = getParser().getCurrentSection(SecMapEdit.class);
            if (mapEditSection == null) {
                Skript.error("You need to specify a map canvas when outside a map edit section");
                return false;
            }
            return true;
        }
        canvasExpr = (Expression<MapCanvas>) exprs[0];
        return true;
    }
}
