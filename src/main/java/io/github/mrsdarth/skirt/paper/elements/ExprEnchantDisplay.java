package io.github.mrsdarth.skirt.paper.elements;

import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.EnchantmentType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.mrsdarth.skirt.Reflectness;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.*;

@Name("Enchant display")
@Description("returns an enchantment type as a formatted string example sharpness 7 returns §7Sharpness VII")
@Examples("send enchant display of enchantments of player's tool")
@Since("2.0.0")
@RequiredPlugins("Paper")

public class ExprEnchantDisplay extends SimplePropertyExpression<EnchantmentType, String> {

    private static TranslatableComponentRenderer<Locale> RENDERER;

    static {
        try {
            Module module = Bukkit.class.getModule();
            TranslationRegistry registry = TranslationRegistry.create(Key.key("skirt", "mojang-translations"));
            registry.registerAll(Locale.US, ResourceBundle.getBundle("mojang-translations/en_US", module), false);
            RENDERER = TranslatableComponentRenderer.usingTranslationSource(new ChainingTranslator(registry) {
                private final JsonObject json = JsonParser.parseReader(new InputStreamReader(module.getResourceAsStream("assets/minecraft/lang/en_us.json"))).getAsJsonObject();
                @Override
                public @Nullable MessageFormat trans(@NotNull String key, @NotNull Locale locale) {
                    return json.has(key) ? new MessageFormat(json.get(key).getAsString(), locale) : null;
                }
            });
            register(ExprEnchantDisplay.class, String.class, "[enchant[ment]] display", "enchantmenttypes");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "[enchant[ment]] display";
    }

    @Nullable
    @Override
    public String convert(EnchantmentType enchantmentType) {
        Enchantment type = enchantmentType.getType();
        return (type == null) ? null : LegacyComponentSerializer.legacySection().serialize(RENDERER.render(type.displayName(enchantmentType.getLevel()), Locale.US));
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    private static abstract class ChainingTranslator implements Translator {
        private final Translator chain;
        public ChainingTranslator(Translator chain) {
            this.chain = chain;
        }
        @Override
        public @NotNull Key name() {
            return Key.key("skirt");
        }
        @Override
        public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
            MessageFormat format = trans(key, locale);
            return format == null ? chain.translate(key, locale) : format;
        }
        protected abstract MessageFormat trans(@NotNull String key, @NotNull Locale locale);
    }

}
