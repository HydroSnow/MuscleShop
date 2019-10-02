package fr.hydrosnow.muscleshop.propositions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class AdminSellProposition implements Proposition {
	private UUID uuid;
	private ItemStack itemStack;
	private double price;

	public AdminSellProposition(final ItemStack s, final double p) {
		loadFromReferences(s, p);
	}

	public AdminSellProposition(final Map<String, Object> m) {
		loadFromMap(m);
	}

	private void loadFromReferences(final ItemStack s, final double p) {
		uuid = UUID.randomUUID();
		itemStack = s;
		price = p;
		check();
	}

	private void loadFromMap(final Map<String, Object> m) {
		uuid = UUID.fromString((String) m.get("n"));
		itemStack = (ItemStack) m.get("i");
		price = (Double) m.get("p");
		check();
	}

	private void check() {
		if (uuid == null)
			throw new NullPointerException("UUID passed is null");
		if (itemStack == null)
			throw new NullPointerException("ItemStack passed is null");
		if (price < 0)
			throw new IllegalArgumentException("Price passed is negative");
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> m = new HashMap<>();
		m.put("n", uuid.toString());
		m.put("i", itemStack);
		m.put("p", price);
		return m;
	}

	@Override
	public ItemStack getItemStack() {
		return itemStack.clone();
	}

	@Override
	public double getPrice() {
		return price;
	}
}
