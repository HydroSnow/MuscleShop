package fr.hydrosnow.muscleshop.critical;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

class MuscleStack {
	private final ItemStack itemStack;
	private int amount;

	public MuscleStack(final ItemStack is, final int a) {
		itemStack = is;
		is.setAmount(1);
		amount = a;

		check();
	}

	public MuscleStack(final String b64) {
		try {
			final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(b64));
			final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			amount = dataInput.readInt();
			itemStack = (ItemStack) dataInput.readObject();
			dataInput.close();
		} catch (final IOException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new NullPointerException();
		}

		check();
	}

	private void check() {
		if (amount < 0)
			amount = 0;
		if (itemStack == null)
			throw new NullPointerException("Item stack passed is null");
	}

	public String serialize() {
		check();

		try {
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeInt(amount);
			dataOutput.writeObject(itemStack);
			dataOutput.close();
			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void add(final int x) {
		amount += x;
	}

	public void remove(final int x) {
		amount -= x;
	}

	public boolean equals(final ItemStack is) {
		return itemStack.isSimilar(is);
	}

	public boolean isEmpty() {
		return amount == 0;
	}

	public int getAmount() {
		return amount;
	}

	public List<ItemStack> toItemStacks() {
		final List<ItemStack> itemList = new ArrayList<>();
		final int inc = itemStack.getMaxStackSize();
		for (int a = 0; a < amount; a += inc) {
			final ItemStack is = itemStack.clone();
			is.setAmount(Math.min(inc, amount - a));
			itemList.add(is);
		}
		return itemList;
	}
}
