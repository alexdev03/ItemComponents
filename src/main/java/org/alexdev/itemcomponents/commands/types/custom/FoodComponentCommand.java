package org.alexdev.itemcomponents.commands.types.custom;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.alexdev.itemcomponents.commands.types.AbstractComponentCommand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FoodComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<FoodProperties>> {

    public FoodComponentCommand(@NotNull String name, DataComponentType.Valued<@NotNull FoodProperties> type) {
        super(name, type);
    }

    @Override
    public CommandNode<CommandSourceStack> getNode() {
        return Commands.literal(name)
                .then(Commands.literal("canAlwaysEat")
                        .then(Commands.argument("canAlwaysEat", BoolArgumentType.bool())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final boolean booleanValue = context.getArgument("canAlwaysEat", Boolean.class);
                                    itemStack.setData(type, getFoodProperties(itemStack).canAlwaysEat(booleanValue).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .then(Commands.literal("saturation")
                        .then(Commands.argument("saturation", FloatArgumentType.floatArg())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final float floatValue = context.getArgument("saturation", Float.class);
                                    itemStack.setData(type, getFoodProperties(itemStack).saturation(floatValue).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .then(Commands.literal("nutrition")
                        .then(Commands.argument("nutrition", IntegerArgumentType.integer())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final int integerValue = context.getArgument("nutrition", Integer.class);
                                    itemStack.setData(type, getFoodProperties(itemStack).nutrition(integerValue).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .build();
    }

    enum Type {
        ALWAYS_EAT,
        SATURATION,
        NUTRITION,
    }

    @NotNull
    private FoodProperties.Builder getFoodProperties(@NotNull ItemStack itemStack) {
        return itemStack.hasData(DataComponentTypes.FOOD) ? itemStack.getData(DataComponentTypes.FOOD).toBuilder() : FoodProperties.food();
    }
}
