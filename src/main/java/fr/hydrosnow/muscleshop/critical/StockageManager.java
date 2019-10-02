package fr.hydrosnow.muscleshop.critical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.hydrosnow.muscleshop.propositions.Proposition;

public class StockageManager {
	private final File configFile;
	private final FileConfiguration config;
	private final List<Proposition> propositions;

	@SuppressWarnings("unchecked")
	public StockageManager() throws IOException {
		new File("plugins/MuscleShop").mkdirs();
		configFile = new File("plugins/MuscleShop/data.yml");
		configFile.createNewFile();
		config = YamlConfiguration.loadConfiguration(configFile);

		propositions = (List<Proposition>) config.getList("propositions", new ArrayList<Proposition>());
	}

	public void save() {
		config.set("propositions", propositions);
		try {
			config.save(configFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public List<Proposition> getPropositions() {
		return propositions;
	}

	public void addProposition(final Proposition proposition) {
		propositions.add(proposition);
		save();
	}

	public void removeProposition(final Proposition proposition) {
		propositions.remove(proposition);
		save();
	}
}
