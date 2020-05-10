package com.raus.wallet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Wallet extends JavaPlugin
{
	private List<String> tokens = new ArrayList<String>();
	private Map<String, String> symbols = new HashMap<String, String>();
	private Map<UUID, Map<String, Integer>> wallets = new HashMap<UUID, Map<String, Integer>>();

	private File dataFile;
	private FileConfiguration data;

	@Override
	public void onEnable()
	{
		// Config stuff
		saveDefaultConfig();
		createData();
		reload();

		// Listeners
		getServer().getPluginManager().registerEvents(new JoinListener(), this);

		// Register command
		getCommand("wallet").setExecutor(new MainCommand());
		getCommand("wallet").setTabCompleter(new MainCommand());
	}

	@Override
	public void onDisable()
	{
		// Iterate through wallets
		for (UUID player : wallets.keySet())
		{
			for (String token : wallets.get(player).keySet())
			{
				data.set(player + "." + token, wallets.get(player).get(token));
			}
		}

		// Save data
		try {
			data.save(dataFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createData()
	{
		// Create file if it's missing
		dataFile = new File(getDataFolder(), "data.yml");
		if (!dataFile.exists())
		{
			dataFile.getParentFile().mkdirs();
			saveResource("data.yml", false);
		}

		// Load data
		data = new YamlConfiguration();
		try {
			data.load(dataFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reload()
	{
		// Clear
		tokens.clear();
		symbols.clear();

		// Read
		ConfigurationSection section = getConfig().getConfigurationSection("tokens");
		for (String token : section.getKeys(false))
		{
			// Add entries
			tokens.add(token);
			symbols.put(token, getConfig().getString("tokens." + token));
		}

		// Read player data
		/*section = data.getConfigurationSection("");
		for (String uuid : section.getKeys(false))
		{
			// Prepare wallet
			Map<String, Integer> wallet = new HashMap<String, Integer>();
			wallets.put(UUID.fromString(uuid), wallet);

			// Get tokens
			ConfigurationSection sec = data.getConfigurationSection(uuid);
			for (String token : sec.getKeys(false))
			{
				wallet.put(token, data.getInt(uuid + "." + token));
			}
		}*/
	}

	/*
	 * @param player The UUID of the player whom you want to load the wallet.
	 */
	public void loadWallet(UUID player)
	{
		// Read player data
		ConfigurationSection section = data.getConfigurationSection(player.toString());

		if (section != null)
		{
			// Prepare wallet
			Map<String, Integer> wallet = new HashMap<String, Integer>();
			wallets.put(player, wallet);

			// Get tokens
			for (String token : section.getKeys(false))
			{
				wallet.put(token, data.getInt(player.toString() + "." + token));
			}
		}
	}

	/*
	 * @return A list of available tokens and their symbols.
	 */
	public List<String> getTokens()
	{
		return Collections.unmodifiableList(tokens);
	}

	/*
	 * @param token The name of the token.
	 * @return The token's symbol.
	 */
	public String getSymbol(String token)
	{
		return symbols.getOrDefault(token, "");
	}

	/*
	 * @param player The UUID of the player.
	 * @param token The name of the token.
	 * @return The amount this player has of a given token.
	 */
	public int getFromWallet(UUID player, String token)
	{
		return wallets.containsKey(player) ? wallets.get(player).getOrDefault(token, 0) : 0;
	}

	/*
	 * @param player The UUID of the player.
	 * @param token The name of the token.
	 * @param amount The amount to set.
	 */
	public void setWallet(UUID player, String token, int amount)
	{
		// Add missing values
		if (!wallets.containsKey(player))
		{
			wallets.put(player, new HashMap<String, Integer>());
		}

		// Set
		wallets.get(player).put(token, amount);
	}

	/*
	 * @param player The UUID of the player.
	 * @param token The name of the token.
	 * @param amount The amount to give.
	 */
	public void giveToWallet(UUID player, String token, int amount)
	{
		// Add missing values
		if (!wallets.containsKey(player))
		{
			wallets.put(player, new HashMap<String, Integer>());
		}
		Map<String, Integer> wallet = wallets.get(player);
		if (!wallet.containsKey(token))
		{
			wallet.put(token, 0);
		}

		// Add
		int current = wallet.get(token);
		wallet.put(token, current + amount);
	}

	/*
	 * @param player The UUID of the player.
	 * @param token The name of the token.
	 * @param amount The amount to take.
	 */
	public void takeFromWallet(UUID player, String token, int amount)
	{
		// Add missing values
		if (!wallets.containsKey(player))
		{
			wallets.put(player, new HashMap<String, Integer>());
		}
		Map<String, Integer> wallet = wallets.get(player);
		if (!wallets.get(player).containsKey(token))
		{
			wallet.put(token, 0);
		}

		// Subtract
		int current = wallet.get(token);
		wallet.put(token, current - amount);
	}
}