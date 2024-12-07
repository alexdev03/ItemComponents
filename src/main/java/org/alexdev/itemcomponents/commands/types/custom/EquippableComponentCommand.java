package org.alexdev.itemcomponents.commands.types.custom;

import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.alexdev.itemcomponents.commands.types.AbstractComponentCommand;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EquippableComponentCommand extends AbstractComponentCommand<DataComponentType.@NotNull Valued<Equippable>> {

    public EquippableComponentCommand(@NotNull String name, DataComponentType.Valued<@NotNull Equippable> type) {
        super(name, type);
    }

    @Override
    public CommandNode<CommandSourceStack> getNode() {
        return Commands.literal(name)
                .then(Commands.literal("set")
                        .then(Commands.literal("equipmentSlot")
                                .then(Commands.argument("equipmentSlot", new EquipmentSlotArgumentType())
                                        .executes(context -> {
                                            if (isInvalidItem(context)) {
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            final ItemStack itemStack = getItem(context);
                                            final EquipmentSlot equipmentSlot = context.getArgument("equipmentSlot", EquipmentSlot.class);
                                            if (!itemStack.hasData(type)) {
                                                itemStack.setData(type, Equippable.equippable(equipmentSlot).build());
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            final Equippable equippable = itemStack.getData(type);
                                            itemStack.setData(type, Equippable.equippable(equipmentSlot)
                                                    .model(equippable.model())
                                                    .cameraOverlay(equippable.cameraOverlay())
                                                    .allowedEntities(equippable.allowedEntities())
                                                    .dispensable(equippable.dispensable())
                                                    .swappable(equippable.swappable())
                                                    .damageOnHurt(equippable.damageOnHurt())
                                                    .build());
                                            return Command.SINGLE_SUCCESS;
                                        }).build()))
                .then(Commands.literal("equipSound")
                        .then(Commands.argument("equipSound", ArgumentTypes.namespacedKey())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final NamespacedKey equipSound = context.getArgument("equipSound", NamespacedKey.class);
                                    if (!itemStack.hasData(type)) {
                                        itemStack.setData(type, Equippable.equippable(EquipmentSlot.HAND).equipSound(equipSound).build());
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().equipSound(equipSound).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .then(Commands.literal("model")
                        .then(Commands.argument("model", ArgumentTypes.namespacedKey())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final NamespacedKey model = context.getArgument("model", NamespacedKey.class);
                                    if (!itemStack.hasData(type)) {
                                        itemStack.setData(type, Equippable.equippable(EquipmentSlot.HAND).model(model).build());
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().model(model).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .then(Commands.literal("cameraOverlay")
                        .then(Commands.argument("cameraOverlay", ArgumentTypes.namespacedKey())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final NamespacedKey cameraOverlay = context.getArgument("cameraOverlay", NamespacedKey.class);
                                    if (!itemStack.hasData(type)) {
                                        itemStack.setData(type, Equippable.equippable(EquipmentSlot.HAND).cameraOverlay(cameraOverlay).build());
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().cameraOverlay(cameraOverlay).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .then(Commands.literal("allowedEntities")
                        //add - remove - list
                        .then(Commands.literal("add")
                                .then(Commands.argument("entityType", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE))
                                        .executes(context -> {
                                            if (isInvalidItem(context)) {
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            final ItemStack itemStack = getItem(context);
                                            final EntityType entityType = context.getArgument("entityType", EntityType.class);
                                            final TypedKey<EntityType> entityTypeKey = TypedKey.create(RegistryKey.ENTITY_TYPE, entityType.getKey());
                                            if (!itemStack.hasData(type)) {
                                                final RegistryKeySet<@NotNull EntityType> entityTypeSet = RegistrySet.keySet(RegistryKey.ENTITY_TYPE, entityTypeKey);
                                                itemStack.setData(type, Equippable.equippable(EquipmentSlot.HAND).allowedEntities(entityTypeSet).build());
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            final Equippable equippable = itemStack.getData(type);
                                            if (equippable.allowedEntities() == null) {
                                                final RegistryKeySet<@NotNull EntityType> entityTypeSet = RegistrySet.keySet(RegistryKey.ENTITY_TYPE, entityTypeKey);
                                                itemStack.setData(type, equippable.toBuilder().allowedEntities(entityTypeSet).build());
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            if (equippable.allowedEntities().contains(entityTypeKey)) {
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            final Set<@NotNull TypedKey<EntityType>> allowedEntities = Sets.newHashSet(equippable.allowedEntities());
                                            allowedEntities.add(entityTypeKey);
                                            final RegistryKeySet<@NotNull EntityType> newAllowedEntities = RegistrySet.keySet(RegistryKey.ENTITY_TYPE, allowedEntities);
                                            itemStack.setData(type, equippable.toBuilder().allowedEntities(newAllowedEntities).build());
                                            return Command.SINGLE_SUCCESS;
                                        }).build()))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("entityType", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE))
                                        .executes(context -> {
                                            if (isInvalidItem(context)) {
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            final ItemStack itemStack = getItem(context);
                                            final EntityType entityType = context.getArgument("entityType", EntityType.class);
                                            final TypedKey<EntityType> entityTypeKey = TypedKey.create(RegistryKey.ENTITY_TYPE, entityType.getKey());
                                            if (!itemStack.hasData(type)) {
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            final Equippable equippable = itemStack.getData(type);
                                            if (equippable.allowedEntities() == null) {
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            if (!equippable.allowedEntities().contains(entityTypeKey)) {
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            final Set<@NotNull TypedKey<EntityType>> allowedEntities = Sets.newHashSet(equippable.allowedEntities());
                                            allowedEntities.remove(entityTypeKey);
                                            final RegistryKeySet<@NotNull EntityType> newAllowedEntities = RegistrySet.keySet(RegistryKey.ENTITY_TYPE, allowedEntities);
                                            itemStack.setData(type, equippable.toBuilder().allowedEntities(newAllowedEntities).build());
                                            return Command.SINGLE_SUCCESS;
                                        }).build()))
                        .then(Commands.literal("list")
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    if (!itemStack.hasData(type)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    if (equippable.allowedEntities() == null) {
                                        context.getSource().getSender().sendRichMessage("<green>No allowed entities");
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Collection<@NotNull TypedKey<EntityType>> allowedEntities = equippable.allowedEntities().values();
                                    if (allowedEntities.isEmpty()) {
                                        context.getSource().getSender().sendRichMessage("<green>No allowed entities");
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final StringBuilder stringBuilder = new StringBuilder();
                                    for (TypedKey<EntityType> typedKey : allowedEntities) {
                                        stringBuilder.append(typedKey.key().key()).append(", ");
                                    }
                                    stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                                    context.getSource().getSender().sendRichMessage("<green>Allowed Entities: " + stringBuilder);
                                    return Command.SINGLE_SUCCESS;
                                })
                        ))
                .then(Commands.literal("dispensable")
                        .then(Commands.argument("dispensable", BoolArgumentType.bool())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    final ItemStack itemStack = getItem(context);
                                    final boolean dispensable = context.getArgument("dispensable", Boolean.class);
                                    if (!itemStack.hasData(type)) {
                                        itemStack.setData(type, Equippable.equippable(EquipmentSlot.HAND).dispensable(dispensable).build());
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().dispensable(dispensable).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .then(Commands.literal("swappable")
                        .then(Commands.argument("swappable", BoolArgumentType.bool())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final ItemStack itemStack = getItem(context);
                                    final boolean swappable = context.getArgument("swappable", Boolean.class);
                                    if (!itemStack.hasData(type)) {
                                        itemStack.setData(type, Equippable.equippable(EquipmentSlot.HAND).swappable(swappable).build());
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().swappable(swappable).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                .then(Commands.literal("damageOnHurt")
                        .then(Commands.argument("damageOnHurt", BoolArgumentType.bool())
                                .executes(context -> {
                                    if (isInvalidItem(context)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final ItemStack itemStack = getItem(context);
                                    final boolean damageOnHurt = context.getArgument("damageOnHurt", Boolean.class);
                                    if (!itemStack.hasData(type)) {
                                        itemStack.setData(type, Equippable.equippable(EquipmentSlot.HAND).damageOnHurt(damageOnHurt).build());
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().damageOnHurt(damageOnHurt).build());
                                    return Command.SINGLE_SUCCESS;
                                }).build()))
                )
                .then(Commands.literal("remove")
                .then(Commands.argument("param", new RemoveArgumentType())
                        .executes(context -> {
                            if (isInvalidItem(context)) {
                                return Command.SINGLE_SUCCESS;
                            }

                            final ItemStack itemStack = getItem(context);
                            final RemoveEnum param = context.getArgument("param", RemoveEnum.class);
                            switch (param) {
                                case EQUIP_SOUND -> {
                                    if (!itemStack.hasData(type)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().equipSound(null).build());
                                    return Command.SINGLE_SUCCESS;
                                }
                                case MODEL -> {
                                    if (!itemStack.hasData(type)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().model(null).build());
                                    return Command.SINGLE_SUCCESS;
                                }
                                case CAMERA_OVERLAY -> {
                                    if (!itemStack.hasData(type)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().cameraOverlay(null).build());
                                    return Command.SINGLE_SUCCESS;
                                }
                                case ALLOWED_ENTITIES -> {
                                    if (!itemStack.hasData(type)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().allowedEntities(null).build());
                                    return Command.SINGLE_SUCCESS;
                                }
                                case DISPENSEABLE -> {
                                    if (!itemStack.hasData(type)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().dispensable(false).build());
                                    return Command.SINGLE_SUCCESS;
                                }
                                case SWAPPABLE -> {
                                    if (!itemStack.hasData(type)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().swappable(false).build());
                                    return Command.SINGLE_SUCCESS;
                                }
                                case DAMAGE_ON_HURT -> {
                                    if (!itemStack.hasData(type)) {
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    final Equippable equippable = itemStack.getData(type);
                                    itemStack.setData(type, equippable.toBuilder().damageOnHurt(false).build());
                                    return Command.SINGLE_SUCCESS;
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        }).build()))
                .build();
    }

    private static class EquipmentSlotArgumentType implements CustomArgumentType.Converted<EquipmentSlot, String> {

        @Override
        @NotNull
        public EquipmentSlot convert(@Subst("string") @NotNull String string) throws CommandSyntaxException {
            try {
                return EquipmentSlot.valueOf(string.toUpperCase());
            } catch (InvalidKeyException e) {
                final Message message = MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize("<red>Invalid equipment slot: " + string + ". Available slots are " +
                        Arrays.stream(EquipmentSlot.values()).map(EquipmentSlot::name).collect(Collectors.joining(", ")) + "</red>"));
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
            Arrays.stream(EquipmentSlot.values())
                    .map(EquipmentSlot::name)
                    .filter(s -> s.startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);

            return builder.buildFuture();
        }
    }

    enum RemoveEnum {
        EQUIP_SOUND,
        MODEL,
        CAMERA_OVERLAY,
        ALLOWED_ENTITIES,
        DISPENSEABLE,
        SWAPPABLE,
        DAMAGE_ON_HURT
    }

    private static class RemoveArgumentType implements CustomArgumentType.Converted<RemoveEnum, String> {
        @Override
        @NotNull
        public RemoveEnum convert(@Subst("string") @NotNull String string) throws CommandSyntaxException {
            try {
                return RemoveEnum.valueOf(string.toUpperCase());
            } catch (InvalidKeyException e) {
                final Message message = MessageComponentSerializer.message().serialize(MiniMessage.miniMessage().deserialize("<red>Invalid remove parameter: " + string + ". Available parameters are " +
                        Arrays.stream(EquipmentSlot.values()).map(EquipmentSlot::name).collect(Collectors.joining(", ")) + "</red>"));
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
