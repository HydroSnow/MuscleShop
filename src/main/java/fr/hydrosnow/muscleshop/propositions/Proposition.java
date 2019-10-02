package fr.hydrosnow.muscleshop.propositions;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public interface Proposition extends ConfigurationSerializable {
	public ItemStack getItemStack();

	public double getPrice();
}
