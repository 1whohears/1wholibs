package com.onewhohears.onewholibs.item.forge;

import com.onewhohears.onewholibs.item.TestItem;
import com.onewhohears.onewholibs.util.forge.UtilItemImpl;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TestItemImpl {

    public static TestItem create() {
        return new TestItem() {
            @Override
            public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
                UtilItemImpl.onObjModelItemInitClient(consumer);
            }
        };
    }

}
