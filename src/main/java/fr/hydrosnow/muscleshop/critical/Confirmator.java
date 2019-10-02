package fr.hydrosnow.muscleshop.critical;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import fr.hydrosnow.muscleshop.propositions.AdminBuyProposition;
import fr.hydrosnow.muscleshop.propositions.AdminSellProposition;
import fr.hydrosnow.muscleshop.propositions.PlayerBuyProposition;
import fr.hydrosnow.muscleshop.propositions.PlayerSellProposition;

class Confirmator {
	private EconomyHook economyHook;
	private StockageManager stockageManager;

	public Confirmator() {}

	public void load(final EconomyHook e, final StockageManager s) {
		economyHook = e;
		stockageManager = s;
	}

	public void confirmOne(final PlayerBuyProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final OfflinePlayer owner = proposition.getPlayer();

		if (client.equals(owner)) {
			stockageManager.removeProposition(proposition);
			client.sendMessage("§eTu as retiré ton offre.");
		} else {
			final ItemStack itemStack = proposition.getItemStack();
			itemStack.setAmount(1);
			final double price = proposition.getPrice();

			if (economyHook.balance(client) < price) {
				client.sendMessage("§6Erreur : §eTu n'as pas assez d'argent !");
				return;
			}

			if (client.getInventory().firstEmpty() == -1) {
				client.sendMessage("§6Erreur : §eTu n'as pas assez de place dans ton inventaire !");
				return;
			}

			final MuscleInventory ownerInventory = MuscleInventory.getInventory(owner);

			if (ownerInventory.count(itemStack) == 0) {
				client.sendMessage("§6Erreur : §eLe joueur n'a pas les ressources nécessaires.");
				return;
			}



			client.getInventory().addItem(itemStack);
			ownerInventory.remove(itemStack);
			economyHook.transfer(client, owner, price);

			client.sendMessage("§eTu as acheté §61 " + itemStack.getType().toString() + "§e à §6" + owner.getName() + "§e pour §6"
				+ economyHook.ts(price) + "§e.");
		}
	}

	public void confirmOne(final PlayerSellProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final OfflinePlayer owner = proposition.getPlayer();

		if (client.equals(owner)) {
			stockageManager.removeProposition(proposition);
			client.sendMessage("§eTu as retiré ton offre.");
		} else {
			final ItemStack itemStack = proposition.getItemStack();
			itemStack.setAmount(1);
			final double price = proposition.getPrice();

			if (economyHook.balance(owner) < price) {
				client.sendMessage("§6Erreur : §eLe joueur n'a pas assez d'argent !");
				return;
			}

			final int count = count(client.getInventory(), itemStack, null);

			if (count == 0) {
				client.sendMessage("§6Erreur : §eTu n'as rien à vendre !");
				return;
			}

			final MuscleInventory ownerInventory = MuscleInventory.getInventory(owner);

			client.getInventory().removeItem(itemStack);
			ownerInventory.add(itemStack);
			economyHook.transfer(owner, client, price);

			client.sendMessage("§eTu as vendu §61 " + itemStack.getType().toString() + "§e à §6" + owner.getName() + "§e pour §6"
				+ economyHook.ts(price) + "§e.");
		}
	}

	public void confirmOne(final AdminBuyProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final ItemStack itemStack = proposition.getItemStack();
		itemStack.setAmount(1);
		final double price = proposition.getPrice();

		if (economyHook.balance(client) < proposition.getPrice()) {
			client.sendMessage("§6Erreur : §eTu n'as pas assez d'argent !");
			return;
		}

		if (client.getInventory().firstEmpty() == -1) {
			client.sendMessage("§6Erreur : §eTu n'as pas assez de place dans ton inventaire !");
			return;
		}

		client.getInventory().addItem(itemStack);
		economyHook.withdraw(client, price);

		client.sendMessage("§eTu as acheté §61 " + itemStack.getType().toString() + "§e pour §6" + economyHook.ts(price) + "§e.");
	}

	public void confirmOne(final AdminSellProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final ItemStack itemStack = proposition.getItemStack();
		itemStack.setAmount(1);
		final double price = proposition.getPrice();

		final int count = count(client.getInventory(), itemStack, null);

		if (count == 0) {
			client.sendMessage("§6Erreur : §eTu n'as rien à vendre !");
			return;
		}

		client.getInventory().removeItem(itemStack);
		economyHook.deposit(client, price);

		client.sendMessage("§eTu as vendu §61 " + itemStack.getType().toString() + "§e pour §6" + economyHook.ts(price) + "§e.");
	}

	public void confirmStack(final PlayerBuyProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final OfflinePlayer owner = proposition.getPlayer();

		if (client.equals(owner)) {
			stockageManager.removeProposition(proposition);
			client.sendMessage("§eTu as retiré ton offre.");
		} else {
			final ItemStack itemStack = proposition.getItemStack();
			int amount = itemStack.getAmount();
			double price = proposition.getPrice() * amount;

			if (client.getInventory().firstEmpty() == -1) {
				client.sendMessage("§6Erreur : §eTu n'as pas assez de place dans ton inventaire !");
				return;
			}

			final MuscleInventory ownerInventory = MuscleInventory.getInventory(owner);
			final int count = ownerInventory.count(itemStack);

			if (count == 0) {
				client.sendMessage("§6Erreur : §eLe joueur n'a pas les ressources nécessaires.");
				return;
			} else if (count < amount) {
				amount = count;
				itemStack.setAmount(amount);
				price = proposition.getPrice() * amount;
			}

			if (economyHook.balance(client) < price) {
				client.sendMessage("§6Erreur : §eTu n'as pas assez d'argent !");
				return;
			}

			client.getInventory().addItem(itemStack);
			ownerInventory.remove(itemStack);
			economyHook.transfer(client, owner, price);

			client.sendMessage("§eTu as acheté §6" + amount + " " + itemStack.getType().toString() + "§e à §6" + owner.getName()
				+ "§e pour §6" + economyHook.ts(price) + "§e.");
		}
	}

	public void confirmStack(final PlayerSellProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final OfflinePlayer owner = proposition.getPlayer();

		if (client.equals(owner)) {
			stockageManager.removeProposition(proposition);
			client.sendMessage("§eTu as retiré ton offre.");
		} else {
			final ItemStack itemStack = proposition.getItemStack();
			int amount = itemStack.getAmount();
			double price = proposition.getPrice() * amount;

			final int count = count(client.getInventory(), itemStack, null);

			if (count == 0) {
				client.sendMessage("§6Erreur : §eTu n'as rien à vendre !");
				return;
			} else if (count < amount) {
				amount = count;
				itemStack.setAmount(amount);
				price = proposition.getPrice() * amount;
			}

			if (economyHook.balance(owner) < price) {
				client.sendMessage("§6Erreur : §eLe joueur n'a pas assez d'argent !");
				return;
			}

			final MuscleInventory ownerInventory = MuscleInventory.getInventory(owner);

			client.getInventory().removeItem(itemStack);
			ownerInventory.add(itemStack);
			economyHook.transfer(owner, client, price);

			client.sendMessage("§eTu as vendu §6" + amount + " " + itemStack.getType().toString() + "§e à §6" + owner.getName()
				+ "§e pour §6" + economyHook.ts(price) + "§e.");
		}
	}

	public void confirmStack(final AdminBuyProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final ItemStack itemStack = proposition.getItemStack();
		final int amount = itemStack.getAmount();
		final double price = proposition.getPrice() * amount;

		if (economyHook.balance(client) < proposition.getPrice()) {
			client.sendMessage("§6Erreur : §eTu n'as pas assez d'argent !");
			return;
		}

		if (client.getInventory().firstEmpty() == -1) {
			client.sendMessage("§6Erreur : §eTu n'as pas assez de place dans ton inventaire !");
			return;
		}

		client.getInventory().addItem(itemStack);
		economyHook.withdraw(client, price);

		client.sendMessage(
			"§eTu as acheté §6" + amount + " " + itemStack.getType().toString() + "§e pour §6" + economyHook.ts(price) + "§e.");
	}

	public void confirmStack(final AdminSellProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final ItemStack itemStack = proposition.getItemStack();
		int amount = itemStack.getAmount();
		double price = proposition.getPrice() * amount;

		final int count = count(client.getInventory(), itemStack, null);

		if (count == 0) {
			client.sendMessage("§6Erreur : §eTu n'as rien à vendre !");
			return;
		} else if (count < amount) {
			amount = count;
			itemStack.setAmount(amount);
			price = proposition.getPrice() * amount;
		}

		client.getInventory().removeItem(itemStack);
		economyHook.deposit(client, price);

		client.sendMessage(
			"§eTu as vendu §6" + amount + " " + itemStack.getType().toString() + "§e pour §6" + economyHook.ts(price) + "§e.");
	}

	public void confirmAll(final PlayerSellProposition proposition, final Player client) {
		if (!stockageManager.getPropositions().contains(proposition)) {
			client.sendMessage("§6Erreur : §eL'item n'existe pas ou plus !");
			return;
		}

		final OfflinePlayer owner = proposition.getPlayer();

		if (client.equals(owner)) {
			stockageManager.removeProposition(proposition);
			client.sendMessage("§eTu as retiré ton offre.");
		} else {
			final ItemStack itemStack = proposition.getItemStack();

			final List<ItemStack> stacks = new ArrayList<>();
			final int count = count(client.getInventory(), itemStack, stacks);

			if (count == 0) {
				client.sendMessage("§6Erreur : §eTu n'as rien à vendre !");
				return;
			}

			final int amount = count;
			itemStack.setAmount(amount);
			final double price = proposition.getPrice() * amount;

			if (economyHook.balance(owner) < price) {
				client.sendMessage("§6Erreur : §eLe joueur n'a pas assez d'argent !");
				return;
			}

			final MuscleInventory ownerInventory = MuscleInventory.getInventory(owner);

			for (final ItemStack s : stacks)
				client.getInventory().removeItem(s);
			ownerInventory.add(stacks.toArray(new ItemStack[stacks.size()]));
			economyHook.transfer(owner, client, price);

			client.sendMessage("§eTu as vendu §6" + amount + " " + itemStack.getType().toString() + "§e à §6" + owner.getName()
				+ "§e pour §6" + economyHook.ts(price) + "§e.");
		}
	}

	public void confirmAll(final AdminSellProposition proposition, final Player client) {
		final ItemStack itemStack = proposition.getItemStack();

		final List<ItemStack> stacks = new ArrayList<>();
		final int count = count(client.getInventory(), itemStack, stacks);

		if (count == 0) {
			client.sendMessage("§6Erreur : §eTu n'as rien à vendre !");
			return;
		}

		final int amount = count;
		itemStack.setAmount(amount);
		final double price = proposition.getPrice() * amount;

		for (final ItemStack s : stacks)
			client.getInventory().removeItem(s);
		economyHook.deposit(client, price);

		client.sendMessage(
			"§eTu as vendu §6" + amount + " " + itemStack.getType().toString() + "§e pour §6" + economyHook.ts(price) + "§e.");
	}

	private int count(final Inventory i, final ItemStack is, final List<ItemStack> stacks) {
		int count = 0;
		if (stacks != null) {
			for (final ItemStack s : i)
				if (is.isSimilar(s)) {
					stacks.add(s);
					count += s.getAmount();
				}
		} else
			for (final ItemStack s : i)
				if (is.isSimilar(s))
					count += s.getAmount();
		return count;
	}
}
