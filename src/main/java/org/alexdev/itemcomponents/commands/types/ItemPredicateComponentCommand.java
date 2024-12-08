package org.alexdev.itemcomponents.commands.types;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ItemPredicateComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<ItemAdventurePredicate>> {

    public ItemPredicateComponentCommand(@NotNull String name, DataComponentType.Valued<@NotNull ItemAdventurePredicate> type) {
        super(name, type);
    }

    @Override
    public CommandNode<CommandSourceStack> getNode() {
        // commands: add -> (list of namespaces), remove -> (list of namespaces), list -> (list of namespaces with possibility of removing them)
        return Commands.literal(name)
                .then(Commands.literal("add").then(Commands.argument("namespace", new NameSpaceListArgumentType())
                        .executes(context -> {
                            if (isInvalidItem(context)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            final ItemStack itemStack = getItem(context);
                            final List<NamespacedKey> namespaces = context.getArgument("namespace", ListNamespaces.class).namespaces();
                            if (namespaces.isEmpty()) {
                                return Command.SINGLE_SUCCESS;
                            }
                            final Set<@NotNull NamespacedKey> namespacesSet = Sets.newHashSet(namespaces);

                            final List<BlockPredicate> predicates = Lists.newArrayList();
                            if (itemStack.hasData(type)) {
                                final ItemAdventurePredicate itemAdventurePredicate = itemStack.getData(type);
                                predicates.addAll(itemAdventurePredicate.predicates());
                            }

                            final List<TypedKey<BlockType>> blocks = namespacesSet.stream()
                                    .map(n -> TypedKey.create(RegistryKey.BLOCK, n))
                                    .toList();

                            final RegistryKeySet<@NotNull BlockType> blocksRegistryKeySet = RegistrySet.keySet(RegistryKey.BLOCK, blocks);
                            predicates.add(BlockPredicate.predicate().blocks(blocksRegistryKeySet).build());

                            itemStack.setData(type, ItemAdventurePredicate.itemAdventurePredicate(predicates));
                            return Command.SINGLE_SUCCESS;
                        }).build()))
                .then(Commands.literal("remove").then(Commands.argument("namespace", new NameSpaceListArgumentType())
                        .executes(context -> {
                            if (isInvalidItem(context)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            final ItemStack itemStack = getItem(context);
                            final List<NamespacedKey> namespaces = context.getArgument("namespace", ListNamespaces.class).namespaces();
                            if (namespaces.isEmpty()) {
                                return Command.SINGLE_SUCCESS;
                            }
                            final Set<@NotNull NamespacedKey> namespacesSet = Sets.newHashSet(namespaces);

                            final List<BlockPredicate> predicates = Lists.newArrayList();
                            if (itemStack.hasData(type)) {
                                final ItemAdventurePredicate itemAdventurePredicate = itemStack.getData(type);
                                predicates.addAll(itemAdventurePredicate.predicates());
                            }

                            final List<TypedKey<BlockType>> blocks = namespacesSet.stream()
                                    .map(n -> TypedKey.create(RegistryKey.BLOCK, n))
                                    .toList();

                            final RegistryKeySet<@NotNull BlockType> blocksRegistryKeySet = RegistrySet.keySet(RegistryKey.BLOCK, blocks);
                            predicates.removeIf(predicate -> {
                                AtomicBoolean remove = new AtomicBoolean(false);
                                blocksRegistryKeySet.values().forEach(blockType -> {
                                    if (predicate.blocks().contains(blockType)) {
                                        remove.set(true);
                                    }
                                });
                                return remove.get();
                            });

                            if (predicates.isEmpty()) {
                                itemStack.resetData(type);
                                return Command.SINGLE_SUCCESS;
                            }

                            itemStack.setData(type, ItemAdventurePredicate.itemAdventurePredicate(predicates));
                            return Command.SINGLE_SUCCESS;
                        }).build()))
                .then(Commands.literal("list").executes(context -> {
                    if (isInvalidItem(context)) {
                        return Command.SINGLE_SUCCESS;
                    }

                    final ItemStack itemStack = getItem(context);
                    if (!itemStack.hasData(type)) {
                        return Command.SINGLE_SUCCESS;
                    }
                    final ItemAdventurePredicate itemAdventurePredicate = itemStack.getData(type);
                    final List<BlockPredicate> predicates = itemAdventurePredicate.predicates();


                    Component component = Component.empty();
                    for (BlockPredicate predicate : predicates) {
                        final Component predicateComponent = Component.text(predicate.blocks().values().stream()
                                .map(b -> b.key().asString())
                                .collect(Collectors.joining(", ")));
                        final Component line = Component.text(predicate.blocks().size() + " blocks: ").color(TextColor.color(0x00FF00))
                                .hoverEvent(predicateComponent);
                        final Component remove = Component.text("- ").color(TextColor.color(0xFF0000))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/itemcomponents " + name + " remove " + predicate.blocks().values().stream()
                                        .map(b -> b.key().asString())
                                        .collect(Collectors.joining(","))));
                        component = component.append(line).append(Component.text(" ")).append(remove).append(Component.newline());
                    }

                    context.getSource().getExecutor().sendMessage(component);

                    return Command.SINGLE_SUCCESS;
                }))
                .build();
//        return Commands.literal(name)
//                .then(Commands.literal("set")
//                        .then(Commands.argument("enchantment", ArgumentTypes.resource(RegistryKey.ENCHANTMENT))
//                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 255))
//                                        .executes(context -> {
//                                            if (isInvalidItem(context)) {
//                                                return Command.SINGLE_SUCCESS;
//                                            }
//
//                                            final ItemStack itemStack = getItem(context);
//                                            final Enchantment enchantmentArgumentType = context.getArgument("enchantment", Enchantment.class);
//                                            final int level = context.getArgument("level", Integer.class);
//                                            final Map<Enchantment, Integer> enchantments = Maps.newHashMap(itemStack.getEnchantments());
//                                            enchantments.put(enchantmentArgumentType, level);
//                                            final boolean showInTooltip = itemStack.hasData(type) ? itemStack.getData(type).showInTooltip() : true;
//                                            itemStack.setData(type, ItemEnchantments.itemEnchantments(enchantments, showInTooltip));
//                                            return Command.SINGLE_SUCCESS;
//                                        }).build())))
//                .then(Commands.literal("remove")
//                        .then(Commands.argument("enchantment", ArgumentTypes.resource(RegistryKey.ENCHANTMENT))
//                                .executes(context -> {
//                                    if (isInvalidItem(context)) {
//                                        return Command.SINGLE_SUCCESS;
//                                    }
//
//                                    final ItemStack itemStack = getItem(context);
//                                    final Enchantment enchantmentArgumentType = context.getArgument("enchantment", Enchantment.class);
//                                    final Map<Enchantment, Integer> enchantments = Maps.newHashMap(itemStack.getEnchantments());
//                                    enchantments.remove(enchantmentArgumentType);
//                                    final boolean showInTooltip = itemStack.hasData(type) ? itemStack.getData(type).showInTooltip() : true;
//                                    itemStack.setData(type, ItemEnchantments.itemEnchantments(enchantments, showInTooltip));
//                                    return Command.SINGLE_SUCCESS;
//                                }).build()))
//                .then(Commands.literal("clear")
//                        .executes(context -> {
//                            if (isInvalidItem(context)) {
//                                return Command.SINGLE_SUCCESS;
//                            }
//
//                            final ItemStack itemStack = getItem(context);
//                            final Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
//                            enchantments.clear();
//                            final boolean showInTooltip = itemStack.hasData(type) ? itemStack.getData(type).showInTooltip() : true;
//                            itemStack.setData(type, ItemEnchantments.itemEnchantments(enchantments, showInTooltip));
//                            return Command.SINGLE_SUCCESS;
//                        }).build())
//                .then(Commands.literal("showInTooltip")
//                        .then(Commands.argument("showInTooltip", BoolArgumentType.bool())
//                                .executes(context -> {
//                                    if (isInvalidItem(context)) {
//                                        return Command.SINGLE_SUCCESS;
//                                    }
//
//                                    final ItemStack itemStack = getItem(context);
//                                    final boolean showInTooltip = context.getArgument("showInTooltip", Boolean.class);
//                                    itemStack.setData(type, ItemEnchantments.itemEnchantments(itemStack.getEnchantments(), showInTooltip));
//                                    return Command.SINGLE_SUCCESS;
//                                }).build()))
//                .build();
    }

    private static class NameSpaceListArgumentType implements CustomArgumentType.Converted<ListNamespaces, String> {

        @Override
        @NotNull
        public ListNamespaces convert(@NotNull String string) throws CommandSyntaxException {
            final List<NamespacedKey> namespaces = Lists.newArrayList();
            try {
                for (String s : string.split(",")) {
                    namespaces.add(NamespacedKey.fromString(s));
                }
            } catch (IllegalArgumentException e) {
                final Message message = MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize("<red>Invalid namespace list: " + string));
                throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
            }
            return new ListNamespaces(namespaces);
        }

        @Override
        @NotNull
        public ArgumentType<String> getNativeType() {
            return StringArgumentType.greedyString();
        }
    }

    private record ListNamespaces(List<NamespacedKey> namespaces) {
    }


}
