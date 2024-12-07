package org.alexdev.itemcomponents.commands.types;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.datacomponent.DataComponentType;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class TextComponentComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<Component>> {

    public TextComponentComponentCommand(@NotNull String name, DataComponentType.Valued<Component> type) {
        super(name, type);
    }

    public ArgumentCommandNode<CommandSourceStack, Component> getNode() {
        return Commands.argument("component", new TextComponentArgumentType())
                .executes(context -> {
                    if (isInvalidItem(context)) {
                        return Command.SINGLE_SUCCESS;
                    }

                    final ItemStack itemStack = getItem(context);
                    final Component component = context.getArgument("component", Component.class);
                    itemStack.setData(type, component);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    private static class TextComponentArgumentType implements CustomArgumentType.Converted<Component, String> {

        @Override
        @NotNull
        public Component convert(@Subst("string") @NotNull String string) throws CommandSyntaxException {
            try {
                return MiniMessage.miniMessage().deserialize(string);
            } catch (InvalidKeyException e) {
                final Message message = MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize("<red>Invalid minimessage string: " + string));
                throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
            }
        }

        @Override
        @NotNull
        public ArgumentType<String> getNativeType() {
            return StringArgumentType.greedyString();
        }
    }

}
