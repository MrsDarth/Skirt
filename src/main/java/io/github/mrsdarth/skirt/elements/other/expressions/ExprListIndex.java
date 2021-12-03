package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class ExprListIndex extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprListIndex.class, Object.class, ExpressionType.COMBINED,
                "[(first|2¦last)] [(1¦variable)] index of %object% in %objects%",
                "[all] [(1¦variable)] ind(ex|ic)es %object% in %objects%");
    }

    private Expression<?> elementExpr, listExpr;

    private boolean variable;
    private int pattern;

    @Override
    protected @Nullable
    Object[] get(@NotNull Event e) {
        Object element = elementExpr.getSingle(e);
        if (element == null) return null;
        if (variable) {
            Object o = ((Variable<?>) listExpr).getRaw(e);
            if (o instanceof Map) {
                @SuppressWarnings("unchecked")
                Stream<String> lazyIndexes = ((Map<String, Object>) o).entrySet().stream()
                        .filter(entry -> entry.getValue().equals(element))
                        .map(Map.Entry::getKey);
                return switch (pattern) {
                    case 0 -> lazyIndexes.findFirst().map(CollectionUtils::array).orElse(null);
                    case 1 -> lazyIndexes.toArray(String[]::new);
                    case 2 -> lazyIndexes.reduce((first, last) -> last).map(CollectionUtils::array).orElse(null);
                    default -> null;
                };
            }
            return null;
        } else {
            Object[] array = listExpr.getArray(e);
            return switch (pattern) {
                case 0 -> {
                    int index = CollectionUtils.indexOf(array, element);
                    yield index == -1 ? null : CollectionUtils.array(index);
                }
                case 1 -> IntStream.range(0, array.length)
                                .filter(i -> element.equals(array[i]))
                                .mapToObj(Long::valueOf)
                                .toArray(Long[]::new);
                case 2 -> {
                    int index = CollectionUtils.lastIndexOf(array, element);
                    yield index == -1 ? null : CollectionUtils.array(index);
                }
                default -> null;
            };
        }
    }

    @Override
    public boolean isSingle() {
        return pattern != 1;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return variable ? String.class : Long.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (switch (pattern) {
            case 0 -> "first";
            case 1 -> "all";
            case 2 -> "last";
            default -> throw new IllegalStateException();
        }) + (variable ? " variable" : "") + (isSingle() ? " index" : " indexes") + " of " + elementExpr.toString(e, debug) + " in " + listExpr.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        variable = (parseResult.mark & 1) == 1;
        pattern = matchedPattern ^ (parseResult.mark & 2);
        elementExpr = LiteralUtils.defendExpression(exprs[0]);
        listExpr = LiteralUtils.defendExpression(exprs[1]);
        if (!listExpr.isSingle())
            return false;
        if (variable && !(elementExpr instanceof Variable)) {
            Skript.error(listExpr + " is not a list variable");
            return false;
        }
        return LiteralUtils.canInitSafely(elementExpr, listExpr);
    }
}
