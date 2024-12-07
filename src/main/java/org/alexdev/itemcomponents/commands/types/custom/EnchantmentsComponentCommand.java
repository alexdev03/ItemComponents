package org.alexdev.itemcomponents.commands.types.custom;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryKey;
import org.alexdev.itemcomponents.commands.types.AbstractComponentCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EnchantmentsComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<ItemEnchantments>> {

    public EnchantmentsComponentCommand(@NotNull String name, DataComponentType.Valued<@NotNull ItemEnchantments> type) {
        super(name, type);
    }

    @Override
    public CommandNode<CommandSourceStack> getNode() {
        return Commands.literal(name)
                .then(Commands.literal("set")
                        .then(Commands.argument("enchantment", ArgumentTypes.resource(RegistryKey.ENCHANTMENT))
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 255))
                                        .executes(context -> {
                                            if (isInvalidItem(context)) {
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            final ItemStack itemStack = getItem(context);
                                            final Enchantment enchantmentArgumentType = context.getArgument("enchantment", Enchantment.class);
                                            final int level = context.getArgument("level", Integer.class);
                                            final Map<Enchantment, Integer> enchantments = Maps.newHashMap(itemStack.getEnchantments());
                                            enchantments.put(enchantmentArgumentType, level);
                                            final boolean showInTooltip = itemStack.hasData(type) ? itemStack.getData(type).showInTooltip() : true;
                                            itemStack.setData(type, ItemEnchantments.itemEnchantments(enchantments, showInTooltip));
                                            return Command.SINGLE_SUCCESS;
                                        }).build())))
                .then(Commands.literal("remove")
                        .then(Commands.argument("enchantment", ArgumentTypes.resource(RegistryKey.ENCHANTMENT))
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final Enchantment enchantmentArgumentType = context.getArgument("enchantment", Enchantment.class);
                                    final Map<Enchantment, Integer> enchantments = Maps.newHashMap(itemStack.getEnchantments());
                                    enchantments.remove(enchantmentArgumentType);
                                    final boolean showInTooltip = itemStack.hasData(type) ? itemStack.getData(type).showInTooltip() : true;
                                    itemStack.setData(type, ItemEnchantments.itemEnchantments(enchantments, showInTooltip));
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .then(Commands.literal("clear")
                        .executes(context -> {
                            if (isInvalidItem(context)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            final ItemStack itemStack = getItem(context);
                            final Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
                            enchantments.clear();
                            final boolean showInTooltip = itemStack.hasData(type) ? itemStack.getData(type).showInTooltip() : true;
                            itemStack.setData(type, ItemEnchantments.itemEnchantments(enchantments, showInTooltip));
                            return Command.SINGLE_SUCCESS;
                        }).build())
                .then(Commands.literal("showInTooltip")
                        .then(Commands.argument("showInTooltip", BoolArgumentType.bool())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final boolean showInTooltip = context.getArgument("showInTooltip", Boolean.class);
                                    itemStack.setData(type, ItemEnchantments.itemEnchantments(itemStack.getEnchantments(), showInTooltip));
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .build();
    }


}
