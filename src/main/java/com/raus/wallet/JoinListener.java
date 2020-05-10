package com.raus.wallet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinListener implements Listener
{
	private final Wallet plugin = JavaPlugin.getPlugin(Wallet.class);

	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		plugin.loadPlayerWallet(event.getPlayer().getUniqueId());
	}
}