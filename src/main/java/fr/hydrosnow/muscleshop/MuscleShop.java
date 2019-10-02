package fr.hydrosnow.muscleshop;

import java.io.IOException;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import fr.hydrosnow.muscleshop.critical.Critical;
import fr.hydrosnow.muscleshop.gui.GuiManager;
import fr.hydrosnow.muscleshop.propositions.AdminBuyProposition;
import fr.hydrosnow.muscleshop.propositions.AdminSellProposition;
import fr.hydrosnow.muscleshop.propositions.PlayerBuyProposition;
import fr.hydrosnow.muscleshop.propositions.PlayerSellProposition;

public class MuscleShop extends JavaPlugin {
	private static MuscleShop instance = null;

	public static MuscleShop getPlugin() {
		if (MuscleShop.instance == null)
			throw new IllegalStateException("The plugin is not loaded!");
		return MuscleShop.instance;
	}

	private GuiManager guiManager;
	private Critical criticalSection;
	private boolean enabled;

	public MuscleShop() {}

	@Override
	public void onEnable() {
		MuscleShop.instance = this;
		try {
			criticalSection = new Critical();
			guiManager = new GuiManager();
			enabled = true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (cmd.getName().equals("muscleshop")) {
			if (!enabled) {
				sender.sendMessage("§6Erreur : §eLe plugin est en maintenance.");
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage("§6Erreur : §eVous devez être un joueur pour ouvrir le shop.");
				return true;
			}

			final Player player = (Player) sender;

			if (args.length == 0) {
				if (!sender.hasPermission("muscleshop.use")) {
					sender.sendMessage("§6Erreur : §eVous n'avez pas la permission.");
					return true;
				}

				criticalSection.getMenuManager().openMenu(player, 0);
				return true;
			}

			if ((args.length == 2))
				if (args[0].equalsIgnoreCase("pb")) {
					if (!sender.hasPermission("muscleshop.shop")) {
						sender.sendMessage("§6Erreur : §eVous n'avez pas la permission.");
						return true;
					}

					try {
						final PlayerInventory playerInventory = player.getInventory();
						final ItemStack itemStack = playerInventory.getItemInMainHand();
						if ((itemStack != null) && (itemStack.getAmount() != 0) && !itemStack.getType().equals(Material.AIR)) {
							final double price = Double.parseDouble(args[1]) / itemStack.getAmount();
							final PlayerBuyProposition sp = new PlayerBuyProposition(player, itemStack, price);
							criticalSection.getStockageManager().addProposition(sp);
							sender.sendMessage("§eVotre item a été ajouté à l'hôtel des ventes.");
						} else
							sender.sendMessage("§6Erreur : §eVous devez tenir un item dans votre main pour l'ajouter.");
					} catch (final NumberFormatException e) {
						sender.sendMessage("§6Erreur : §eLe prix entré doit être un nombre.");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("ps")) {
					if (!sender.hasPermission("muscleshop.shop")) {
						sender.sendMessage("§6Erreur : §eVous n'avez pas la permission.");
						return true;
					}

					try {
						final PlayerInventory playerInventory = player.getInventory();
						final ItemStack itemStack = playerInventory.getItemInMainHand();
						if ((itemStack != null) && (itemStack.getAmount() != 0) && !itemStack.getType().equals(Material.AIR)) {
							final double price = Double.parseDouble(args[1]) / itemStack.getAmount();
							final PlayerSellProposition sp = new PlayerSellProposition(player, itemStack, price);
							criticalSection.getStockageManager().addProposition(sp);
							sender.sendMessage("§eVotre item a été ajouté à l'hôtel des ventes.");
						} else
							sender.sendMessage("§6Erreur : §eVous devez tenir un item dans votre main pour l'ajouter.");
					} catch (final NumberFormatException e) {
						sender.sendMessage("§6Erreur : §eLe prix entré doit être un nombre.");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("ab")) {
					if (!sender.hasPermission("muscleshop.adminshop")) {
						sender.sendMessage("§6Erreur : §eVous n'avez pas la permission.");
						return true;
					}

					try {
						final PlayerInventory playerInventory = player.getInventory();
						final ItemStack itemStack = playerInventory.getItemInMainHand();
						if ((itemStack != null) && (itemStack.getAmount() != 0) && !itemStack.getType().equals(Material.AIR)) {
							final double price = Double.parseDouble(args[1]) / itemStack.getAmount();
							final AdminBuyProposition sp = new AdminBuyProposition(itemStack, price);
							criticalSection.getStockageManager().addProposition(sp);
							sender.sendMessage("§eVotre item a été ajouté à l'hôtel des ventes.");
						} else
							sender.sendMessage("§6Erreur : §eVous devez tenir un item dans votre main pour l'ajouter.");
					} catch (final NumberFormatException e) {
						sender.sendMessage("§6Erreur : §eLe prix entré doit être un nombre.");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("as")) {
					if (!sender.hasPermission("muscleshop.adminshop")) {
						sender.sendMessage("§6Erreur : §eVous n'avez pas la permission.");
						return true;
					}

					try {
						final PlayerInventory playerInventory = player.getInventory();
						final ItemStack itemStack = playerInventory.getItemInMainHand();
						if ((itemStack != null) && (itemStack.getAmount() != 0) && !itemStack.getType().equals(Material.AIR)) {
							final double price = Double.parseDouble(args[1]) / itemStack.getAmount();
							final AdminSellProposition sp = new AdminSellProposition(itemStack, price);
							criticalSection.getStockageManager().addProposition(sp);
							sender.sendMessage("§eVotre item a été ajouté à l'hôtel des ventes.");
						} else
							sender.sendMessage("§6Erreur : §eVous devez tenir un item dans votre main pour l'ajouter.");
					} catch (final NumberFormatException e) {
						sender.sendMessage("§6Erreur : §eLe prix entré doit être un nombre.");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("disable")) {
					if (!sender.isOp()) {
						sender.sendMessage("§6Erreur : §eVous n'avez pas la permission.");
						return true;
					}

					enabled = false;
					sender.sendMessage("§ePlugin désactivé.");
					return true;
				} else if (args[0].equalsIgnoreCase("ensable")) {
					if (!sender.isOp()) {
						sender.sendMessage("§6Erreur : §eVous n'avez pas la permission.");
						return true;
					}

					enabled = true;
					sender.sendMessage("§ePlugin activé.");
					return true;
				}

			sender.sendMessage("§6Erreur : §eSyntaxe incorrecte.");
			return true;
		}

		return false;
	}

	@Override
	public void onDisable() {
		enabled = false;
		MuscleShop.instance = null;
	}

	public Critical getCriticalSection() {
		return criticalSection;
	}

	public GuiManager getGuiManager() {
		return guiManager;
	}
}
