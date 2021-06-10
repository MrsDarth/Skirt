package io.github.mrsdarth.skirt.elements.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;


public class Renderer extends MapRenderer {


    private static final HashMap<MapView, Consumer<MapCanvas>> firstrun = new HashMap<>();
    private static final HashMap<Event, MapCanvas> edit = new HashMap<>();

    public Renderer(MapView map, boolean remove, @Nullable Consumer<MapCanvas> run) {
        if (run != null) firstrun.put(map, run);
        if (remove) clear(map, true);
        map.addRenderer(this);
    }

    public static void clear(MapView map, boolean all) {
        for (MapRenderer renderer : map.getRenderers()) {
            if (all || renderer instanceof Renderer) map.removeRenderer(renderer);
        }
    }

    public static void edit(Event e, MapCanvas canvas) {
        edit.put(e, canvas);
    }

    public static MapCanvas getCanvas(Event e) {
        return edit.get(e);
    }


    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        if (firstrun.containsKey(mapView)) firstrun.remove(mapView).accept(mapCanvas);
    }
}
