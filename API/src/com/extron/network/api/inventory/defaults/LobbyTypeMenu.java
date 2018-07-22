package com.extron.network.api.inventory.defaults;

import com.extron.network.api.collection.*;
import com.extron.network.api.inventory.*;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.function.Supplier;

public class LobbyTypeMenu<T extends Category> extends SingleCategoryMenu<T> {
    public LobbyTypeMenu(T category, Supplier<InventoryMenu> back) {
        super(category, back);
    }

    @Override
    protected boolean useSmartGrid() {
        return true;
    }

    @Override
    public void init() {
        super.init();
        if (this.category instanceof CollectibleType) {
            createButton(49,Material.GLASS)
                    .setDamage(14)
                    .setLore(ItemLore.create().empty().description("Click to deselect the current " + category.getName().toLowerCase() + "!"))
                    .setDisplayName(ChatColor.RED + "Deselect")
                    .setAction((e)->((CollectibleType)category).deselect(owner));
        }
    }

    @Override
    protected Button createItemButton(CategoryItem item) {
        if (item instanceof LobbyCollectible) {
            LobbyCollectible c = (LobbyCollectible)item;
            return new SimpleButton(c.getIcon())
                    .setDamage(c.getIconDamage())
                    .setDisplayName((owner.foundCollectible(c) ? ChatColor.GREEN : ChatColor.RED) + c.getDisplayName())
                    .setLore(ItemLore.description(c).empty()
                            .line("Rarity: " + c.getRarity().getColor() + TextUtils.capitalize(c.getRarity().name().toLowerCase()))
                            .empty()
                            .conditioned(owner.foundCollectible(c) && owner.getSelectedCollectible(c.getType()) != c,ChatColor.YELLOW + "Click to select!")
                            .conditioned(owner.foundCollectible(c) && owner.getSelectedCollectible(c.getType()) == c,ChatColor.YELLOW + "Click to deselect!")
                            .conditioned(!owner.foundCollectible(c),ChatColor.RED + "You have'nt found this " + category.getName().toLowerCase() + " yet!"))
                    .setAction((e)->c.getType().menuClick((Collectible) item,e.getPlayer(),e));
        }
        return new ErrorButton();
    }

    @Override
    protected Button createSubCategoryButton(Category item) {
        if (item instanceof LobbyCollectible) {
            LobbyCollectible c = (LobbyCollectible)item;
            return new SimpleButton(c.getIcon())
                    .setDamage(c.getIconDamage())
                    .setDisplayName((owner.getCollection().getFoundOfCategory(item).size() == item.getAll().size() ? ChatColor.GREEN : ChatColor.RED) + c.getDisplayName())
                    .setLore(ItemLore.create().empty().found(owner.getCollection().getFoundOfCategory(item).size(),item.getAll().size()).empty().clickTo("browse"))
                    .setAction((e)->e.getPlayer().openInventory(new LobbyTypeMenu<>(item,()->new LobbyTypeMenu<>(category,back))));
        }
        return new ErrorButton();
    }

    @Override
    public String getTitle() {
        return TextUtils.pluralize(category.getName(),true);
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public void onClose() {

    }
}
