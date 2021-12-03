package io.github.mrsdarth.skirt.protocolLib.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.github.mrsdarth.skirt.Skirt;
import io.github.mrsdarth.skirt.protocolLib.PLib;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class EffPauseChat extends Effect {

    private static final Set<Player> PAUSED_PLAYERS = Collections.synchronizedSet(new HashSet<>());

    static {

        PLib.addListener(new PacketAdapter(JavaPlugin.getPlugin(Skirt.class), PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                event.setCancelled(PAUSED_PLAYERS.contains(event.getPlayer()));
            }
        });
        Skript.registerEffect(EffPauseChat.class, "(pause|1¦resume) chat (of|for) %players%");
    }

    private Expression<Player> playerExpr;

    private boolean pause;

    @Override
    protected void execute(@NotNull Event e) {
        Player[] players = playerExpr.getArray(e);
        if (pause)
            Collections.addAll(PAUSED_PLAYERS, players);
        else
            Arrays.stream(players).forEach(PAUSED_PLAYERS::remove);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (pause ? "pause" : "resume") + " chat for " + playerExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        playerExpr = (Expression<Player>) exprs[0];
        pause = parseResult.mark == 0;
        return true;
    }
}