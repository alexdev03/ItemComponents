package org.alexdev.itemcomponents.commands.types.custom;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.Unbreakable;
import org.alexdev.itemcomponents.commands.types.AbstractComponentCommand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UnbreakableComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<Unbreakable>> {

    public UnbreakableComponentCommand(@NotNull String name, DataComponentType.Valued<@NotNull Unbreakable> type) {
        super(name, type);
    }

    @Override
    public CommandNode<CommandSourceStack> getNode() {
        return Commands.literal(name)
                .then(Commands.argument("hideTooltip", BoolArgumentType.bool())
                        .executes(context -> {
                            if (isInvalidItem(context)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            final ItemStack itemStack = getItem(context);
                            final boolean booleanValue = context.getArgument("hideTooltip", Boolean.class);
                            itemStack.setData(type, Unbreakable.unbreakable(booleanValue));
                            return Command.SINGLE_SUCCESS;
                        }).build())
                .executes(context -> {
                    if (isInvalidItem(context)) {
                        return Command.SINGLE_SUCCESS;
                    }

                    final ItemStack itemStack = getItem(context);
                    itemStack.setData(type, Unbreakable.unbreakable().build());
                    return Command.SINGLE_SUCCESS;
                }).build();
    }


}
