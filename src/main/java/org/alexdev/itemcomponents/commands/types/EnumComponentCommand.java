package org.alexdev.itemcomponents.commands.types;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.datacomponent.DataComponentType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class EnumComponentCommand<T extends Enum<T>> extends AbstractComponentCommand<DataComponentType.@NotNull Valued<T>> {

    private final Class<T> enumType;

    public EnumComponentCommand(@NotNull String name, DataComponentType.Valued<T> type, @NotNull Class<T> enumType) {
        super(name, type);
        this.enumType = enumType;
    }

    public ArgumentCommandNode<CommandSourceStack, T> getNode() {
        return Commands.argument("enum", new EnumArgumentType())
                .executes(context -> {
                    if (isInvalidItem(context)) {
                        return Command.SINGLE_SUCCESS;
                    }


                    final ItemStack itemStack = getItem(context);
                    final T enumValue = context.getArgument("enum", enumType);
                    itemStack.setData(type, enumValue);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    private class EnumArgumentType implements CustomArgumentType.Converted<T, String> {

        @Override
        @NotNull
        public T convert(@Subst("string") @NotNull String string) throws CommandSyntaxException {
            try {
                return Enum.valueOf(enumType, string);
            } catch (IllegalArgumentException e) {
                final Message message = MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize("<red>Invalid enum: " + string + ", valid enums: " + getValidEnums()));
                throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
            }
        }

        @NotNull
        private String getValidEnums() {
            return String.join(", ", getEnums());
        }

        @NotNull
        private List<String> getEnums() {
            return Arrays.stream(enumType.getEnumConstants())
                    .map(Enum::name)
                    .toList();
        }

        @Override
        @NotNull
        public ArgumentType<String> getNativeType() {
            return StringArgumentType.greedyString();
        }


        @Override
        @NotNull
        public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, SuggestionsBuilder builder) {
            getEnums().stream()
                    .filter(s -> s.startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);

            return builder.buildFuture();
        }
    }

}
