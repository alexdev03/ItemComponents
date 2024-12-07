package org.alexdev.itemcomponents.commands.types;

import io.papermc.paper.datacomponent.DataComponentType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
public abstract class AbstractComponentCommand<T extends DataComponentType> extends AbstractCommand {

    protected final T type;

    public AbstractComponentCommand(@NotNull String name, @NotNull T type) {
        super(name);
        this.type = type;
    }

}
