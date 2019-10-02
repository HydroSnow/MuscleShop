package fr.hydrosnow.muscleshop.critical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import fr.hydrosnow.muscleshop.MuscleShop;
import fr.hydrosnow.muscleshop.gui.toolbox.AdditionnalInfo;
import fr.hydrosnow.muscleshop.gui.toolbox.GuiAction;
import fr.hydrosnow.muscleshop.gui.toolbox.GuiItem;
import fr.hydrosnow.muscleshop.propositions.AdminBuyProposition;
import fr.hydrosnow.muscleshop.propositions.AdminSellProposition;
import fr.hydrosnow.muscleshop.propositions.PlayerBuyProposition;
import fr.hydrosnow.muscleshop.propositions.PlayerSellProposition;
import fr.hydrosnow.muscleshop.propositions.Proposition;

public class MenuManager {
	private final ItemStack back;
	private final ItemStack categories;
	private final ItemStack inventory;
	private final ItemStack notSorted;
	private final ItemStack idSorted;
	private final ItemStack priceSorted;
	private final ItemStack notFiltered;
	private final ItemStack buyFiltered;
	private final ItemStack sellFiltered;

	private Confirmator confirmator;
	private EconomyHook economyHook;
	private StockageManager stockageManager;

	public MenuManager() {
		{
			back = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = back.getItemMeta();
			m.setDisplayName("§cRetour");
			back.setItemMeta(m);
		}

		{
			categories = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = categories.getItemMeta();
			m.setDisplayName("§eRechercher un item");
			categories.setItemMeta(m);
		}

		{
			inventory = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = inventory.getItemMeta();
			m.setDisplayName("§eInventaire");
			inventory.setItemMeta(m);
		}

		{
			notSorted = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = notSorted.getItemMeta();
			m.setDisplayName("§eTri : §aAucun");
			notSorted.setItemMeta(m);
		}

		{
			idSorted = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = idSorted.getItemMeta();
			m.setDisplayName("§eTri : §6Identifiant");
			idSorted.setItemMeta(m);
		}

		{
			priceSorted = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = priceSorted.getItemMeta();
			m.setDisplayName("§eTri : §6Prix unité");
			priceSorted.setItemMeta(m);
		}

		{
			notFiltered = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = notFiltered.getItemMeta();
			m.setDisplayName("§eAffichage : §dAchat et Vente");
			notFiltered.setItemMeta(m);
		}

		{
			buyFiltered = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = buyFiltered.getItemMeta();
			m.setDisplayName("§eAffichage : §9Achat");
			buyFiltered.setItemMeta(m);
		}

		{
			sellFiltered = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
			final ItemMeta m = sellFiltered.getItemMeta();
			m.setDisplayName("§eAffichage : §cVente");
			sellFiltered.setItemMeta(m);
		}
	}

	public void load(final Confirmator c, final EconomyHook e, final StockageManager s) {
		confirmator = c;
		economyHook = e;
		stockageManager = s;
	}

	private List<GuiItem> transform(final List<Proposition> propositions, final Player player, final PageRefresh refresh) {
		final List<GuiItem> items = new ArrayList<>();
		for (final Proposition prop : propositions)
			if (prop instanceof PlayerBuyProposition) {
				final PlayerBuyProposition proposition = (PlayerBuyProposition) prop;
				final GuiItem item = showcase(proposition, player, refresh);
				items.add(item);
			} else if (prop instanceof PlayerSellProposition) {
				final PlayerSellProposition proposition = (PlayerSellProposition) prop;
				final GuiItem item = showcase(proposition, player, refresh);
				items.add(item);
			} else if (prop instanceof AdminBuyProposition) {
				final AdminBuyProposition proposition = (AdminBuyProposition) prop;
				final GuiItem item = showcase(proposition, player, refresh);
				items.add(item);
			} else if (prop instanceof AdminSellProposition) {
				final AdminSellProposition proposition = (AdminSellProposition) prop;
				final GuiItem item = showcase(proposition, player, refresh);
				items.add(item);
			}
		return items;
	}

	private GuiItem showcase(final PlayerBuyProposition proposition, final Player player, final PageRefresh refresh) {
		final double price = proposition.getPrice();
		final ItemStack itemStack = proposition.getItemStack();
		final int amount = itemStack.getAmount();

		final ItemMeta itemMeta = itemStack.getItemMeta();
		if (((OfflinePlayer) player).equals(proposition.getPlayer())) {
			if (amount == 1)
				itemMeta.setLore(Arrays
					.asList(new String[] {"§aVendeur : §evous", "§aPrix : §e" + economyHook.ts(price), "§6Clic pour §bretirer"}));
			else
				itemMeta.setLore(Arrays.asList(new String[] {
					"§aVendeur : §evous",
					"§aPrix : §e" + economyHook.ts(price * amount),
					"§aPrix unité : §e" + economyHook.ts(price),
					"§6Clic pour §bretirer"}));
		} else if (amount == 1)
			itemMeta.setLore(Arrays.asList(new String[] {
				"§aVendeur : §e" + proposition.getPlayer().getName(),
				"§aPrix : §e" + economyHook.ts(price),
				"§6Clic pour §bacheter"}));
		else
			itemMeta.setLore(Arrays.asList(new String[] {
				"§aVendeur : §e" + proposition.getPlayer().getName(),
				"§aPrix : §e" + economyHook.ts(price * amount),
				"§aPrix unité : §e" + economyHook.ts(price),
				"§6Clic gauche pour §bacheter " + amount,
				"§6Clic droit pour §bacheter 1"}));
		itemStack.setItemMeta(itemMeta);

		final GuiItem item = new GuiItem(itemStack, (g, p, e) -> {
			switch (e.getClick()) {
				case LEFT:
					confirmator.confirmStack(proposition, p);
					break;
				case RIGHT:
					confirmator.confirmOne(proposition, p);
					break;
				default:
					break;
			}
			refresh.apply(g.getAdditionnalInfo().get("page"));
		});

		return item;
	}

	private GuiItem showcase(final PlayerSellProposition proposition, final Player player, final PageRefresh refresh) {
		final double price = proposition.getPrice();
		final ItemStack itemStack = proposition.getItemStack();
		final int amount = itemStack.getAmount();

		final ItemMeta itemMeta = itemStack.getItemMeta();
		if (((OfflinePlayer) player).equals(proposition.getPlayer())) {
			if (amount == 1)
				itemMeta.setLore(Arrays.asList(
					new String[] {"§aAcheteur : §evous", "§aPrix : §e" + economyHook.ts(price), "§6Clic pour §bretirer"}));
			else
				itemMeta.setLore(Arrays.asList(new String[] {
					"§aAcheteur : §evous",
					"§aPrix : §e" + economyHook.ts(price * amount),
					"§aPrix unité : §e" + economyHook.ts(price),
					"§6Clic pour §bretirer"}));
		} else if (amount == 1)
			itemMeta.setLore(Arrays.asList(new String[] {
				"§aAcheteur : §e" + proposition.getPlayer().getName(),
				"§aPrix : §e" + economyHook.ts(price),
				"§6Clic pour §bvendre"}));
		else
			itemMeta.setLore(Arrays.asList(new String[] {
				"§aAcheteur : §e" + proposition.getPlayer().getName(),
				"§aPrix : §e" + economyHook.ts(price * amount),
				"§aPrix unité : §e" + economyHook.ts(price),
				"§6Clic gauche pour §bvendre " + amount,
				"§6Clic droit pour §bvendre 1"}));
		itemStack.setItemMeta(itemMeta);

		final GuiItem item = new GuiItem(itemStack, (g, p, e) -> {
			switch (e.getClick()) {
				case LEFT:
					confirmator.confirmStack(proposition, p);
					break;
				case RIGHT:
					confirmator.confirmOne(proposition, p);
					break;
				case SHIFT_LEFT:
				case SHIFT_RIGHT:
					confirmator.confirmAll(proposition, p);
				default:
					break;
			}
			refresh.apply(g.getAdditionnalInfo().get("page"));
		});

		return item;
	}

	private GuiItem showcase(final AdminBuyProposition proposition, final Player player, final PageRefresh refresh) {
		final double price = proposition.getPrice();
		final ItemStack itemStack = proposition.getItemStack();
		final int amount = itemStack.getAmount();

		final ItemMeta itemMeta = itemStack.getItemMeta();
		if (amount == 1)
			itemMeta.setLore(Arrays.asList(
				new String[] {"§aVendeur : §eAdmin Shop", "§aPrix : §e" + economyHook.ts(price), "§6Clic pour §bacheter"}));
		else
			itemMeta.setLore(Arrays.asList(new String[] {
				"§aVendeur : §eAdmin Shop",
				"§aPrix : §e" + economyHook.ts(price * amount),
				"§aPrix unité : §e" + economyHook.ts(price),
				"§6Clic gauche pour §bacheter " + amount,
				"§6Clic droit pour §bacheter 1"}));
		itemStack.setItemMeta(itemMeta);

		final GuiItem item = new GuiItem(itemStack, (g, p, e) -> {
			switch (e.getClick()) {
				case LEFT:
					confirmator.confirmStack(proposition, p);
					break;
				case RIGHT:
					confirmator.confirmOne(proposition, p);
					break;
				default:
					break;
			}
			refresh.apply(g.getAdditionnalInfo().get("page"));
		});

		return item;
	}

	private GuiItem showcase(final AdminSellProposition proposition, final Player player, final PageRefresh refresh) {
		final double price = proposition.getPrice();
		final ItemStack itemStack = proposition.getItemStack();
		final int amount = itemStack.getAmount();

		final ItemMeta itemMeta = itemStack.getItemMeta();
		if (amount == 1)
			itemMeta.setLore(Arrays.asList(
				new String[] {"§aAcheteur : §eAdmin Shop", "§aPrix : §e" + economyHook.ts(price), "§6Clic pour §bvendre"}));
		else
			itemMeta.setLore(Arrays.asList(new String[] {
				"§aAcheteur : §eAdmin Shop",
				"§aPrix : §e" + economyHook.ts(price * amount),
				"§aPrix unité : §e" + economyHook.ts(price),
				"§6Clic gauche pour §bvendre " + amount,
				"§6Clic droit pour §bvendre 1"}));
		itemStack.setItemMeta(itemMeta);

		final GuiItem item = new GuiItem(itemStack, (g, p, e) -> {
			switch (e.getClick()) {
				case LEFT:
					confirmator.confirmStack(proposition, p);
					break;
				case RIGHT:
					confirmator.confirmOne(proposition, p);
					break;
				case SHIFT_LEFT:
				case SHIFT_RIGHT:
					confirmator.confirmAll(proposition, p);
				default:
					break;
			}
			refresh.apply(g.getAdditionnalInfo().get("page"));
		});

		return item;
	}

	@FunctionalInterface
	private static interface PageRefresh {
		public void apply(final int p);
	}

	public void openMenu(final Player player, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions();
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openMenu(player, i));

		final Map<Integer, GuiItem> options = new HashMap<>(4, 1);
		options.put(47, new GuiItem(categories, (g, p, e) -> openCategoriesMenu(p)));
		options.put(48, new GuiItem(inventory, (g, p, e) -> openInventory(p, 0)));
		options.put(50, new GuiItem(notSorted, (g, p, e) -> openMenuIDSorted(p, 0)));
		options.put(51, new GuiItem(notFiltered, (g, p, e) -> openBuyMenu(p, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openBuyMenu(final Player player, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> (x instanceof PlayerBuyProposition) || (x instanceof AdminBuyProposition))
			.collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openBuyMenu(player, i));

		final Map<Integer, GuiItem> options = new HashMap<>(4, 1);
		options.put(47, new GuiItem(categories, (g, p, e) -> openCategoriesMenu(p)));
		options.put(48, new GuiItem(inventory, (g, p, e) -> openInventory(p, 0)));
		options.put(50, new GuiItem(notSorted, (g, p, e) -> openBuyMenuIDSorted(p, 0)));
		options.put(51, new GuiItem(buyFiltered, (g, p, e) -> openSellMenu(p, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openSellMenu(final Player player, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> (x instanceof PlayerSellProposition) || (x instanceof AdminSellProposition))
			.collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openSellMenu(player, i));

		final Map<Integer, GuiItem> options = new HashMap<>(4, 1);
		options.put(47, new GuiItem(categories, (g, p, e) -> openCategoriesMenu(p)));
		options.put(48, new GuiItem(inventory, (g, p, e) -> openInventory(p, 0)));
		options.put(50, new GuiItem(notSorted, (g, p, e) -> openSellMenuIDSorted(p, 0)));
		options.put(51, new GuiItem(sellFiltered, (g, p, e) -> openMenu(p, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openMenuIDSorted(final Player player, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream().sorted((x, y) -> {
			final String xv = x.getItemStack().getType().toString();
			final String yv = y.getItemStack().getType().toString();
			return xv.compareTo(yv);
		}).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openBuyMenuIDSorted(player, i));

		final Map<Integer, GuiItem> options = new HashMap<>(4, 1);
		options.put(47, new GuiItem(categories, (g, p, e) -> openCategoriesMenu(p)));
		options.put(48, new GuiItem(inventory, (g, p, e) -> openInventory(p, 0)));
		options.put(50, new GuiItem(idSorted, (g, p, e) -> openMenu(p, 0)));
		options.put(51, new GuiItem(notFiltered, (g, p, e) -> openBuyMenuIDSorted(p, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openBuyMenuIDSorted(final Player player, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> (x instanceof PlayerBuyProposition) || (x instanceof AdminBuyProposition)).sorted((x, y) -> {
				final String xv = x.getItemStack().getType().toString();
				final String yv = y.getItemStack().getType().toString();
				return xv.compareTo(yv);
			}).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openBuyMenuIDSorted(player, i));

		final Map<Integer, GuiItem> options = new HashMap<>(4, 1);
		options.put(47, new GuiItem(categories, (g, p, e) -> openCategoriesMenu(p)));
		options.put(48, new GuiItem(inventory, (g, p, e) -> openInventory(p, 0)));
		options.put(50, new GuiItem(idSorted, (g, p, e) -> openBuyMenu(p, 0)));
		options.put(51, new GuiItem(buyFiltered, (g, p, e) -> openSellMenuIDSorted(p, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openSellMenuIDSorted(final Player player, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> (x instanceof PlayerSellProposition) || (x instanceof AdminSellProposition)).sorted((x, y) -> {
				final String xv = x.getItemStack().getType().toString();
				final String yv = y.getItemStack().getType().toString();
				return xv.compareTo(yv);
			}).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openBuyMenuIDSorted(player, i));

		final Map<Integer, GuiItem> options = new HashMap<>(4, 1);
		options.put(47, new GuiItem(categories, (g, p, e) -> openCategoriesMenu(p)));
		options.put(48, new GuiItem(inventory, (g, p, e) -> openInventory(p, 0)));
		options.put(50, new GuiItem(idSorted, (g, p, e) -> openSellMenu(p, 0)));
		options.put(51, new GuiItem(sellFiltered, (g, p, e) -> openMenuIDSorted(p, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openCategoriesMenu(final Player player) {
		final Map<Material, Integer> categoriesMap = new HashMap<>();
		for (final Proposition proposition : stockageManager.getPropositions())
			categoriesMap.compute(proposition.getItemStack().getType(), (k, v) -> (v == null) ? 1 : (v + 1));

		final List<GuiItem> itemList = new ArrayList<>();
		for (final Entry<Material, Integer> entry : categoriesMap.entrySet()) {
			final Material mat = entry.getKey();
			final int nb = entry.getValue();

			final ItemStack stack = new ItemStack(mat, Math.min(nb, 64));
			final ItemMeta stackMeta = stack.getItemMeta();
			stackMeta.setLore(Arrays.asList(new String[] {"§d" + nb + " proposition(s)"}));
			stack.setItemMeta(stackMeta);

			itemList.add(new GuiItem(stack, (g, p, e) -> openCategoryMenu(p, mat, 0)));
		}

		final Map<Integer, GuiItem> options = new HashMap<>(2, 1);
		options.put(47, new GuiItem(back, (g, p, e) -> openMenu(p, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, 0, null);
	}

	public void openCategoryMenu(final Player player, final Material material, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> x.getItemStack().getType().equals(material)).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openCategoryMenu(player, material, i));

		final Map<Integer, GuiItem> options = new HashMap<>(3, 1);
		options.put(47, new GuiItem(back, (g, p, e) -> openCategoriesMenu(p)));
		options.put(50, new GuiItem(notSorted, (g, p, e) -> openCategoryMenuPriceSorted(p, material, 0)));
		options.put(51, new GuiItem(notFiltered, (g, p, e) -> openBuyCategoryMenu(p, material, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openBuyCategoryMenu(final Player player, final Material material, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> (x instanceof PlayerBuyProposition) || (x instanceof AdminBuyProposition))
			.filter((x) -> x.getItemStack().getType().equals(material)).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openCategoryMenu(player, material, i));

		final Map<Integer, GuiItem> options = new HashMap<>(3, 1);
		options.put(47, new GuiItem(back, (g, p, e) -> openCategoriesMenu(p)));
		options.put(50, new GuiItem(notSorted, (g, p, e) -> openBuyCategoryMenuPriceSorted(p, material, 0)));
		options.put(51, new GuiItem(buyFiltered, (g, p, e) -> openSellCategoryMenu(p, material, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openSellCategoryMenu(final Player player, final Material material, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> (x instanceof PlayerSellProposition) || (x instanceof AdminSellProposition))
			.filter((x) -> x.getItemStack().getType().equals(material)).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openCategoryMenu(player, material, i));

		final Map<Integer, GuiItem> options = new HashMap<>(3, 1);
		options.put(47, new GuiItem(back, (g, p, e) -> openCategoriesMenu(p)));
		options.put(50, new GuiItem(notSorted, (g, p, e) -> openSellCategoryMenuPriceSorted(p, material, 0)));
		options.put(51, new GuiItem(sellFiltered, (g, p, e) -> openCategoryMenu(p, material, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openCategoryMenuPriceSorted(final Player player, final Material material, final int page) {
		final List<Proposition> propositions =
			stockageManager.getPropositions().parallelStream().filter((x) -> x.getItemStack().getType().equals(material))
				.sorted((x, y) -> (int) (100 * (x.getPrice() - y.getPrice()))).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openCategoryMenu(player, material, i));

		final Map<Integer, GuiItem> options = new HashMap<>(3, 1);
		options.put(47, new GuiItem(back, (g, p, e) -> openCategoriesMenu(p)));
		options.put(50, new GuiItem(priceSorted, (g, p, e) -> openCategoryMenu(p, material, 0)));
		options.put(51, new GuiItem(notFiltered, (g, p, e) -> openBuyCategoryMenuPriceSorted(p, material, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openBuyCategoryMenuPriceSorted(final Player player, final Material material, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> (x instanceof PlayerBuyProposition) || (x instanceof AdminBuyProposition))
			.filter((x) -> x.getItemStack().getType().equals(material))
			.sorted((x, y) -> (int) (100 * (x.getPrice() - y.getPrice()))).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openCategoryMenu(player, material, i));

		final Map<Integer, GuiItem> options = new HashMap<>(3, 1);
		options.put(47, new GuiItem(back, (g, p, e) -> openCategoriesMenu(p)));
		options.put(50, new GuiItem(priceSorted, (g, p, e) -> openBuyCategoryMenu(p, material, 0)));
		options.put(51, new GuiItem(buyFiltered, (g, p, e) -> openSellCategoryMenuPriceSorted(p, material, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openSellCategoryMenuPriceSorted(final Player player, final Material material, final int page) {
		final List<Proposition> propositions = stockageManager.getPropositions().parallelStream()
			.filter((x) -> (x instanceof PlayerSellProposition) || (x instanceof AdminSellProposition))
			.filter((x) -> x.getItemStack().getType().equals(material))
			.sorted((x, y) -> (int) (100 * (x.getPrice() - y.getPrice()))).collect(Collectors.toList());
		final List<GuiItem> itemList = transform(propositions, player, (i) -> openCategoryMenu(player, material, i));

		final Map<Integer, GuiItem> options = new HashMap<>(3, 1);
		options.put(47, new GuiItem(back, (g, p, e) -> openCategoriesMenu(p)));
		options.put(50, new GuiItem(priceSorted, (g, p, e) -> openSellCategoryMenu(p, material, 0)));
		options.put(51, new GuiItem(sellFiltered, (g, p, e) -> openCategoryMenuPriceSorted(p, material, 0)));

		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, null);
	}

	public void openInventory(final Player player, final int page) {
		final GuiAction lambda1 = (g, p, e) -> {
			final ItemStack stack = e.getCurrentItem();
			final MuscleInventory inventory = MuscleInventory.getInventory(p);
			final Inventory playerInventory = p.getInventory();

			switch (e.getClick()) {
				case LEFT:
				case RIGHT:
					if (playerInventory.firstEmpty() != -1) {
						playerInventory.addItem(stack);
						inventory.remove(stack);
					}
					break;
				case SHIFT_LEFT:
				case SHIFT_RIGHT:

					break;
				default:
					break;
			}

			openInventory(p, g.getAdditionnalInfo().get("page"));
		};

		final GuiAction lambda2 = (g, p, e) -> {
			final ItemStack stack = e.getCurrentItem();
			final MuscleInventory inventory = MuscleInventory.getInventory(p);
			final Inventory playerInventory = p.getInventory();

			switch (e.getClick()) {
				case LEFT:
				case RIGHT:
					playerInventory.removeItem(stack);
					inventory.add(stack);
					break;
				case SHIFT_LEFT:
				case SHIFT_RIGHT:
					final List<ItemStack> all = new ArrayList<>();
					count(playerInventory, stack, all);
					final ItemStack[] stacks = all.toArray(new ItemStack[all.size()]);
					playerInventory.removeItem(stacks);
					inventory.add(stacks);
					break;
				default:
					break;
			}

			openInventory(p, g.getAdditionnalInfo().get("page"));
		};

		final MuscleInventory inventory = MuscleInventory.getInventory(player);
		final List<GuiItem> itemList = new ArrayList<>();
		for (final ItemStack s : inventory.getInventory())
			itemList.add(new GuiItem(s, lambda1));

		final Map<Integer, GuiItem> options = new HashMap<>(3, 1);
		options.put(48, new GuiItem(back, (g, p, e) -> openMenu(p, 0)));

		final AdditionnalInfo additionnalInfo = new AdditionnalInfo();
		additionnalInfo.add("other_action", lambda2);
		MuscleShop.getPlugin().getGuiManager().openPagedGui(player, "MuscleShop", itemList, options, page, additionnalInfo);
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
