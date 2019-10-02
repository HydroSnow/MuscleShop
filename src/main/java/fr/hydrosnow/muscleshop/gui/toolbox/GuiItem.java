package fr.hydrosnow.muscleshop.gui.toolbox;

import org.bukkit.inventory.ItemStack;

public class GuiItem {
	private final ItemStack itemStack;
	private final GuiAction action;

	public GuiItem(final ItemStack i, final GuiAction a) {
		itemStack = i;
		action = a;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public GuiAction getAction() {
		return action;
	}
}
