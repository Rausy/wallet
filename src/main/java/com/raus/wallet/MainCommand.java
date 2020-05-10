package com.raus.wallet;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class MainCommand implements CommandExecutor, TabCompleter
{
	private final Wallet plugin = JavaPlugin.getPlugin(Wallet.class);
	private final String[] help = {
			" §e---- §6Wallet Help §e----",
			"§6/wallet§r: See your wallet",
			"§6/wallet help§r: See this help page",
			"§6/wallet info§r: Plugin info",
			"§6/wallet peek [player]§r: Peek at a player's wallet",
			"§6/wallet give [player] [token] [#]§r: Give a player some tokens",
			"§6/wallet take [player] [token] [#]§r: Take tokens from a player",
			"§6/wallet set [player] [token] [#]§r: Set a player's tokens",
			"§6/wallet reload§r: Reload config"
	};
	private final String prefix = "§6[Wallet]";

	//@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length == 0)
		{
			if (!(sender instanceof Player))
			{
				// Send message
				sender.sendMessage(prefix + ChatColor.RED + " This command cannot be run from the console.");
				return true;
			}

			// Send message
			sender.sendMessage(getWallet((Player) sender));
			return true;
		}
		else if (args[0].equals("help"))
		{
			// Send message
			sender.sendMessage(help);
			return true;
		}
		else if (args[0].equals("info"))
		{
			// Send message
			sender.sendMessage(prefix + ChatColor.GRAY + " Version " + plugin.getDescription().getVersion()
					+ " by " + ChatColor.RED + "Raus");
			return true;
		}
		else if (args[0].equals("reload"))
		{
			if (!sender.hasPermission("wallet.reload"))
			{
				// Send message
				sender.sendMessage(prefix + ChatColor.RED + " You do not have permission to run this command.");
				return true;
			}

			// Reload config
			plugin.reload();

			// Send message
			sender.sendMessage(prefix + ChatColor.GRAY + " Config reloaded!");
			return true;
		}
		else if (args[0].equals("peek"))
		{
			if (!sender.hasPermission("wallet.peek"))
			{
				sender.sendMessage(prefix + ChatColor.RED + " You do not have permission to peek into other's wallets.");
				return true;
			}
			else if (args.length < 2)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Peek at who?");
				return true;
			}

			// Get player balance
			Player ply = Bukkit.getPlayer(args[1]);

			if (ply != null)
			{
				sender.sendMessage(getWallet(ply));
				return true;
			}
			else
			{
				sender.sendMessage(prefix + ChatColor.RED + " Player is not online.");
				return true;
			}
		}
		else if (args[0].equals("give"))
		{
			if (!sender.hasPermission("wallet.modify"))
			{
				sender.sendMessage(prefix + ChatColor.RED + " You do not have permission to modify other's wallets.");
				return true;
			}
			else if (args.length < 2)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Give to who?");
				return true;
			}
			else if (args.length < 3)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Give which token to them?");
				return true;
			}
			else if (args.length < 4)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Give how much to them?");
				return true;
			}

			// Get player balance
			Player ply = Bukkit.getPlayer(args[1]);

			if (ply != null)
			{
				plugin.giveToken(ply.getUniqueId(), args[2], Integer.parseInt(args[3]));
				sender.sendMessage(prefix + ChatColor.GRAY + " Gave " + ChatColor.RED + args[3] + " " + args[2] + ChatColor.GRAY + " to " + ply.getName() + ".");
				return true;
			}
			else
			{
				sender.sendMessage(prefix + ChatColor.RED + " Player is not online.");
				return true;
			}
		}
		else if (args[0].equals("take"))
		{
			if (!sender.hasPermission("wallet.modify"))
			{
				sender.sendMessage(prefix + ChatColor.RED + " You do not have permission to modify other's wallets.");
				return true;
			}
			else if (args.length < 2)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Take from who?");
				return true;
			}
			else if (args.length < 3)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Take which token from them?");
				return true;
			}
			else if (args.length < 4)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Take how much from them?");
				return true;
			}

			// Get player balance
			Player ply = Bukkit.getPlayer(args[1]);

			if (ply != null)
			{
				plugin.takeToken(ply.getUniqueId(), args[2], Integer.parseInt(args[3]));
				sender.sendMessage(prefix + ChatColor.GRAY + " Took " + ChatColor.RED + args[3] + " " + args[2] + ChatColor.GRAY + " from " + ply.getName() + ".");
				return true;
			}
			else
			{
				sender.sendMessage(prefix + ChatColor.RED + " Player is not online.");
				return true;
			}
		}
		else if (args[0].equals("set"))
		{
			if (!sender.hasPermission("wallet.modify"))
			{
				sender.sendMessage(prefix + ChatColor.RED + " You do not have permission to modify other's wallets.");
				return true;
			}
			else if (args.length < 2)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Set who's wallet?");
				return true;
			}
			else if (args.length < 3)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Set which token?");
				return true;
			}
			else if (args.length < 4)
			{
				sender.sendMessage(prefix + ChatColor.RED + " Set how much?");
				return true;
			}

			// Get player balance
			Player ply = Bukkit.getPlayer(args[1]);

			if (ply != null)
			{
				plugin.setToken(ply.getUniqueId(), args[2], Integer.parseInt(args[3]));
				sender.sendMessage(prefix + ChatColor.GRAY + " Set " + ply.getName() + "'s " + ChatColor.RED + args[2] + ChatColor.GRAY + " to " + ChatColor.RED + args[3] + ChatColor.GRAY + ".");
				return true;
			}
			else
			{
				sender.sendMessage(prefix + ChatColor.RED + " Player is not online.");
				return true;
			}
		}
		else
		{
			// Default
			sender.sendMessage(help);
			return true;
		}
	}

	//@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		List<String> list = new ArrayList<String>();
		if (args.length == 1)
		{
			list.add("help");
			list.add("info");
			if (sender.hasPermission("wallet.peek")) { list.add("peek"); }
			if (sender.hasPermission("wallet.modify"))
			{
				list.add("give");
				list.add("take");
				list.add("set");
			}
			if (sender.hasPermission("wallet.reload")) { list.add("reload"); }
		}
		else if (args.length == 2)
		{
			for (Player ply : Bukkit.getOnlinePlayers())
			{
				list.add(ply.getName());
			}
		}
		else if (args.length == 3)
		{
			list = plugin.getTokenList();
		}
		return list;
	}

	private String[] getWallet(Player player)
	{
		List<String> tokens = plugin.getTokenList();
		String[] display = new String[tokens.size() + 1];
		display[0] = ChatColor.GOLD + "[" + player.getName() + "'s Wallet]";
		for (int i = 0; i < tokens.size(); ++i)
		{
			String token = tokens.get(i);
			String symbol = plugin.getSymbol(token);
			String str = token.substring(0,1).toUpperCase() + token.substring(1).toLowerCase();
			display[i + 1] = ChatColor.GREEN + str + ": " + ChatColor.RED + symbol + plugin.getToken(player.getUniqueId(), token);
		}
		return display;
	}
}