package fr.hydrosnow.muscleshop.critical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

class MuscleInventory {
	private static final HashMap<OfflinePlayer, MuscleInventory> cache = new HashMap<>();

	public static MuscleInventory getInventory(final OfflinePlayer player) {
		return MuscleInventory.cache.computeIfAbsent(player, (p) -> new MuscleInventory(p));
	}

	private final OfflinePlayer player;
	private final List<MuscleStack> inventory;

	private final File configFile;
	private final FileConfiguration config;

	private MuscleInventory(final OfflinePlayer player) {
		this.player = player;

		new File("plugins/MuscleShop/inventories").mkdirs();
		configFile = new File("plugins/MuscleShop/inventories/" + player.getUniqueId().toString() + ".yml");
		try {
			configFile.createNewFile();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		inventory = new ArrayList<>();
		final List<String> strs = config.getStringList("inventory");
		for (final String ms : strs)
			inventory.add(new MuscleStack(ms));
	}

	private void save() {
		final List<String> strs = new ArrayList<>();
		for (final MuscleStack ms : inventory)
			strs.add(ms.serialize());
		config.set("inventory", strs);

		try {
			config.save(configFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public List<ItemStack> getInventory() {
		final List<ItemStack> list = new ArrayList<>();
		for (final MuscleStack ms : inventory) {
			final List<ItemStack> items = ms.toItemStacks();
			list.addAll(items);
		}
		return list;
	}

	public int count(final ItemStack s) {
		for (final MuscleStack ms : inventory)
			if (ms.equals(s))
				return ms.getAmount();
		return 0;
	}

	public void add(final ItemStack... s) {
		for (final ItemStack sr1 : s) {
			final ItemStack s1 = sr1.clone();
			boolean pass = true;
			for (final MuscleStack ms : inventory)
				if (ms.equals(s1)) {
					ms.add(s1.getAmount());
					pass = false;
					break;
				}
			if (pass)
				inventory.add(new MuscleStack(s1, s1.getAmount()));
		}
		save();
	}

	public void remove(final ItemStack... s) {
		final List<MuscleStack> gc = new ArrayList<>();
		for (final ItemStack sr1 : s) {
			final ItemStack s1 = sr1.clone();
			for (final MuscleStack ms : inventory)
				if (ms.equals(s1)) {
					ms.remove(s1.getAmount());
					if (ms.isEmpty())
						gc.add(ms);
					break;
				}
		}
		inventory.removeAll(gc);
		save();
	}
}
