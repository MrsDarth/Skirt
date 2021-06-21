package io.github.mrsdarth.skirt.elements.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;


public class Renderer extends MapRenderer {

    public enum Mode {
        EDIT,
        PARTIAL,
        RAW
    }


    private static final HashMap<MapView, Consumer<MapCanvas>> firstrun = new HashMap<>();
    private static final HashMap<MapCanvas, Runnable> call = new HashMap<>();
    private static final HashMap<Event, MapCanvas> edit = new HashMap<>();
    private static final HashMap<Renderer, MapRenderer> copy = new HashMap<>();


    public Renderer(MapView map, Mode mode, @Nullable Consumer<MapCanvas> run) {
        if (mode == Mode.RAW && !map.isVirtual()) copy.put(this, map.getRenderers().get(0));
        if (run != null) firstrun.put(map, run);
        if (mode != Mode.PARTIAL) clear(map, true);
        map.addRenderer(this);
        for (Player p: Bukkit.getOnlinePlayers()) {
            p.sendMap(map);
        }
    }

    public static void onRender(MapCanvas c, Runnable run) {
        call.put(c, run);
    }

    public static void clear(MapView map, boolean all) {
        for (MapRenderer renderer : map.getRenderers()) {
            if (all || renderer instanceof Renderer) {
                map.removeRenderer(renderer);
                if (renderer instanceof Renderer && copy.containsKey(renderer)) map.addRenderer(copy.get(renderer));
            }
        }
    }

    public static void edit(Event e, MapCanvas canvas) {
        edit.put(e, canvas);
    }

    public static MapCanvas getCanvas(Event e) {
        return edit.get(e);
    }


    @Override
    @SuppressWarnings("deprecation")
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        if (firstrun.containsKey(mapView)) firstrun.remove(mapView).accept(mapCanvas);
        if (call.containsKey(mapCanvas)) call.remove(mapCanvas).run();
        if (copy.containsKey(this)) {
            copy.get(this).render(mapView, mapCanvas, player);
            MapCursorCollection cursorCollection = mapCanvas.getCursors();
            int size = cursorCollection.size();
            for (int i = 0; i < size; i++) {
                MapCursor cursor = cursorCollection.getCursor(i);
                String name = cursor.getCaption();
                if (name != null && name.isEmpty()) cursor.setCaption(null);
            }
        }
    }
}
