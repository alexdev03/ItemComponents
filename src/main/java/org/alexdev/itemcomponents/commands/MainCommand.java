package org.alexdev.itemcomponents.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.item.MapPostProcessing;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.RequiredArgsConstructor;
import org.alexdev.itemcomponents.ItemComponents;
import org.alexdev.itemcomponents.commands.types.*;
import org.alexdev.itemcomponents.commands.types.custom.EnchantmentsComponentCommand;
import org.alexdev.itemcomponents.commands.types.custom.EquippableComponentCommand;
import org.alexdev.itemcomponents.commands.types.custom.FoodComponentCommand;
import org.alexdev.itemcomponents.commands.types.custom.UnbreakableComponentCommand;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
@SuppressWarnings({"unused", "UnstableApiUsage"})
public class MainCommand {

    private final ItemComponents plugin;

    /*
    public static final DataComponentType.Valued<@Positive Integer> MAX_DAMAGE = valued("max_damage");
    public static final DataComponentType.Valued<@NonNegative Integer> DAMAGE = valued("damage");
    public static final DataComponentType.Valued<Unbreakable> UNBREAKABLE = valued("unbreakable");
    public static final DataComponentType.Valued<Component> CUSTOM_NAME = valued("custom_name");
    public static final DataComponentType.Valued<Component> ITEM_NAME = valued("item_name");
    public static final DataComponentType.Valued<Key> ITEM_MODEL = valued("item_model");
    public static final DataComponentType.Valued<ItemLore> LORE = valued("lore");
    public static final DataComponentType.Valued<ItemRarity> RARITY = valued("rarity");
    public static final DataComponentType.Valued<ItemEnchantments> ENCHANTMENTS = valued("enchantments");
    public static final DataComponentType.Valued<ItemAdventurePredicate> CAN_PLACE_ON = valued("can_place_on");
    public static final DataComponentType.Valued<ItemAdventurePredicate> CAN_BREAK = valued("can_break");
    public static final DataComponentType.Valued<ItemAttributeModifiers> ATTRIBUTE_MODIFIERS = valued("attribute_modifiers");
    public static final DataComponentType.Valued<CustomModelData> CUSTOM_MODEL_DATA = valued("custom_model_data");
    public static final DataComponentType.NonValued HIDE_ADDITIONAL_TOOLTIP = unvalued("hide_additional_tooltip");
    public static final DataComponentType.NonValued HIDE_TOOLTIP = unvalued("hide_tooltip");
    public static final DataComponentType.Valued<@NonNegative Integer> REPAIR_COST = valued("repair_cost");
    public static final DataComponentType.Valued<Boolean> ENCHANTMENT_GLINT_OVERRIDE = valued("enchantment_glint_override");
    public static final DataComponentType.NonValued INTANGIBLE_PROJECTILE = unvalued("intangible_projectile");
    public static final DataComponentType.Valued<FoodProperties> FOOD = valued("food");
    public static final DataComponentType.Valued<Consumable> CONSUMABLE = valued("consumable");
    public static final DataComponentType.Valued<UseRemainder> USE_REMAINDER = valued("use_remainder");
    public static final DataComponentType.Valued<UseCooldown> USE_COOLDOWN = valued("use_cooldown");
    public static final DataComponentType.Valued<DamageResistant> DAMAGE_RESISTANT = valued("damage_resistant");
    public static final DataComponentType.Valued<Tool> TOOL = valued("tool");
    public static final DataComponentType.Valued<Enchantable> ENCHANTABLE = valued("enchantable");
    public static final DataComponentType.Valued<Equippable> EQUIPPABLE = valued("equippable");
    public static final DataComponentType.Valued<Repairable> REPAIRABLE = valued("repairable");
    public static final DataComponentType.NonValued GLIDER = unvalued("glider");
    public static final DataComponentType.Valued<Key> TOOLTIP_STYLE = valued("tooltip_style");
    public static final DataComponentType.Valued<DeathProtection> DEATH_PROTECTION = valued("death_protection");
    public static final DataComponentType.Valued<ItemEnchantments> STORED_ENCHANTMENTS = valued("stored_enchantments");
    public static final DataComponentType.Valued<DyedItemColor> DYED_COLOR = valued("dyed_color");
    public static final DataComponentType.Valued<MapItemColor> MAP_COLOR = valued("map_color");
    public static final DataComponentType.Valued<MapId> MAP_ID = valued("map_id");
    public static final DataComponentType.Valued<MapDecorations> MAP_DECORATIONS = valued("map_decorations");
    public static final DataComponentType.Valued<MapPostProcessing> MAP_POST_PROCESSING = valued("map_post_processing");
    public static final DataComponentType.Valued<ChargedProjectiles> CHARGED_PROJECTILES = valued("charged_projectiles");
    public static final DataComponentType.Valued<BundleContents> BUNDLE_CONTENTS = valued("bundle_contents");
    public static final DataComponentType.Valued<PotionContents> POTION_CONTENTS = valued("potion_contents");
    public static final DataComponentType.Valued<SuspiciousStewEffects> SUSPICIOUS_STEW_EFFECTS = valued("suspicious_stew_effects");
    public static final DataComponentType.Valued<WritableBookContent> WRITABLE_BOOK_CONTENT = valued("writable_book_content");
    public static final DataComponentType.Valued<WrittenBookContent> WRITTEN_BOOK_CONTENT = valued("written_book_content");
    public static final DataComponentType.Valued<ItemArmorTrim> TRIM = valued("trim");
    public static final DataComponentType.Valued<MusicInstrument> INSTRUMENT = valued("instrument");
    public static final DataComponentType.Valued<OminousBottleAmplifier> OMINOUS_BOTTLE_AMPLIFIER = valued("ominous_bottle_amplifier");
    public static final DataComponentType.Valued<JukeboxPlayable> JUKEBOX_PLAYABLE = valued("jukebox_playable");
    public static final DataComponentType.Valued<List<Key>> RECIPES = valued("recipes");
    public static final DataComponentType.Valued<LodestoneTracker> LODESTONE_TRACKER = valued("lodestone_tracker");
    public static final DataComponentType.Valued<FireworkEffect> FIREWORK_EXPLOSION = valued("firework_explosion");
    public static final DataComponentType.Valued<Fireworks> FIREWORKS = valued("fireworks");
    public static final DataComponentType.Valued<ResolvableProfile> PROFILE = valued("profile");
    public static final DataComponentType.Valued<Key> NOTE_BLOCK_SOUND = valued("note_block_sound");
    public static final DataComponentType.Valued<BannerPatternLayers> BANNER_PATTERNS = valued("banner_patterns");
    public static final DataComponentType.Valued<DyeColor> BASE_COLOR = valued("base_color");
    public static final DataComponentType.Valued<PotDecorations> POT_DECORATIONS = valued("pot_decorations");
    public static final DataComponentType.Valued<ItemContainerContents> CONTAINER = valued("container");
    public static final DataComponentType.Valued<BlockItemDataProperties> BLOCK_DATA = valued("block_state");
    public static final DataComponentType.Valued<SeededContainerLoot> CONTAINER_LOOT = valued("container_loot");
     */

    public void registerCommands() {
        final LifecycleEventManager<@org.jetbrains.annotations.NotNull Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            final LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("itemcomponents")
                    .executes(context -> {
                        context.getSource().getSender().sendRichMessage("<red>Hello world");
                        return Command.SINGLE_SUCCESS;
                    });

            command.then(Commands.literal("maxDamage").then(new IntegerComponentCommand("maxDamage", DataComponentTypes.MAX_DAMAGE, IntegerComponentCommand.IntegerType.POSITIVE).getNode()));
            command.then(Commands.literal("damage").then(new IntegerComponentCommand("damage", DataComponentTypes.DAMAGE, IntegerComponentCommand.IntegerType.NON_NEGATIVE).getNode()));
            command.then(new UnbreakableComponentCommand("unbreakable", DataComponentTypes.UNBREAKABLE).getNode());
            command.then(Commands.literal("customName").then(new TextComponentComponentCommand("customName", DataComponentTypes.CUSTOM_NAME).getNode()));
            command.then(Commands.literal("itemName").then(new TextComponentComponentCommand("itemName", DataComponentTypes.ITEM_NAME).getNode()));
            command.then(Commands.literal("itemModel").then(new KeyComponentCommand("itemModel", DataComponentTypes.ITEM_MODEL).getNode()));
            //skip lore
            command.then(Commands.literal("rarity").then(new EnumComponentCommand<>("rarity", DataComponentTypes.RARITY, ItemRarity.class).getNode()));
            command.then(new EnchantmentsComponentCommand("enchantments", DataComponentTypes.ENCHANTMENTS).getNode());
            command.then(new ItemPredicateComponentCommand("canPlaceOn", DataComponentTypes.CAN_PLACE_ON).getNode());
            command.then(new ItemPredicateComponentCommand("canBreak", DataComponentTypes.CAN_BREAK).getNode());
            //attribute modifiers
            //custom model data
            command.then(new NonValuedComponentCommand("hideAdditionalTooltip", DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP).getNode());
            command.then(new NonValuedComponentCommand("hideTooltip", DataComponentTypes.HIDE_TOOLTIP).getNode());
            command.then(Commands.literal("repairCost").then(new IntegerComponentCommand("repairCost", DataComponentTypes.REPAIR_COST, IntegerComponentCommand.IntegerType.NON_NEGATIVE).getNode()));
            command.then(Commands.literal("enchantmentGlintOverride").then(new BooleanComponentCommand("enchantmentGlintOverride", DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE).getNode()));
            command.then(new NonValuedComponentCommand("intangibleProjectile", DataComponentTypes.INTANGIBLE_PROJECTILE).getNode());
            command.then(new FoodComponentCommand("food", DataComponentTypes.FOOD).getNode());
            //consumable
            //use remainder
            //use cooldown
            //damage resistant
            //tool
            //enchantable
            command.then(new EquippableComponentCommand("equippable", DataComponentTypes.EQUIPPABLE).getNode());
            //repairable
            command.then(new NonValuedComponentCommand("glider", DataComponentTypes.GLIDER).getNode());
            //tooltip style
            //death protection
            command.then(new EnchantmentsComponentCommand("storedEnchantments", DataComponentTypes.STORED_ENCHANTMENTS).getNode());
            //dyeable
            //map color
            //map id
            //map decorations
            command.then(Commands.literal("mapPostProcessing").then(new EnumComponentCommand<>("mapPostProcessing", DataComponentTypes.MAP_POST_PROCESSING, MapPostProcessing.class).getNode()));
            //charged projectiles
            //bundle contents
            //potion contents
            //suspicious steew effects
            //writable book content
            //written book content
            //trim
            //instrument
            //ominous bottle amplifier
            //jukebox playable
            //recipes
            //lodestone tracker
            //firework explosion
            //fireworks
            //profile
            //note block sound
            //banner pattern layers
            command.then(Commands.literal("baseColor").then(new EnumComponentCommand<>("baseColor", DataComponentTypes.BASE_COLOR, DyeColor.class).getNode()));
            //pot decorations
            //container
            //block data
            //container loot

            command.then(new RemoveComponentCommand("remove", plugin).getNode());

            final LiteralCommandNode<CommandSourceStack> commandNode = command.build();

            commands.register(commandNode);
            commands.register(Commands.literal("ie").redirect(commandNode).build());
        });

    }

}
