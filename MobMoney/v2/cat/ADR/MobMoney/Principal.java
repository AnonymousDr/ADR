package cat.ADR.MobMoney;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import cat.ADR.MobMoney.objetos.Mob;
import cat.ADR.MobMoney.objetos.Timer;
import cat.ADR.MobMoney.objetos.User;
import net.milkbowl.vault.economy.Economy;

public class Principal extends JavaPlugin{
	public static final HashMap<String,String> msg=new HashMap<String,String>();
	public static final List<SpawnReason> spawnban=new ArrayList<SpawnReason>();
	public static final List<String> bannedUUID=new ArrayList<String>();
	public static List<String> disabledWorlds;
	public static boolean disableCreative,enableTimer;
	public static File cplugin;
	public static Principal instancia;
	public static Economy eco;
	
	public void onEnable(){
		try{
			instancia=this;
			cplugin=getDataFolder();
			if(!cplugin.exists())cplugin.mkdir();
			setConfig();
			eco=Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
			Bukkit.getPluginManager().registerEvents(new Listener(){
				@EventHandler
				public void alEntrar(PlayerJoinEvent e){
					new User(e.getPlayer().getName());
				}
				@EventHandler
				public void alSalir(PlayerQuitEvent e){
					User.getUser(e.getPlayer().getName()).disconnect();
				}
				@EventHandler
				public void alMorirENTIDAD(EntityDeathEvent e){
					LivingEntity m=e.getEntity();
					if(disabledWorlds.contains(m.getWorld().getName()))return;
					Mob mob=Mob.getEntidad(e.getEntityType());
					if(mob==null)return;
					Player j=m.getKiller();
					if(j==null)return;
					if(!j.hasPermission("mobmoney.get"))return;
					if(disableCreative&&j.getGameMode().equals(GameMode.CREATIVE))return;
					if(bannedUUID.contains(m.getUniqueId().toString())){
						j.sendMessage(msg.get("Events.entityBanned"));
						return;
					}User u=User.getUser(j.getName());
					if(!u.canGiveReward()){
						if(u.getReceiveOnDeath())j.sendMessage(msg.get("Events.MaxKillsReached"));
						return;
					}if(u.getReceiveOnDeath())j.sendMessage(msg.get("Events.hunt").replace("%entity%",mob.getName()).replace("%reward%",String.valueOf(mob.getPrice())));
					eco.depositPlayer(j,mob.getPrice());
				}
				@EventHandler
				public void alSpawnear(CreatureSpawnEvent e){
					if(spawnban.contains(e.getSpawnReason()))bannedUUID.add(e.getEntity().getUniqueId().toString());
				}
			},this);
			
			//Cargando entiades baneadas
			File f=new File(cplugin+"/entitybans.dat");
			if(!f.exists())return;
			FileConfiguration yml=new YamlConfiguration();
			yml.load(f);
			List<String> bans=yml.getStringList("Bans");
			for(World w:Bukkit.getWorlds())for(Entity e:w.getEntities()){
				String UUID=e.getUniqueId().toString();
				if(bans.contains(UUID))bannedUUID.add(UUID);
			}
			
			Bukkit.getScheduler().runTaskLater(this,new Runnable(){
				@Override
				public void run(){
					for(Player j:Bukkit.getOnlinePlayers())new User(j.getName());
				}
			},1);
		}catch(Exception Ex){
			Ex.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[MobMoney] "+ChatColor.RED+"Plugin disabled!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	private void setConfig()throws Exception{
		File fConfig=new File(cplugin+"/config.yml");
		if(!fConfig.exists())fConfig.createNewFile();
		FileConfiguration config=new YamlConfiguration();
		config.load(fConfig);
		
		//Creando configuración
		if(!config.contains("disabledWorlds"))config.set("disabledWorlds",new String[0]);
		if(!config.contains("Language"))config.set("Language","English");
		if(!config.contains("DisableCreative"))config.set("DisableCreative",true);
		if(!config.contains("Timer.enable"))config.set("Timer.enable",false);
		if(!config.contains("Timer.maxKills"))config.set("Timer.maxKills",20);
		if(!config.contains("Timer.resetTimeInSeconds"))config.set("Timer.resetTimeInSeconds",30);
		for(EntityType et:EntityType.values()){
			if(!(et.isSpawnable()&&et.isAlive()))continue;
			String name=et.name().toLowerCase();
			if(!config.contains("Entity.economy."+name))config.set("Entity.economy."+name,1d);
			if(!config.contains("Entity.name."+name))config.set("Entity.name."+name,name);
		}for(SpawnReason sr:SpawnReason.values())if(!config.contains("BlockPayEntitiesSpawnedBy."+sr.name()))config.set("BlockPayEntitiesSpawnedBy."+sr.name(),false);
		config.save(fConfig);
		
		//TODO creando idiomas
		File idiomas=new File(cplugin+"/language/");
		if(!idiomas.exists())idiomas.mkdir();
		
		//English
		File archivo=new File(idiomas+"/English.yml");
		FileConfiguration yml=new YamlConfiguration();
		if(!archivo.exists())archivo.createNewFile();
		else yml.load(archivo);
		yml.set("Events.hunt","&aYou've hunted a &b%entity%&a and you've been rewareded %reward%&a!");
		yml.set("Events.MaxKillsReached","&cYou reached the limit of entities of you can kill!");
		yml.set("Events.entityBanned","&cThe entity you have hunted is banned.");
		yml.set("Commands.noPermission","&cYou don't have enough privileges for it.");
		yml.set("Commands.onlyPlayers","&cThis command only can be executed by in-game players.");
		yml.set("Commands.invalidArguments","&cInvalid arguments.");
		yml.set("Commands.arguments.reload","reload");
		yml.set("Commands.arguments.disableWorld","disableworld");
		yml.set("Commands.arguments.enableWorld","enableworld");
		yml.set("Commands.arguments.toggle","toggle");
		yml.set("Commands.Messages.enabledMessages","&aNow you will receive messages!");
		yml.set("Commands.Messages.disabledMessages","&6Now you will not receive messages!");
		yml.set("Commands.Messages.addWorld","&aWorld enabled!");
		yml.set("Commands.Messages.delWorld","&aWorld disabled!");
		yml.set("Commands.Messages.CurrentlyWorldAdded","&6The world was already in the list!");
		yml.set("Commands.Messages.WorldNotFinded","&cThe world has not been found!");
		yml.set("Commands.Use.reload","&aReload config: &b/mobmoney config");
		yml.set("Commands.Use.enableWorld","&aEnable world: &b/mobmoney enableworld <World>");
		yml.set("Commands.Use.disableWorld","&aDisable world: &b/mobmoney disableworld <World>");
		yml.set("Commands.Use.toggle","&aToggle messages: &b/mobmoney toggle");
		yml.save(archivo);
		
		//Español
		archivo=new File(idiomas+"/Spanish.yml");
		yml=new YamlConfiguration();
		if(!archivo.exists())archivo.createNewFile();
		else yml.load(archivo);
		yml.set("Events.hunt","&a¡Has cazado un &b%entity%&a y has sido recompensado con %reward%&a$!");
		yml.set("Events.MaxKillsReached","&c¡Has alcanzado al límite de entidades que puedes matar!");
		yml.set("Events.entityBanned","&cLa entidad que has cazado se encuentra baneada.");
		yml.set("Commands.noPermission","&cNo tienes suficientes privilegios para ello.");
		yml.set("Commands.onlyPlayers","&cEste comando solo puede ser ejecutado por jugadores dentro del juego.");
		yml.set("Commands.invalidArguments","&cArgumentos inválidos.");
		yml.set("Commands.arguments.reload","recargar");
		yml.set("Commands.arguments.disableWorld","deshabilitarmundo");
		yml.set("Commands.arguments.enableWorld","habilitarmundo");
		yml.set("Commands.arguments.toggle","toggle");
		yml.set("Commands.Messages.enabledMessages","&a¡Ahora recibirás mensajes!");
		yml.set("Commands.Messages.disabledMessages","&6¡Ya no recibirás mensajes!");
		yml.set("Commands.Messages.addWorld","&a¡Mundo habilitado!");
		yml.set("Commands.Messages.delWorld","&a¡Mundo deshabilitado!");
		yml.set("Commands.Messages.CurrentlyWorldAdded","&6¡El mundo ya estaba en la lista!");
		yml.set("Commands.Messages.WorldNotFinded","&c¡No se ha encontrado el mundo!");
		yml.set("Commands.Use.reload","&aRecargar la configuración: &b/mobmoney recargar");
		yml.set("Commands.Use.enableWorld","&aHabilitar mundo: &b/mobmoney habilitarmundo <Mundo>");
		yml.set("Commands.Use.disableWorld","&aDeshabilitar mundo: &b/mobmoney deshabilitarmundo <Mundo>");
		yml.set("Commands.Use.toggle","&aDeshabilitar mensajes: &b/mobmoney toggle");
		yml.save(archivo);
		
		//Català
		archivo=new File(idiomas+"/Catalan.yml");
		yml=new YamlConfiguration();
		if(!archivo.exists())archivo.createNewFile();
		else yml.load(archivo);
		yml.set("Events.hunt","&aHas caçat un &b%entity%&a i has sigut recompensat amb %reward%&a$!");
		yml.set("Events.MaxKillsReached","&cHas arribat al limit d'entitats que pots matar!");
		yml.set("Events.entityBanned","&cL'entitat que has caçat es troba banejada.");
		yml.set("Commands.noPermission","&cNo tens suficients privilegis per a això.");
		yml.set("Commands.onlyPlayers","&cAquest comand solamment pot ser executat per jugadors dins del joc.");
		yml.set("Commands.invalidArguments","&cArguments invàlids.");
		yml.set("Commands.arguments.reload","recarregar");
		yml.set("Commands.arguments.disableWorld","deshabilitarmon");
		yml.set("Commands.arguments.enableWorld","habilitarmon");
		yml.set("Commands.arguments.toggle","toggle");
		yml.set("Commands.Messages.enabledMessages","&aAra rebràs missatges!");
		yml.set("Commands.Messages.disabledMessages","&6Ja no rebràs missatges!");
		yml.set("Commands.Messages.addWorld","&aMón habilitat!");
		yml.set("Commands.Messages.delWorld","&aMón deshabilitat!");
		yml.set("Commands.Messages.CurrentlyWorldAdded","&6El món ja estava a la llista!");
		yml.set("Commands.Messages.WorldNotFinded","&cNo s'ha trobat el món!");
		yml.set("Commands.Use.reload","&aRecarregar la configuració: &b/mobmoney recarregar");
		yml.set("Commands.Use.enableWorld","&aHabilitar món: &b/mobmoney habilitarmon <Món>");
		yml.set("Commands.Use.disableWorld","&aDeshabilitar món: &b/mobmoney deshabilitarmon <Món>");
		yml.set("Commands.Use.toggle","&aDeshabilitar messatges: &b/mobmoney toggle");
		yml.save(archivo);
		
		//Cargando configuración
		disabledWorlds=config.getStringList("disabledWorlds");
		File fidioma=new File(idiomas+"/"+config.getString("Language")+".yml");
		if(!fidioma.exists()){
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA+"[MobMoney] "+ChatColor.RED+"Language file named '"+fidioma.getName()+"' not found! Using 'English.yml'");
			config.set("Language","English");
			config.save(fConfig);
			fidioma=new File(idiomas+"/English.yml");
		}for(EntityType et:EntityType.values()){
			String name=et.name().toLowerCase();
			new Mob(et,config.getDouble("Entity.economy."+name),config.getString("Entity.name."+name));
		}disableCreative=config.getBoolean("DisableCreative");
		enableTimer=config.getBoolean("Timer.enable");
		if(enableTimer){
			Timer.TICKS=config.getInt("Timer.resetTimeInSeconds")*20;
			Timer.KILLS=config.getInt("Timer.maxKills");
		}spawnban.clear();
		for(SpawnReason sr:SpawnReason.values())if(config.getBoolean("BlockPayEntitiesSpawnedBy."+sr.name()))spawnban.add(sr);
		
		//Cargando idioma
		FileConfiguration idioma=new YamlConfiguration();
		idioma.load(fidioma);
		msg.clear();
		for(String v:idioma.getValues(true).keySet())msg.put(v,ChatColor.translateAlternateColorCodes('&',idioma.getString(v)));
	}
	
	public void onDisable(){
		if(!bannedUUID.isEmpty()){
			List<String> bans=new ArrayList<String>();
			for(World w:Bukkit.getWorlds())for(Entity e:w.getEntities()){
				String UUID=e.getUniqueId().toString();
				if(bannedUUID.contains(UUID))bans.add(UUID);
			}if(bans.isEmpty())return;
			try{
				File f=new File(cplugin+"/entitybans.dat");
				if(!f.exists())f.createNewFile();
				FileConfiguration yml=new YamlConfiguration();
				yml.set("Bans",bans);
				yml.save(f);
			}catch(Exception Ex){
				Ex.printStackTrace();
			}
		}
	}
	
	public boolean onCommand(CommandSender j,Command cmd,String label,String[] args){
		if(args.length==0){
			boolean permiso=false;
			if(j.hasPermission("mobmoney.reload")?permiso=true:false)j.sendMessage(msg.get("Commands.Use.reload"));
			if(j.hasPermission("mobmoney.enableworld")?permiso=true:false)j.sendMessage(msg.get("Commands.Use.enableWorld"));
			if(j.hasPermission("mobmoney.disableworld")?permiso=true:false)j.sendMessage(msg.get("Commands.Use.disableWorld"));
			if(j.hasPermission("mobmoney.toggle")?permiso=true:false)j.sendMessage(msg.get("Commands.Use.toggle"));
			if(!permiso)j.sendMessage(msg.get("Commands.noPermission"));
			return true;
		}String arg0=args[0];
		if(arg0.equalsIgnoreCase(msg.get("Commands.arguments.reload"))){
			if(!j.hasPermission("mobmoney.reload")){
				j.sendMessage(msg.get("Commands.noPermission"));
				return true;
			}try{
				setConfig();
			}catch(Exception Ex){
				Ex.printStackTrace();
				j.sendMessage(ChatColor.RED+"An error ocurred.");
				return true;
			}j.sendMessage(ChatColor.GREEN+"Reloaded!");
			return true;
		}if(arg0.equalsIgnoreCase(msg.get("Commands.arguments.disableWorld"))){
			if(!j.hasPermission("mobmoney.disableworld")){
				j.sendMessage(msg.get("Commands.noPermission"));
				return true;
			}if(args.length==1){
				j.sendMessage(msg.get("Commands.Use.disableWorld"));
				return true;
			}World w=Bukkit.getWorld(args[1]);
			if(w==null){
				j.sendMessage(msg.get("Commands.Messages.WorldNotFinded"));
				return true;
			}if(disabledWorlds.contains(w.getName())){
				j.sendMessage(msg.get("Commands.Messages.CurrentlyWorldAdded"));
				return true;
			}disabledWorlds.add(w.getName());
			try{
				File f=new File(cplugin+"/config.yml");
				FileConfiguration yml=new YamlConfiguration();
				yml.load(f);
				yml.set("disabledWorlds",disabledWorlds);
				yml.save(f);
				j.sendMessage(msg.get("Commands.Messages.delWorld"));
			}catch(Exception Ex){
				j.sendMessage(ChatColor.RED+"An error ocurred");
			}return true;
		}if(arg0.equalsIgnoreCase(msg.get("Commands.arguments.enableWorld"))){
			if(!j.hasPermission("mobmoney.enableworld")){
				j.sendMessage(msg.get("Commands.noPermission"));
				return true;
			}if(args.length==1){
				j.sendMessage(msg.get("Commands.Use.enableWorld"));
				return true;
			}World w=Bukkit.getWorld(args[1]);
			if(w==null){
				j.sendMessage(msg.get("Commands.Messages.WorldNotFinded"));
				return true;
			}if(!disabledWorlds.contains(w.getName())){
				j.sendMessage(msg.get("Commands.Messages.CurrentlyWorldAdded"));
				return true;
			}disabledWorlds.remove(w.getName());
			try{
				File f=new File(cplugin+"/config.yml");
				FileConfiguration yml=new YamlConfiguration();
				yml.load(f);
				yml.set("disabledWorlds",disabledWorlds);
				yml.save(f);
				j.sendMessage(msg.get("Commands.Messages.addWorld"));
			}catch(Exception Ex){
				j.sendMessage(ChatColor.RED+"An error ocurred");
			}return true;
		}if(arg0.equalsIgnoreCase(msg.get("Commands.arguments.toggle"))){
			if(!j.hasPermission("mobmoney.toggle")){
				j.sendMessage(msg.get("Commands.noPermission"));
				return true;
			}if(!(j instanceof Player)){
				j.sendMessage(msg.get("Commands.onlyPlayers"));
				return true;
			}User u=User.getUser(j.getName());
			if(u.getReceiveOnDeath()){
				u.setReceiveOnDeath(false);
				j.sendMessage(msg.get("Commands.Messages.disabledMessages"));
			}else{
				u.setReceiveOnDeath(true);
				j.sendMessage(msg.get("Commands.Messages.enabledMessages"));
			}return true;
		}j.sendMessage(msg.get("Commands.invalidArguments"));
		if(j.hasPermission("mobmoney.reload"))j.sendMessage(msg.get("Commands.Use.reload"));
		if(j.hasPermission("mobmoney.enableworld"))j.sendMessage(msg.get("Commands.Use.enableWorld"));
		if(j.hasPermission("mobmoney.disableworld"))j.sendMessage(msg.get("Commands.Use.disableWorld"));
		if(j.hasPermission("mobmoney.toggle"))j.sendMessage(msg.get("Commands.Use.toggle"));
		return true;
	}
}
