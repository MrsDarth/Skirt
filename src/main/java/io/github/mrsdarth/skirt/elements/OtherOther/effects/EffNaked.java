package io.github.mrsdarth.skirt.elements.OtherOther.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Iterables;
import io.github.mrsdarth.skirt.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

@NoDoc

public class EffNaked extends Effect {

    static {
        if (Skript.classExists("com.destroystokyo.paper.profile.PlayerProfile")) {
            Skript.registerEffect(EffNaked.class,
                    "strip [(man|1¦girl)] %players%",
                    "make [(man|1¦girl)] %players% naked",
                    "remove clothes of [(man|1¦girl)] %players%",
                    "unstrip %players%",
                    "make %players% unnaked",
                    "put %players% clothes back on");
        }
    }

    private static final ProfileProperty
            NUDE_MAN = profile("ewogICJ0aW1lc3RhbXAiIDogMTYxNDk3MzE1NDQ3NCwKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTgyN2UzZmMxNDdhYjdjZTI2ZjkyYWViZjg2NDNmMGI1ZTdkZjE3NWUyMDIwNjQ3M2JlMDI3ZjJiMjRlOTcwMSIKICAgIH0KICB9Cn0=", "v5mwiBad+QbzueMh2R2AgSuFlZ5ZN9R8463ES7oKuwwctI0PaIAtdQgmXNiloLoh+AiVJXVLBtyc2kRTjnJ0fJtu2cIlsNXvShHWGuemkm5t7o28GzYKU9XBAaCDiaRX12izD5DQBlpgzqpnVjZ5UlRzpG/d5v/Ym/zLUQw7p/CRHgArqL39c5dT4sjct6ktrf3ga7999SkYpCcVOGroTKzSJc2d5o6l3yLLMT7BR9W79kAXil0B39FLWXrZJEMDl5pzDJBLjRDYS3bXQjKQcYmuZU8MBz6eFWT6FUV3Y8oXJQVQ2PZEjFvxe3cFZjmbZwA9DHJPWIkSYxBm6LXLNruYwrSMBvGIJOeNzmfQMmx/JMH1skg4JhhVOVv88v2G/bs5UthvWhSxmPgvXmliUT4vWTV8jXCBQmN0zbfMyQOlLR473BOkcD8vhkL8JyCaMyOzEnuTNL7N+7v8JXXniPm3nPp6a6AxHL4ejGRcv21uZfqlLPz+jSxFjcFbm8kF6Tlqp/VpJURFpv694AmfdM724gimKNa9jhI6ttkXXKxHApoqvrl3x75PapiRJ0r2bsovhcE5bEQwkJRTsfDr8N2TrC02PJgH9BTT+v/K0R0x8scPvOFW8hjneliPIkNCg05BOQchMeBmPKpYY2M4vWxEWiLDHkomnxoVw5FFIRA="),
            NUDE_GIRL = profile("eyJ0aW1lc3RhbXAiOjE1ODgwMjM0MjExMDksInByb2ZpbGVJZCI6IjJkYzc3YWU3OTQ2MzQ4MDI5NDI4MGM4NDIyNzRiNTY3IiwicHJvZmlsZU5hbWUiOiJzYWR5MDYxMCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTVhZTgxM2FmZmNlN2IwMDFmNjllNzAwMzQ0MjU0YzMyMzVhMTQ3OGI3NzIxZWM3OTk5ZWJiNjRmZjFiM2E3YyJ9fX0=", "b6EvO52dEbOEbJe4zAtheIdAHVRSFJmKSdHDwNIRu69eo2zH9E2X2W8SH8ipuUg4+77C81z4jeumgh4JKjLZ8DwE1r+DULUSu0iyY04/PYI4Ea8slB1ecAIbEk/1SdI2MmNbz1fPs5EofwwWDHlCQCkvfI2einGSEU+OIonLUGeF+1vgd8ajAKE/fJLLSU14e42IqVob+D3KMOzh05m22GeOhq3mWQhmoNe6JpeLD9AL+x9upVPM2nHW4as/je+v3iWl7D7PL/hspHLYpJqeAtrRUWU8YqvaAGJh+cdynbssW6rW1qctSI9d6NDzwlkHsShgQElX2FQtNrcYlznnRDmahRriXtN+5Ff6nlX0vu88tXlmwJ7qjN/LQoxdGEvEeMHJIVT6SpSMTADnEuP9QoVkje6sg+z2g0FoOxWRhkGTGkYHmxr3hvD5L0+4/fhAfhGtKryB9l3n7L6rV+zlzT19U/R2yylEcOrRp4ShG0BjymtkUFy1aKnXQ0R/VJ18lOq9UTzrgkwFnlk2IztIB87IuaDJb832dk2Z1T0JJ5QKOPiXgg69MNibsbSO6w97SKJEa7JTeZgq7YQBvAQLUfXhDN2PRJejpqGHbCWpV3k2bQzqOKSVQF7wZDa4ZuY8D4nWB5wljwRng/l6goE3DvCH7Qn0TYjpjK1FWNxKtG0=");

    private static final HashMap<Player, ProfileProperty>
            defaultSkins = new HashMap<>(),
            manSkins = new HashMap<>(),
            girlSkins = new HashMap<>();

    private Expression<Player> players;

    private boolean reset, isGirl;

    private static ProfileProperty profile(String value, String signature) {
        return new ProfileProperty("textures", value, signature);
    }

    private static BufferedImage getSkin(ProfileProperty profile) {
        try {
            return ImageIO.read(
                    new URL(((JSONObject) ((JSONObject) ((JSONObject) JSONValue.parse(
                            new String(Base64.getDecoder().decode(
                                    profile.getValue())))
                    ).get("textures")).get("SKIN")).get("url").toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static ProfileProperty mineSkinFromImage(BufferedImage image) {
        try {
            HttpURLConnection http = (HttpURLConnection) new URL("https://api.mineskin.org/generate/upload").openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setConnectTimeout(10000);
            http.setReadTimeout(10000);
            String boundary = Long.toHexString(System.currentTimeMillis());
            String separator = "\r\n";
            http.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream output = http.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
            writer.append("--").append(boundary).append(separator);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append("skirt_skin.png").append("\"").append(separator);
            writer.append("Content-Type: application/octet-stream").append(separator);
            writer.append(separator).flush();
            ImageIO.write(image, "png", output);
            output.flush();
            writer.append(separator).flush();
            writer.append("--").append(boundary).append("--").append(separator).flush();
            JSONObject textureJson = (JSONObject) ((JSONObject) ((JSONObject) JSONValue.parse(new InputStreamReader(http.getInputStream()))).get("data")).get("texture");
            return profile(textureJson.get("value").toString(), textureJson.get("signature").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static ProfileProperty playerN(Player p, boolean isGirl) {
        BufferedImage body = getSkin(isGirl ? NUDE_GIRL : NUDE_MAN);
        Graphics g = body.getGraphics();
        g.drawImage(getSkin(defaultSkins.get(p)).getSubimage(0,0,64,16),0,0,null);
        g.dispose();
        return mineSkinFromImage(body);
    }

    private ProfileProperty getSkin(Player p) {
        if (reset)
            return defaultSkins.get(p);
        if (isGirl)
            if (girlSkins.containsKey(p))
                return girlSkins.get(p);
            else {
                ProfileProperty skin = playerN(p, isGirl);
                girlSkins.put(p,skin);
                return skin;
            }
        else
            if (manSkins.containsKey(p))
                return manSkins.get(p);
            else {
                ProfileProperty skin = playerN(p, isGirl);
                manSkins.put(p,skin);
                return skin;
            }
    }


    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "STRIP";
    }

    @Override
    protected void execute(Event event) {
        Main plugin = Main.getInstance();
        players.stream(event).forEach(p -> {
            PlayerProfile profile = p.getPlayerProfile();
            if (!defaultSkins.containsKey(p)) defaultSkins.put(p, Iterables.getFirst(profile.getProperties(), null));
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                ProfileProperty property = getSkin(p);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    profile.setProperty(property);
                    p.setPlayerProfile(profile);
                });
            });
        });
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        players = (Expression<Player>) expressions[0];
        reset = i >= 3;
        isGirl = parseResult.mark == 1;
        return true;
    }
}
