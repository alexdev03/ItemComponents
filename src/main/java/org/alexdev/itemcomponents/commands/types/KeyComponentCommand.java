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
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class KeyComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<Key>> {

    public KeyComponentCommand(@NotNull String name, DataComponentType.Valued<Key> type) {
        super(name, type);
    }

    public ArgumentCommandNode<CommandSourceStack, Key> getNode() {
        return Commands.argument("key", new KeyArgumentType())
                .executes(context -> {
                    if (isInvalidItem(context)) {
                        return Command.SINGLE_SUCCESS;
                    }

                    final ItemStack itemStack = getItem(context);
                    final Key key = context.getArgument("key", Key.class);
                    itemStack.setData(type, key);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    private static class KeyArgumentType implements CustomArgumentType.Converted<Key, String> {

        @Override
        @NotNull
        public Key convert(@Subst("string") @NotNull String string) throws CommandSyntaxException {
            try {
                return NamespacedKey.fromString(string);
            } catch (InvalidKeyException e) {
                final Message message = MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize("<red>Invalid key: " + string));
                throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
            }
        }

        @Override
        @NotNull
        public ArgumentType<String> getNativeType() {
            return StringArgumentType.greedyString();
        }

        @Override
        @NotNull
        public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, SuggestionsBuilder builder) {
            Registry.ITEM.stream()
                    .map(r -> r.getKey().asString())
                    .filter(s -> s.startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);

            return builder.buildFuture();
        }
    }

}
