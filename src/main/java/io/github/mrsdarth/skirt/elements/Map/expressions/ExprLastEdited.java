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
import io.github.mrsdarth.skirt.elements.Map.sections.SecMapEdit;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.jetbrains.annotations.Nullable;

@Name("Last Edited Canvas")
@Description("returns the last edited map after or within a map edit section")
@Examples({
        "edit map 0",
        "set all pixels on last edited canvas to black"
})
@Since("1.2.3")

public class ExprLastEdited extends SimpleExpression<MapCanvas> {

    static {
        Skript.registerExpression(ExprLastEdited.class, MapCanvas.class, ExpressionType.SIMPLE,
                "[the] [last edited] [map] canvas");
    }

    @Nullable
    @Override
    protected MapCanvas[] get(Event event) {
        MapCanvas c = SecMapEdit.getCanvas(event, null);
        return CollectionUtils.array(c != null ? c : SecMapEdit.canvas);
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
        return "last edited map canvas";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
