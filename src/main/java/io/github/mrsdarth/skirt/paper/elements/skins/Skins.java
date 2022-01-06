package io.github.mrsdarth.skirt.paper.elements.skins;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import io.github.mrsdarth.skirt.HttpUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

public class Skins {

    static {
        Classes.registerClass(new ClassInfo<>(ProfileProperty.class, "skin")
                .name("Skin")
                .user("skins?")
                .description("represents the skin of a player, skull block or skull item")
                .since("2.0.0")
                .parser(new Parser<>() {
                    @Override
                    public @NotNull String toString(ProfileProperty profile, int flags) {
                        return toVariableNameString(profile);
                    }

                    @Override
                    public @NotNull String toVariableNameString(ProfileProperty profile) {
                        return "{Value:" + profile.getValue() + (profile.isSigned() ? ",Signature:" + profile.getSignature() : "") + "}";
                    }

                    @Override
                    public @NotNull String getVariableNamePattern() {
                        return ".+";
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @Nullable ProfileProperty parse(@NotNull String s, @NotNull ParseContext context) {
                        return null;
                    }
                })
        );
    }


    private static final Map<UUID, ProfileProperty> SKINS = new HashMap<>();
    private static final String MINESKIN_GENERATE = "https://api.mineskin.org/generate/%s";

    private static ProfileProperty fromJson(JsonObject json) {
        return new ProfileProperty("textures", json.get("value").getAsString(), json.get("signature").getAsString());
    }

    private static ProfileProperty mineSkinRequest(HttpRequest request) {
        return HttpUtils.send(request, HttpResponse.BodyHandlers.ofString())
                .map(HttpResponse::body)
                .map(HttpUtils::parseJson)
                .map(JsonElement::getAsJsonObject)
                .map(json -> json.getAsJsonObject("data").getAsJsonObject("texture"))
                .map(Skins::fromJson)
                .orElse(null);
    }


    private static ProfileProperty getSkin(Consumer<OutputStream> writeImageData) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            PrintWriter out = new PrintWriter(byteStream, true);
            out.println();
            out.println("--xxx");
            out.println("Content-Disposition: form-data; name=\"file\"; filename=\"skirt69.png\"");
            out.println("Content-Type: image/png");
            out.println();
            writeImageData.accept(byteStream);
            out.println();
            out.println("--xxx--");
            out.close();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(MINESKIN_GENERATE.formatted("upload")))
                    .timeout(Duration.ofSeconds(5))
                    .setHeader("Content-Type", "multipart/form-data;boundary=xxx")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(byteStream.toByteArray()))
                    .build();
            return mineSkinRequest(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }



    public static void setSkin(Player player, ProfileProperty skin) {
        if (hasValidSignature(skin)) {
            getSkin(player);
            PlayerProfile profile = player.getPlayerProfile();
            profile.setProperty(skin);
            player.setPlayerProfile(profile);
        }
    }

    public static ProfileProperty getSkin(PlayerProfile playerProfile) {
        return playerProfile == null ? null : playerProfile.getProperties().iterator().next();
    }

    public static ProfileProperty getSkin(OfflinePlayer player) {
        return SKINS.computeIfAbsent(player.getUniqueId(), uuid -> {
            Player onlinePlayer = player.getPlayer();
            PlayerProfile profile;
            if (onlinePlayer == null) {
                profile = Bukkit.createProfile(player.getUniqueId(), player.getName());
                if (!profile.complete()) return null;
            } else
                profile = onlinePlayer.getPlayerProfile();
            return getSkin(profile);
        });
    }

    public static ProfileProperty getSkin(String imageURL) {
        try {
            JsonObject content = new JsonObject();
            content.addProperty("url", imageURL);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(MINESKIN_GENERATE.formatted("url")))
                    .timeout(Duration.ofSeconds(5))
                    .setHeader("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                    .build();
            return mineSkinRequest(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ProfileProperty getSkin(BufferedImage image) {
        return getSkin(outputStream -> {
            try {
                ImageIO.write(image, "png", outputStream);
            } catch (IOException ignored) {}
        });
    }

    public static ProfileProperty getSkin(File imageFile) {
        return getSkin(outputStream -> {
            try {
                Files.copy(imageFile.toPath(), outputStream);
            } catch (IOException ignored) {}
        });
    }














    public static boolean hasValidSignature(ProfileProperty profile) {
        try {
            Signature verifier = Signature.getInstance("SHA1withRSA");
            verifier.initVerify(KEY);
            verifier.update(profile.getValue().getBytes());
            return profile.isSigned() && verifier.verify(Base64.getDecoder().decode(profile.getSignature()));
        } catch (Exception ignored) {}
        return false;
    }

    private static final PublicKey KEY = Optional.ofNullable(YggdrasilMinecraftSessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der"))
            .map(sessionKey -> {
                try {
                    return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(sessionKey.readAllBytes()));
                } catch (Exception ex) {
                    return null;
                }
            }).orElse(null);

}
