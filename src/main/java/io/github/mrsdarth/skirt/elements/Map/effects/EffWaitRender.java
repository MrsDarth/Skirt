package io.github.mrsdarth.skirt.elements.Map.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.*;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import io.github.mrsdarth.skirt.elements.Map.expressions.ExprMapCanvas;
import io.github.mrsdarth.skirt.elements.Map.sections.SecEditCanvas;
import io.github.mrsdarth.skirt.elements.Util.EffectSection;
import io.github.mrsdarth.skirt.elements.Util.VariableUtils;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.jetbrains.annotations.Nullable;

@Name("Wait for render")
@Description({
        "waits until the specified canvas is rendered again. Renders happen almost every tick.",
        "Useful when you want every action to be seen."})
@Examples({
        "loop 100 times:",
        "\tadd 1 to cursor rotation of {_cursor}",
        "\twait for {_canvas} to render"
})
@Since("1.2.0")

public class EffWaitRender extends Effect {

    static {
        Skript.registerEffect(EffWaitRender.class,
                "wait for [next] render [of %-mapcanv%]",
                "wait for %mapcanv% to render");
    }

    private Expression<MapCanvas> mapCanvas;

    @Override
    protected void execute(Event event) {
    }

    @Nullable
    @Override
    protected TriggerItem walk(Event event) {
        debug(event, true);
        MapCanvas canvas = ExprMapCanvas.getCanvas(event, mapCanvas);
        TriggerItem next = getNext();
        Delay.addDelayedEvent(event);
        if (canvas == null) return next;
        Object localvars = Variables.removeLocals(event);
        Renderer.onRender(canvas, () -> {
            VariableUtils.pasteVariables(event, localvars);
            continueWalk(next, event);
        });
        return null;
    }

    private void continueWalk(@Nullable TriggerItem next, Event event) {
        Object timing = null;
        if (next != null) {
            if (SkriptTimings.enabled()) {
                Trigger trigger = getTrigger();
                if (trigger != null) {
                    timing = SkriptTimings.start(trigger.getDebugLabel());
                }
            }
            TriggerItem.walk(next, event);
        }
        Variables.removeLocals(event);
        SkriptTimings.stop(timing);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "wait for next render";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!EffectSection.isCurrentSection(SecEditCanvas.class) && exprs[0] == null) {
            Skript.error("You need to specify a map canvas when outside a canvas edit section");
            return false;
        }
        mapCanvas = (Expression<MapCanvas>) exprs[0];
        return true;
    }
}
