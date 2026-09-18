package com.vunilly.vanillie.biome;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.vunilly.vanillie.Vanillie;
import com.vunilly.vanillie.utils.Lang;

import io.papermc.paper.datacomponent.DataComponentTypes;

public class BiomeWand {
    private final float customModelData;
    private final NamespacedKey recipeKey;
    private final NamespacedKey itemKey;

    public BiomeWand() {
        this.customModelData = 1001.0f;
        this.recipeKey = new NamespacedKey(JavaPlugin.getPlugin(Vanillie.class), "vanillie.biomewand");
        this.itemKey = new NamespacedKey(JavaPlugin.getPlugin(Vanillie.class), "is_biome_wand");
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.DEBUG_STICK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.itemName(Lang.get("items.biomewand.name").getFirst());
            meta.lore(Lang.get("items.biomewand.desc"));
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.BOOLEAN, true);
            CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            cmd.setFloats(List.of(customModelData));
            meta.setCustomModelDataComponent(cmd);
            item.setItemMeta(meta);
            item.setData(DataComponentTypes.MAX_DAMAGE, 512);
            item.setData(DataComponentTypes.DAMAGE, 0);
        }
        return item;
    }

    public ShapedRecipe getRecipe(Vanillie plugin) {
        NamespacedKey key = this.recipeKey;

        ShapedRecipe recipe = new ShapedRecipe(key, createItem());

        recipe.shape(
            "  A",
            " D ",
            "S  "
        );

        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('A', Material.AMETHYST_SHARD);

        return recipe;
    }

    public boolean isWand(ItemStack item) {
        if (item == null || item.getType() != Material.DEBUG_STICK || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(itemKey, PersistentDataType.BOOLEAN);
    }
}