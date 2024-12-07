package org.alexdev.itemcomponents.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.datacomponent.DataComponentType;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.alexdev.itemcomponents.ItemComponents;
import org.alexdev.itemcomponents.commands.types.AbstractCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RemoveComponentCommand extends AbstractCommand {

    private final ItemComponents plugin;
    private static final Map<String, DataComponentType> components;

    static {
        components = Maps.newHashMap();
    }

    public RemoveComponentCommand(@NotNull String name, @NotNull ItemComponents plugin) {
        super(name);
        this.plugin = plugin;
    }

    private void loadComponents() {
        try {
            final List<Field> fields = List.of(DataComponentType.class.getFields());
            for (final Field field : fields) {
                if (field.getType().isAssignableFrom(DataComponentType.class)) {
                    final DataComponentType dataComponentType = (DataComponentType) field.get(null);
                    components.put(field.getName(), dataComponentType);
                    final String name = field.getName().charAt(0) + field.getName().substring(1).toLowerCase().replace("_", "");
                    components.put(name, dataComponentType);
                }
            }
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Failed to load components", e);
        }
    }

    @NotNull
    public CommandNode<CommandSourceStack> getNode() {
        return Commands.literal(name)
                .then(Commands.argument("component", new ComponentArgumentType())
                        .executes(context -> {
                            if (isInvalidItem(context)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            final ItemStack itemStack = getItem(context);
                            final DataComponentType component = context.getArgument("component", DataComponentType.class);
                            itemStack.resetData(component);
                            return Command.SINGLE_SUCCESS;
                        }).build())
                .build();
    }

    private static class ComponentArgumentType implements CustomArgumentType.Converted<DataComponentType, String> {

        @Override
        @NotNull
        public DataComponentType convert(@Subst("string") @NotNull String string) throws CommandSyntaxException {
            try {
                return getComponent(string)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid component type: " + string));
            } catch (IllegalArgumentException e) {
                final Message message = MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize("<red>" + e.getMessage()));
                throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
            }
        }

        @Override
        @NotNull
        public ArgumentType<String> getNativeType() {
            return StringArgumentType.greedyString();
        }

        public Optional<DataComponentType> getComponent(@NotNull String string) {
            return Optional.ofNullable(components.get(string.toUpperCase()));
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            components.keySet().stream()
                    .filter(s -> s.equals(s.toUpperCase()))
                    .filter(s -> s.startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);

            return builder.buildFuture();
        }
    }

}
