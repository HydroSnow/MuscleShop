package fr.hydrosnow.muscleshop.critical;

import java.io.IOException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import fr.hydrosnow.muscleshop.propositions.AdminBuyProposition;
import fr.hydrosnow.muscleshop.propositions.AdminSellProposition;
import fr.hydrosnow.muscleshop.propositions.PlayerBuyProposition;
import fr.hydrosnow.muscleshop.propositions.PlayerSellProposition;

public class Critical {
	private final Confirmator confirmator;
	private final EconomyHook economyHook;
	private final MenuManager menuManager;
	private final StockageManager stockageManager;

	public Critical() throws IOException {
		ConfigurationSerialization.registerClass(AdminBuyProposition.class);
		ConfigurationSerialization.registerClass(AdminSellProposition.class);
		ConfigurationSerialization.registerClass(PlayerBuyProposition.class);
		ConfigurationSerialization.registerClass(PlayerSellProposition.class);

		confirmator = new Confirmator();
		economyHook = new EconomyHook();
		menuManager = new MenuManager();
		stockageManager = new StockageManager();

		confirmator.load(economyHook, stockageManager);
		menuManager.load(confirmator, economyHook, stockageManager);
	}

	public Confirmator getConfirmator() {
		return confirmator;
	}

	public EconomyHook getEconomyHook() {
		return economyHook;
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public StockageManager getStockageManager() {
		return stockageManager;
	}
}
