package org.copycraftDev.new_horizons.core.misc;

import nazario.liby.api.registry.auto.LibyAutoRegister;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.NewHorizonsMain;
import org.copycraftDev.new_horizons.core.items.ModItems;

@LibyAutoRegister(method = "registerItemGroups")
public class ModItemGroup {
    // 1) Identifier for both build() and Registry.register(...)
    private static final Identifier NEW_HORIZONS_ID =
            Identifier.of(NewHorizonsMain.MOD_ID, "new_horizons_group");

    // 2) Build the group (icon + display name + entries)
    public static final ItemGroup NEW_HORIZONS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.FLASHLIGHT_ITEM))
            .displayName(Text.translatable("itemGroup.new_horizons.new_horizons_group"))
            .entries((context, entries) -> {
                // add as many items as you like here:
                entries.add(ModItems.FLASHLIGHT_ITEM);

                // entries.add(ModItems.OTHER_ITEM);
            })
            .build();

    // 3) This is the method that LibyAutoRegister will invoke for you
    public static void registerItemGroups() {
        Registry.register(
                Registries.ITEM_GROUP,
                NEW_HORIZONS_ID,
                NEW_HORIZONS_GROUP
        );
    }
}
