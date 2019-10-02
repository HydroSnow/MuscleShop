package fr.hydrosnow.muscleshop.propositions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class PlayerSellProposition implements Proposition {
	private UUID uuid;
	private OfflinePlayer player;
	private ItemStack itemStack;
	private double price;

	public PlayerSellProposition(final OfflinePlayer p, final ItemStack s, final double d) {
		loadFromReferences(p, s, d);
	}

	public PlayerSellProposition(final Map<String, Object> m) {
		loadFromMap(m);
	}

	private void loadFromReferences(final OfflinePlayer p, final ItemStack s, final double d) {
		uuid = UUID.randomUUID();
		player = p;
		itemStack = s;
		price = d;
		check();
	}

	private void loadFromMap(final Map<String, Object> m) {
		uuid = UUID.fromString((String) m.get("n"));
		player = Bukkit.getOfflinePlayer(UUID.fromString((String) m.get("u")));
		itemStack = (ItemStack) m.get("i");
		price = (Double) m.get("p");
		check();
	}

	private void check() {
		if (uuid == null)
			throw new NullPointerException("UUID passed is null");
		if (player == null)
			throw new NullPointerException("OfflinePlayer passed is null");
		if (itemStack == null)
			throw new NullPointerException("ItemStack passed is null");
		if (price < 0)
			throw new IllegalArgumentException("Price passed is negative");
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> m = new HashMap<>();
		m.put("n", uuid.toString());
		m.put("u", player.getUniqueId().toString());
		m.put("i", itemStack);
		m.put("p", price);
		return m;
	}

	public OfflinePlayer getPlayer() {
		return player;
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
