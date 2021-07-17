package io.github.mrsdarth.skirt.elements.Map.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.elements.Map.Renderer;
import io.github.mrsdarth.skirt.elements.Util.EffectSection;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

@Name("Map Edit")
@Description({
        "start editing a map. the %objects% at the end is to store the map canvas to use for editing.",
        "partially edit prevents deleting the orginal map renderers",
        "this effect is a delayed effect and will wait until the specified map begins to render",
        "map begins rendering when it is visible to a player eg. in their inventory or in an item frame"
})
@Examples({
        "edit new map from world:",
        "\tset pixel at 1, 1 to green"
})
@Since("1.2.0")

public class SecMapEdit extends EffectSection {

    public static MapCanvas canvas = null;

    private void setCanvas(MapCanvas mapCanvas) { canvas = mapCanvas; }

    private Renderer.Mode mode;
    private Variable<?> var;
    private Expression<MapView> map;
    private boolean hasSection;

    static {
        Skript.registerCondition(SecMapEdit.class, "[(1¦partially|2¦raw)] (manage|edit) [map] %map% [[and store (canvas|it|)] in %-objects%]");
    }

    public static MapCanvas getCanvas(Event e, @Nullable Expression<MapCanvas> canvas) {
        return (canvas != null) ? canvas.getSingle(e) : Renderer.getCanvas(e);
    }

    private TriggerItem doRender(Event event, @Nullable TriggerItem next) {
        MapView mapView = map.getSingle(event);
        if (mapView == null) return next;
        Object localvars = !hasSection ? Variables.removeLocals(event) : null;
        boolean store = var != null,
                local = store && var.isLocal();
        String varname = store ? var.getName().toString(event) : null;
        new Renderer(mapView, mode, canvas -> {
            if (localvars != null && !hasSection) Variables.setLocalVariables(event, localvars);
            if (store) Variables.setVariable(varname, canvas, event, local);
            setCanvas(canvas);
            if (hasSection) {
                Renderer.edit(event,canvas);
                runSection(event);
            }
            else if (next != null) TriggerItem.walk(next, event);
        });
        return null;
    }

    @Override
    protected void execute(Event event) {
        if (!hasSection) return;
        doRender(event,null);

    }

    @Nullable
    @Override
    protected TriggerItem walk(Event event) {
        TriggerItem next = getNext();
        if (hasSection) return next;
        debug(event,true);
        Delay.addDelayedEvent(event);
        return doRender(event,next);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "manage map";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (exprs[1] instanceof Variable && !((Variable<?>) exprs[1]).isList())
            var = (Variable<?>) exprs[1];
        else if (exprs[1] != null) {
            Skript.error("You need to store the canvas in a single variable");
            return false;
        }
        map = (Expression<MapView>) exprs[0];
        mode = Renderer.Mode.values()[parseResult.mark];
        hasSection = hasSection();
        if (hasSection) loadSection(false);
        return true;
    }
}
