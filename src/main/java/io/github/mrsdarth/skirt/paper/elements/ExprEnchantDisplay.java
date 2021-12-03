package io.github.mrsdarth.skirt.paper.elements;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.EnchantmentType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.key.Key;
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
import java.util.Locale;
import java.util.ResourceBundle;

public class ExprEnchantDisplay extends SimplePropertyExpression<EnchantmentType, String> {

    static {
        try {
            Module module = Bukkit.class.getModule();
            TranslationRegistry registry = TranslationRegistry.create(Key.key("skirt", "mojang-translations"));
            registry.registerAll(Locale.US, ResourceBundle.getBundle("mojang-translations/en_US", module), false);
            GlobalTranslator.get().addSource(registry);
            GlobalTranslator.get().addSource(new Translator() {
                private final JsonObject json = JsonParser.parseReader(new InputStreamReader(module.getResourceAsStream("assets/minecraft/lang/en_us.json"))).getAsJsonObject();
                @Override
                public @NotNull Key name() {
                    return Key.key("skirt", "minecraft-lang");
                }
                @Override
                public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
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
        return (type == null) ? null : LegacyComponentSerializer.legacySection().serialize(GlobalTranslator.render(type.displayName(enchantmentType.getLevel()), Locale.US));
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
