package fr.hydrosnow.muscleshop.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import fr.hydrosnow.muscleshop.gui.toolbox.AdditionnalInfo;
import fr.hydrosnow.muscleshop.gui.toolbox.GuiAction;
import fr.hydrosnow.muscleshop.gui.toolbox.GuiItem;

public class Gui {
	private final GuiManager manager;
	private final Map<Integer, GuiAction> actions;
	private final Inventory inventory;
	private final AdditionnalInfo info;

	public Gui(final GuiManager manager, final String name, final Map<Integer, GuiItem> items, final AdditionnalInfo info) {
		this.manager = manager;
		this.info = info;
		inventory = Bukkit.createInventory(null, 54, name);
		actions = new HashMap<>(55, 1);

		for (final Entry<Integer, GuiItem> i : items.entrySet()) {
			final int s = i.getKey();
			if ((0 > s) || (s >= 54))
				continue;
			final GuiItem gi = i.getValue();
			inventory.setItem(s, gi.getItemStack());
			actions.put(s, gi.getAction());
		}
	}

	public void attach(final Player p) {
		manager.listenGui(this);
		p.openInventory(inventory);
	}

	public List<HumanEntity> getViewers() {
		return inventory.getViewers();
	}

	public boolean equals(final Inventory i) {
		return inventory.equals(i);
	}

	public AdditionnalInfo getAdditionnalInfo() {
		return info;
	}

	public void onInventoryClick(final InventoryClickEvent ev) {
		try {
			GuiAction sInvAction = null;
			if (info != null)
				sInvAction = info.get("other_action");
			if (ev.getClickedInventory() == ev.getInventory()) {
				final GuiAction a = actions.get(ev.getSlot());
				if (a != null) {
					final HumanEntity who = ev.getWhoClicked();
					if (who instanceof Player)
						a.apply(this, (Player) who, ev);
				}
			} else if (sInvAction != null) {
				final HumanEntity who = ev.getWhoClicked();
				if (who instanceof Player)
					sInvAction.apply(this, (Player) who, ev);
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		ev.setCancelled(true);
	}
}
