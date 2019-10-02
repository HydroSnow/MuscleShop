package fr.hydrosnow.muscleshop.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import fr.hydrosnow.muscleshop.MuscleShop;
import fr.hydrosnow.muscleshop.gui.toolbox.AdditionnalInfo;
import fr.hydrosnow.muscleshop.gui.toolbox.GuiItem;
import fr.hydrosnow.muscleshop.toolbox.SecureSet;

public class GuiManager implements Listener {
	private final SecureSet<Gui> set;

	private final ItemStack blank;
	private final ItemStack previous;
	private final ItemStack next;

	public GuiManager() {
		set = new SecureSet<>();
		Bukkit.getPluginManager().registerEvents(this, MuscleShop.getPlugin());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (final Gui g : set)
					if (g.getViewers().isEmpty())
						set.remove(g);
			}
		}.runTaskTimer(MuscleShop.getPlugin(), 200, 200);

		{
			blank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = blank.getItemMeta();
			m.setDisplayName("-");
			blank.setItemMeta(m);
		}

		{
			previous = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = previous.getItemMeta();
			m.setDisplayName("Page précédente");
			previous.setItemMeta(m);
		}

		{
			next = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = next.getItemMeta();
			m.setDisplayName("Page suivante");
			next.setItemMeta(m);
		}
	}

	@EventHandler
	public synchronized void onInventoryClick(final InventoryClickEvent e) {
		for (final Gui gui : set)
			if (gui.equals(e.getInventory()))
				gui.onInventoryClick(e);
	}

	public void listenGui(final Gui gui) {
		set.add(gui);
	}

	public void openRawGui(final Player player, final String name, final Map<Integer, GuiItem> items,
		final AdditionnalInfo info) {
		new Gui(this, name, items, info).attach(player);
	}

	public void openPagedGui(final Player player, final String name, final List<GuiItem> pagedItems,
		final Map<Integer, GuiItem> options, final int pg, final AdditionnalInfo i) {
		final int pages = Math.max(Math.floorDiv(pagedItems.size() - 1, 45) + 1, 1);

		final int page;
		if ((pg < 0))
			page = 0;
		else if (pg >= pages)
			page = pages - 1;
		else
			page = pg;

		final AdditionnalInfo info;
		if (i == null)
			info = new AdditionnalInfo();
		else
			info = i;

		info.add("page", page);

		final int startIndex = page * 45;
		final int endIndex = Math.min(startIndex + 45, pagedItems.size());

		final Map<Integer, GuiItem> rawItems = new HashMap<>((endIndex - startIndex) + 1, 1);

		for (int b = 45; b < 54; b++)
			rawItems.put(b, new GuiItem(blank, null));

		if (options != null)
			rawItems.putAll(options);

		if (page != 0)
			rawItems.put(45, new GuiItem(previous, (g, p, e) -> openPagedGui(player, name, pagedItems, options, page - 1, info)));

		if ((page + 1) != pages)
			rawItems.put(53, new GuiItem(next, (g, p, e) -> openPagedGui(player, name, pagedItems, options, page + 1, info)));

		for (int a = startIndex; a < endIndex; a++)
			rawItems.put(a % 45, pagedItems.get(a));

		new Gui(this, name + " - " + (page + 1) + "/" + pages, rawItems, info).attach(player);
	}
}
