package fr.hydrosnow.muscleshop.critical;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import net.milkbowl.vault.economy.Economy;

class EconomyHook {
	private final Economy economy;

	public EconomyHook() {
		economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
	}

	public String ts(final double a) {
		final double r = Math.round(a * 100.0d) / 100.0d;
		final int lr = (int) Math.round(r);
		return (r == lr) ? (Integer.toString(lr)) : (Double.toString(r));
	}

	public void deposit(final OfflinePlayer p, final double m) {
		economy.depositPlayer(p, m);
	}

	public void withdraw(final OfflinePlayer p, final double m) {
		economy.withdrawPlayer(p, m);
	}

	public void transfer(final OfflinePlayer p1, final OfflinePlayer p2, final double m) {
		withdraw(p1, m);
		deposit(p2, m);
	}

	public double balance(final OfflinePlayer p) {
		return economy.getBalance(p);
	}
}
