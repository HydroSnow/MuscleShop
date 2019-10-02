package fr.hydrosnow.muscleshop.gui.toolbox;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import fr.hydrosnow.muscleshop.gui.Gui;

@FunctionalInterface
public interface GuiAction {
	public void apply(final Gui g, final Player p, final InventoryClickEvent e);
}
