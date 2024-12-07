package org.alexdev.itemcomponents.commands.types;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentType;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class IntegerComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<Integer>> {

    private final IntegerType integerType;

    public IntegerComponentCommand(@NotNull String name, DataComponentType.Valued<Integer> type, @NotNull IntegerType integerType) {
        super(name, type);
        this.integerType = integerType;
    }

    public ArgumentCommandNode<CommandSourceStack, Integer> getNode() {
        return Commands.argument("integer", IntegerArgumentType.integer(integerType.min, integerType.max))
                .executes(context -> {
                    if (isInvalidItem(context)) {
                        return Command.SINGLE_SUCCESS;
                    }

                    final ItemStack itemStack = getItem(context);
                    final int integer = context.getArgument("integer", Integer.class);
                    itemStack.setData(type, integer);
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

    @Getter
    public enum IntegerType {

        POSITIVE(1, Integer.MAX_VALUE),
        NON_NEGATIVE(0, Integer.MAX_VALUE),
        NEGATIVE(-1, Integer.MAX_VALUE),
        NON_POSITIVE(Integer.MIN_VALUE, 0);

        private final int min;
        private final int max;

        IntegerType(int min, int max) {
            this.min = min;
            this.max = max;
        }

    }

}
