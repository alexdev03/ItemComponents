package org.alexdev.itemcomponents.commands.types;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage"})
public abstract class AbstractCommand {

    protected final String name;

    public AbstractCommand(@NotNull String name) {
        this.name = name;
    }

    public boolean isInvalidItem(@NotNull CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getSender() instanceof Player player)) {
            command.getSource().getSender().sendRichMessage("<red>This command can only be used by players");
            return true;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            command.getSource().getSender().sendRichMessage("<red>You must hold an item in your main hand");
            return true;
        }

        return false;
    }

    public ItemStack getItem(@NotNull CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getSender() instanceof Player player)) {
            throw new IllegalStateException("This command can only be used by players");
        }

        return player.getInventory().getItemInMainHand();
    }

    public abstract CommandNode<CommandSourceStack> getNode();
}
