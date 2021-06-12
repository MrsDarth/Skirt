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
import io.github.mrsdarth.skirt.elements.Util.VariableUtils;
import org.bukkit.event.Event;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Map Edit")
@Description({
        "start editing a map. the %objects% at the end is to store the map canvas to use for editing.",
        "partially edit prevents deleting the orginal map renderers",
        "this effect is a delayed effect and will wait until the specified map begins to render",
        "map begins rendering when it is visible to a player eg. in their inventory or in an item frame"
})
@Examples("edit map from id 0 and store in {canvas}")
@Since("1.2.0")

public class EffMapEdit extends Effect {

    private Renderer.Mode mode;
    private Variable<?> var;
    private Expression<MapView> map;

    static {
        Skript.registerEffect(EffMapEdit.class, "[(1¦partially|2¦raw)] (manage|edit) [map] %map% [and store (canvas|it|)] in %objects%");
    }

    @Override
    protected void execute(Event event) {
    }

    @Nullable
    @Override
    protected TriggerItem walk(Event event) {
        debug(event, true);
        MapView mapView = map.getSingle(event);
        TriggerItem next = getNext();
        Delay.addDelayedEvent(event);
        if (mapView == null) return next;
        Object localvars = Variables.removeLocals(event);
        String varname = var.getName().toString(event);
        boolean local = var.isLocal();
        new Renderer(mapView, mode, canvas -> {
            VariableUtils.pasteVariables(event, localvars);
            Variables.setVariable(varname, canvas, event, local);
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
        return "manage map";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (exprs[1] instanceof Variable) {
            var = (Variable<?>) exprs[1];
            if (!var.isList()) {
                map = (Expression<MapView>) exprs[0];
                switch (parseResult.mark) {
                    case 1:
                        mode = Renderer.Mode.PARTIAL;
                        break;
                    case 2:
                        mode = Renderer.Mode.RAW;
                        break;
                    default:
                        mode = Renderer.Mode.EDIT;
                }
                return true;
            }
        }
        Skript.error("You need to store the canvas in a single variable");
        return false;
    }
}
