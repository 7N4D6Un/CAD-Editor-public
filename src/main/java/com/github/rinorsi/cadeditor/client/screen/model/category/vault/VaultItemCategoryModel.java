package com.github.rinorsi.cadeditor.client.screen.model.category.vault;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.Vault;
import com.github.rinorsi.cadeditor.client.screen.model.VaultScreenModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.vault.VaultItemEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VaultItemCategoryModel extends VaultCategoryModel {
    public VaultItemCategoryModel(VaultScreenModel parent) {
        super(ModTexts.ITEM, parent);
    }

    @Override
    protected void setupEntries() {
        Vault.getInstance().getItems().stream()
                .map(tag -> ClientUtil.parseItemStack(ClientUtil.registryAccess(), tag))
                .forEach(itemStack -> getEntries().add(new VaultItemEntryModel(this, itemStack)));
    }

    @Override
    public int getEntryListStart() {
        return 0;
    }

    @Override
    public EntryModel createNewListEntry() {
        return new VaultItemEntryModel(this, new ItemStack(Items.STONE, 1));
    }

    @Override
    protected MutableComponent getAddListEntryButtonTooltip() {
        return ModTexts.ITEM;
    }
}
