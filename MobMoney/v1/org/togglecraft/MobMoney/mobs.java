package org.togglecraft.MobMoney;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;


public class mobs
  extends JavaPlugin
  implements Listener
{
  protected HashMap<String, Boolean> DisabledPlayersHashMap;
  protected HashMap<Object, Boolean> DisabledEntitiesHashMap;
  protected HashMap<String, Integer> MobsTimerHashMap;
  
  private static String prefix = "[MobMoney] ";
  public static Economy eco = null;
  
  public void onEnable()
  {
	  this.DisabledPlayersHashMap = new HashMap<String, Boolean>();
	  this.DisabledEntitiesHashMap = new HashMap<Object, Boolean>();
	  this.MobsTimerHashMap = new HashMap<String, Integer>();
    if ((Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault))
    {
      RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
      if (service != null) {
        eco = (Economy)service.getProvider();
      }
    }
    else
    {
      System.out.print("[MobMoney] Error with Vault, it's necessary to starts this plugin.");
      getServer().getPluginManager().disablePlugin(this);
    }
    onReloadConfig();
    setConfig();
    getServer().getPluginManager().registerEvents(this, this);
    System.out.print("[MobMoney] Enabled correctly.");
  }
  
  public void onDisable()
  {
	     System.out.print("[MobMoney] Disabled correctly.");
  }

public void onReloadConfig() {
	  reloadConfig();
	  if (getConfig().getBoolean("Config.MobsInSeconds.Enable") == true) {
		  MobsTimer();
	  }
	  else {
		  Bukkit.getServer().getScheduler().cancelAllTasks();
	  }
	  return;
  }
  
  public void MobsTimer() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	MobsTimerHashMap.clear();
            }
        }, 0L, Integer.valueOf(20 * getConfig().getInt("Config.MobsInSeconds.Secons")));
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	  if (cmd.getName().equalsIgnoreCase("mobmoney")) {
      if (args.length == 0)
      {
        if ((sender instanceof Player)) {
          sender.sendMessage(ChatColor.RED + prefix + ChatColor.GREEN + "Please execute this command for help: /mobmoney help." + ChatColor.RESET);
        }
      }
      else if (args.length == 1)
      {
        String command = args[0];
        if (command.equalsIgnoreCase("reload"))
        {
          if ((sender instanceof Player))
          {
            if (sender.hasPermission("mobmoney.reload"))
            {
              onReloadConfig();
              sender.sendMessage(ChatColor.RED + prefix + ChatColor.GREEN + "Reloaded config!" + ChatColor.RESET);
            }
            else
            {
              sender.sendMessage(getConfig().getString("Messages.NoPermission").replaceAll("&", "§"));
            }
          }
          else if (sender.isOp())
          {
            onReloadConfig();
            getServer().getLogger().info(prefix + "Reloaded config!");
          }
        }
        else if ((sender instanceof Player)) {
          sendHelpMessage((Player)sender);
        }
      }
      else if (args.length == 2)
      {
        String command = args[0];
        String arg = args[1];
        if (command.equalsIgnoreCase("disable"))
        {
          if (Bukkit.getWorld(arg) != null)
          {
            if ((sender instanceof Player))
            {
              if (sender.hasPermission("mobmoney.disableworld"))
              {
                getConfig().set("Config.WorldsDisabled." + arg, Boolean.valueOf(true));
                saveConfig();
                sender.sendMessage(ChatColor.RED + prefix + ChatColor.GREEN + "Disabled MobMoney for the world: " + ChatColor.GOLD + arg + ChatColor.AQUA + "!" + ChatColor.RESET);
              }
              else
              {
                sender.sendMessage(getConfig().getString("Messages.NoPermission").replaceAll("&", "§"));
              }
            }
            else if (sender.isOp())
            {
              getConfig().set("Config.WorldsDisabled." + arg, Boolean.valueOf(true));
              saveConfig();
              getServer().getLogger().info(prefix + "Disabled MobMoney for the world: " + arg + "!");
            }
          }
          else if ((sender instanceof Player))
          {
            if (sender.hasPermission("mobmoney.disableworld")) {
              sender.sendMessage(ChatColor.RED + prefix + ChatColor.RED + "World doesn't exist (" + ChatColor.GOLD + arg + ChatColor.RED + ")!" + ChatColor.RESET);
            } else {
              sender.sendMessage(getConfig().getString("Messages.NoPermission").replaceAll("&", "§"));
            }
          }
          else if (sender.isOp()) {
            getServer().getLogger().info(prefix + "World doesn't exist (" + arg + ")!");
          }
          return true;
        }
        if (command.equalsIgnoreCase("enable"))
        {
          if (Bukkit.getWorld(arg) != null)
          {
            if ((sender instanceof Player)) {
              if ((sender instanceof Player))
              {
                if (sender.hasPermission("mobmoney.enableworld"))
                {
                  getConfig().set("Config.WorldsDisabled." + arg, Boolean.valueOf(false));
                  saveConfig();
                  sender.sendMessage(ChatColor.RED + prefix + ChatColor.GREEN + "Enabled Mob Cash for the world: " + ChatColor.GOLD + arg + ChatColor.AQUA + "!" + ChatColor.RESET);
                }
                else
                {
                  sender.sendMessage(getConfig().getString("Messages.NoPermission").replaceAll("&", "§"));
                }
              }
              else if (sender.isOp())
              {
                getConfig().set("Config.WorldsDisabled." + arg, Boolean.valueOf(false));
                saveConfig();
                getServer().getLogger().info(prefix + "Enabled Mob Cash for the world: " + arg + "!");
              }
            }
          }
          else if ((sender instanceof Player))
          {
            if (sender.hasPermission("mobmoney.enableworld")) {
              sender.sendMessage(ChatColor.RED + prefix + ChatColor.RED + "World doesn't exist (" + ChatColor.GOLD + arg + ChatColor.RED + ")!" + ChatColor.RESET);
            } else {
              sender.sendMessage(getConfig().getString("Messages.NoPermission").replaceAll("&", "§"));
            }
          }
          else if (sender.isOp()) {
            getServer().getLogger().info(prefix + "World doesn't exist (" + arg + ")!");
          }
          return true;
        }
        if ((sender instanceof Player)) {
          sendHelpMessage((Player)sender);
        }
      }
      else if ((sender instanceof Player))
      {
        sendHelpMessage((Player)sender);
      }
      return true;
    }
	  if (cmd.getName().equalsIgnoreCase("mbtoggle")) {
    		if (sender.hasPermission("mobmoney.toggle")) {
    			if (this.DisabledPlayersHashMap.containsKey(sender.getName())) {
    				sender.sendMessage(getConfig().getString("Messages.ReEnabledMessages").replaceAll("&", "§"));
    				this.DisabledPlayersHashMap.remove(sender.getName());
    				return false;
    			}
    			else {
    				sender.sendMessage(getConfig().getString("Messages.DisabledMessages").replaceAll("&", "§"));
    				this.DisabledPlayersHashMap.put(sender.getName(), true);
    			}
    		}
    		else {
    			sender.sendMessage(getConfig().getString("Messages.NoPermission").replaceAll("&", "§"));
    		}
			return false;
    }
    return false;
  }
  
  public void sendHelpMessage(Player p) {
    if (p.hasPermission("mobmoney.help"))
    {
      ChatColor gold = ChatColor.GOLD;
      ChatColor green = ChatColor.GREEN;
      ChatColor red = ChatColor.RED;
      ChatColor aqua = ChatColor.AQUA;
      p.sendMessage(gold + "------------------ " + red + "MobMoney Help " + gold + "------------------");
      p.sendMessage(gold + "- " + green + aqua + "/mobmoney help " + gold + "-" + green + " Show all the commands.");
      p.sendMessage(gold + "- " + green + aqua + "/mobmoney reload " + gold + "-" + green + " Reload the config file.");
      p.sendMessage(gold + "- " + green + aqua + "/mobmoney disable <WorldName> " + gold + "-" + green + " Disables a world.");
      p.sendMessage(gold + "- " + green + aqua + "/mobmoney enable <WorldName> " + gold + "-" + green + " Re-enable a world.");
    }
  }
  
  public void setConfig() {
    String v = getDescription().getVersion();
    getConfig().options().header("MobMoney v" + v + " by Anonymous_Dr & mjl1010. Help us to: https://www.spigotmc.org/threads/mobmoney.130350/");
    
    getConfig().addDefault("Config.DisableOnCreative", true);
    getConfig().addDefault("Config.RecordInLogKills", false);
    getConfig().addDefault("Config.DebugMode", false);
    getConfig().addDefault("Config.Mode.BlockPayEntitiesSpawnedBySpawnerEggs", false);
    getConfig().addDefault("Config.Mode.BlockPayEntitiesSpawnedBySpawners", false);
    getConfig().addDefault("Config.Mode.BlockPayEntitiesSpawnedBySummonCommand", false);
    getConfig().addDefault("Config.MobsInSeconds.Enable", false);
    getConfig().addDefault("Config.MobsInSeconds.Mobs", Integer.valueOf(3));
    getConfig().addDefault("Config.MobsInSeconds.Secons", Integer.valueOf(1));
    
    getConfig().addDefault("Rewards.Bat", Double.valueOf(0.15D));
    getConfig().addDefault("Rewards.Blaze", Double.valueOf(0.35D));
    getConfig().addDefault("Rewards.CaveSpider", Double.valueOf(0.35D));
    getConfig().addDefault("Rewards.Cow", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Shulker", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Spider", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Cow", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Spider", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Zombie", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Skeleton", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Giant", Double.valueOf(10D));
    getConfig().addDefault("Rewards.WitherBoss", Double.valueOf(100D));
    getConfig().addDefault("Rewards.EnderDragon", Double.valueOf(100D));
    getConfig().addDefault("Rewards.Witch", Double.valueOf(0.40D));
    getConfig().addDefault("Rewards.Enderman", Double.valueOf(0.50D));
    getConfig().addDefault("Rewards.Guardian", Double.valueOf(0.50D));
    getConfig().addDefault("Rewards.EntityHorse", Double.valueOf(0.30D));
    getConfig().addDefault("Rewards.Ghast", Double.valueOf(0.50D));
    getConfig().addDefault("Rewards.Slime", Double.valueOf(0.20D));
    getConfig().addDefault("Rewards.PigZombie", Double.valueOf(0.35D));
    getConfig().addDefault("Rewards.Pig", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Ozelot", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Wolf", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Squid", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Silverfish", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Rabbit", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Creeper", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Endermite", Double.valueOf(0.35D));
    getConfig().addDefault("Rewards.VillagerGolem", Double.valueOf(0.50D));
    getConfig().addDefault("Rewards.Chicken", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Villager", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.Sheep", Double.valueOf(0.25D));
    getConfig().addDefault("Rewards.MushroomCow", Double.valueOf(0.25D));
    
    getConfig().addDefault("Messages.Mobs.Bat", "&bBat");
    getConfig().addDefault("Messages.Mobs.Blaze", "&bBlaze");
    getConfig().addDefault("Messages.Mobs.CaveSpider", "&bCaveSpider");
    getConfig().addDefault("Messages.Mobs.Cow", "&bCow");
    getConfig().addDefault("Messages.Mobs.Shulker", "&bShulker");
    getConfig().addDefault("Messages.Mobs.Spider", "&bSpider");
    getConfig().addDefault("Messages.Mobs.Cow", "&bCow");
    getConfig().addDefault("Messages.Mobs.Spider", "&bSpider");
    getConfig().addDefault("Messages.Mobs.Zombie", "&bZombie");
    getConfig().addDefault("Messages.Mobs.Skeleton", "&bSkeleton");
    getConfig().addDefault("Messages.Mobs.Giant", "&bGiant");
    getConfig().addDefault("Messages.Mobs.WitherBoss", "&bWhiter");
    getConfig().addDefault("Messages.Mobs.EnderDragon", "&bEnderdragon");
    getConfig().addDefault("Messages.Mobs.Witch", "&bWitch");
    getConfig().addDefault("Messages.Mobs.Enderman", "&bEnderman");
    getConfig().addDefault("Messages.Mobs.Guardian", "&bGuardian");
    getConfig().addDefault("Messages.Mobs.EntityHorse", "&bHorse");
    getConfig().addDefault("Messages.Mobs.Ghast", "&bGhast");
    getConfig().addDefault("Messages.Mobs.Slime", "&bSlime");
    getConfig().addDefault("Messages.Mobs.PigZombie", "&bPigZombie");
    getConfig().addDefault("Messages.Mobs.Pig", "&bPig");
    getConfig().addDefault("Messages.Mobs.Ozelot", "&bOcelot");
    getConfig().addDefault("Messages.Mobs.Wolf", "&bWolf");
    getConfig().addDefault("Messages.Mobs.Squid", "&bSquid");
    getConfig().addDefault("Messages.Mobs.Silverfish", "&bSilverfish");
    getConfig().addDefault("Messages.Mobs.Rabbit", "&bRabbit");
    getConfig().addDefault("Messages.Mobs.Creeper", "&bCreeper");
    getConfig().addDefault("Messages.Mobs.Endermite", "&bEndermite");
    getConfig().addDefault("Messages.Mobs.VillagerGolem", "&bIron Golem");
    getConfig().addDefault("Messages.Mobs.Chicken", "&bChicken");
    getConfig().addDefault("Messages.Mobs.Villager", "&bVillager");
    getConfig().addDefault("Messages.Mobs.Sheep", "&bSheep");
    getConfig().addDefault("Messages.Mobs.MushroomCow", "&bMushroomCow");
    
    getConfig().addDefault("Messages.Reward", "%user%&a, you win &b%ammount%&a to kill an %entity%&a! You have &b%balance%&a.");
    getConfig().addDefault("Messages.Error", "%user%&c, han error has ocurred, yo cannot kill &b%entity%&c because: &f%error%&c.");
    getConfig().addDefault("Messages.NoPermission", "&cYou don\u0027t have permission for this!");
    getConfig().addDefault("Messages.ReEnabledMessages", "&aRe-enabled messages!");
    getConfig().addDefault("Messages.DisabledMessages", "&cDisabled messages!");
    getConfig().addDefault("Messages.LimitKills", "&cSorry, &f%user&c, you killed more mobs of the limit, wait to continue earn money killing mobs.");
    getConfig().addDefault("Messages.SpawnModeBlocked", "&cSorry, &f%user&c, you cannot earn money. The entity has spawned an unauthorized way.");
    getConfig().options().copyDefaults(true);
    saveConfig();
  }
  
  @EventHandler
  public void onSpawn(CreatureSpawnEvent event) {
	  if (getConfig().getBoolean("Config.Mode.BlockPayEntitiesSpawnedBySpawnerEggs") == true) {
		  if (event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
			  this.DisabledEntitiesHashMap.put(event.getEntity().getUniqueId(), true);
		  }
	  }
	  if (getConfig().getBoolean("Config.Mode.BlockPayEntitiesSpawnedBySpawners") == true) {
		  if (event.getSpawnReason() == SpawnReason.SPAWNER) {
			  this.DisabledEntitiesHashMap.put(event.getEntity().getUniqueId(), true);
		  }
	  }
	  if (getConfig().getBoolean("Config.Mode.BlockPayEntitiesSpawnedBySummonCommand") == true) {
		  if (event.getSpawnReason() == SpawnReason.DEFAULT) {
			  this.DisabledEntitiesHashMap.put(event.getEntity().getUniqueId(), true);
		  }
		  if (event.getSpawnReason() == SpawnReason.CUSTOM) {
			  this.DisabledEntitiesHashMap.put(event.getEntity().getUniqueId(), true);
		  }
	  }
  }
  
  @SuppressWarnings("deprecation")
@EventHandler
  public void onKill(EntityDeathEvent event)
  {
    if (!(event.getEntity().getKiller() instanceof Player)) {
    	this.DisabledEntitiesHashMap.remove(event.getEntity().getUniqueId());
      return;
    }
    if (!(this.MobsTimerHashMap.containsKey(event.getEntity().getKiller().getName()))) {
    	this.MobsTimerHashMap.put(event.getEntity().getKiller().getName(), Integer.valueOf(0));
    }
    if (getConfig().getString("Config.WorldsDisabled." + event.getEntity().getKiller().getWorld().getName()) == "true") {
    	return;
    }
    if (getConfig().getString("Config.DebugMode") == "true") {
    	System.out.print("[MobMoney Debug Mode] " + event.getEntity().getKiller().getName() + " has killed a " + event.getEntityType().getName() + ".");
    	if (event.getEntity().getKiller().hasPermission("mobmoney.debug")) {

        	event.getEntity().getKiller().sendMessage("[MobMoney Debug Mode] You killed a " + event.getEntityType().getName() + ".");
    	}
    }
	if (event.getEntity().getKiller().hasPermission("mobmoney.get")) {
    if (event.getEntity().getKiller().getGameMode() == GameMode.CREATIVE) {
    	if (getConfig().getString("Config.DisableOnCreative") == "true") {
        	this.DisabledEntitiesHashMap.remove(event.getEntity().getUniqueId());
        	return;
    	}
    }
    if (event.getEntity() == null) {
    	this.DisabledEntitiesHashMap.remove(event.getEntity().getUniqueId());
      return;
    }
	if (this.DisabledEntitiesHashMap.containsKey(event.getEntity().getUniqueId())) {
    	this.DisabledEntitiesHashMap.remove(event.getEntity().getUniqueId());
    	if (!(this.DisabledPlayersHashMap.containsKey(event.getEntity().getKiller().getName()))) {
    		if (getConfig().getString("Messages.SpawnModeBlocked") != "false") {
        		event.getEntity().getKiller().sendMessage(getConfig().getString("Messages.SpawnModeBlocked").replaceAll("&", "§").replaceAll("%user", event.getEntity().getKiller().getDisplayName()));
    		}
    	}
		return;
	}
	if (getConfig().getBoolean("Config.MobsInSeconds.Enable") == true) {
	    if (this.MobsTimerHashMap.get(event.getEntity().getKiller().getName()) >= Integer.valueOf(getConfig().getInt("Config.MobsInSeconds.Mobs") + 1)) {
	    	if (!(this.DisabledPlayersHashMap.containsKey(event.getEntity().getKiller().getName()))) {
	    		if (getConfig().getString("Messages.LimitKills") != "false") {}
	    		event.getEntity().getKiller().sendMessage(getConfig().getString("Messages.LimitKills").replaceAll("&", "§").replaceAll("%user", event.getEntity().getKiller().getDisplayName()));
	    	}
	    	return;
	    }
	}
    String e = event.getEntityType().getName();
    EconomyResponse r = null;
    if (getConfig().getString("Rewards." + e) != null)
    {
      r = eco.depositPlayer(event.getEntity().getKiller(), getConfig().getDouble("Rewards." + e));
      this.MobsTimerHashMap.put(event.getEntity().getKiller().getName(), Integer.valueOf(this.MobsTimerHashMap.get(event.getEntity().getKiller().getName()) + 1));
      if (r.transactionSuccess())
      {
    	  if (getConfig().getString("Messages.Reward") != "false") {
    		  	 this.MobsTimerHashMap.put(event.getEntity().getKiller().getName(), Integer.valueOf(this.MobsTimerHashMap.get(event.getEntity().getKiller().getName()) + 1));
    		     String entidad = event.getEntityType().getName();
    		     if (getConfig().getString("Messages.Mobs." + entidad) != "false") {
    		      entidad = getConfig().getString("Messages.Mobs." + entidad);
    		     }
		    	 if (!(this.DisabledPlayersHashMap.containsKey(event.getEntity().getKiller().getName()))) {
    		     String mensaje = getConfig().getString("Messages.Reward").replaceAll("%ammount%", String.valueOf(r.amount)).replaceAll("%balance%", String.valueOf(r.balance)).replaceAll("%user%", event.getEntity().getKiller().getDisplayName()).replaceAll("%entity%", entidad);
    		     event.getEntity().getKiller().sendMessage(ChatColor.translateAlternateColorCodes('&', mensaje));
    	          if (getConfig().getString("Config.RecordInLogKills") == "true") {
    	            	System.out.print("[MobMoney] " + event.getEntity().getKiller().getName() + " has killed a " + event.getEntityType().getName() + " and this has got " + getConfig().getString("Rewards." + event.getEntityType().getName()) + "$.");
    	            }
	    		  }
    		    }
    	  
        	}
      else
      {
        if (getConfig().getString("Messages.Error") != "false") {
          event.getEntity().getKiller().sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.Error")).replaceAll("%ammount%", String.valueOf(r.amount)).replaceAll("%balance%", String.valueOf(r.balance)).replaceAll("%user%", event.getEntity().getKiller().getDisplayName()).replaceAll("%entity%", event.getEntityType().getName()).replaceAll("%error%", r.errorMessage));
        }
        System.out.print("[MobMoney] Error: " + r.errorMessage);
      }
    }
  }
 }
}
