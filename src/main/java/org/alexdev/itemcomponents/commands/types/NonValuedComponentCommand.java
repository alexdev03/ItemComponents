package org.alexdev.itemcomponents.commands.types;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class NonValuedComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull NonValued> {

    public NonValuedComponentCommand(@NotNull String name, DataComponentType.NonValued type) {
        super(name, type);
    }

    public CommandNode<CommandSourceStack> getNode() {
        return Commands.literal(name)
                .executes(context -> {
                    if (isInvalidItem(context)) {
                        return Command.SINGLE_SUCCESS;
                    }

                    final ItemStack itemStack = getItem(context);
                    itemStack.setData(type);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

}
