package org.alexdev.itemcomponents.commands.types;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentType;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class BooleanComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<Boolean>> {

    public BooleanComponentCommand(@NotNull String name, DataComponentType.Valued<@NotNull Boolean> type) {
        super(name, type);
    }

    public ArgumentCommandNode<CommandSourceStack, Boolean> getNode() {
        return Commands.argument("boolean", BoolArgumentType.bool())
                .executes(context -> {
                    if (isInvalidItem(context)) {
                        return Command.SINGLE_SUCCESS;
                    }

                    final ItemStack itemStack = getItem(context);
                    final boolean booleanValue = context.getArgument("boolean", Boolean.class);
                    itemStack.setData(type, booleanValue);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

}
