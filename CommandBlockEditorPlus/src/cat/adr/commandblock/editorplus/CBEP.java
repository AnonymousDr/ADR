package cat.adr.commandblock.editorplus;

import java.io.File;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import cat.adr.commandblock.editorplus.commands.*;
import cat.adr.commandblock.editorplus.objects.Apply;

public class CBEP extends JavaPlugin{
	public static CBEP instance;
	public static long MAX_TIME;
	public static HashMap<String,String> MESSAGES=new HashMap<String,String>();
	public void onEnable(){
		instance=this;
		try{
			File cplugin=getDataFolder();
			if(!cplugin.exists())cplugin.mkdir();
			File fconfig=new File(cplugin+"/config.yml");
			if(!fconfig.exists())fconfig.createNewFile();
			FileConfiguration config=new YamlConfiguration();
			//Load config
			//Default data
			config.load(fconfig);
			if(!config.contains("maxTimeToApply"))config.set("maxTimeToApply",60);
			if(!config.contains("language"))config.set("language","English");
			config.save(fconfig);
			//Default data saved
			MAX_TIME=config.getLong("maxTimeToApply")*20l;
			//Loading language
			File FLanguage=new File(cplugin+"/language/");
			if(!FLanguage.exists())FLanguage.mkdir();
			//Create language English
			File fenglish=new File(FLanguage+"/English.yml");
			if(!fenglish.exists())fenglish.createNewFile();
			FileConfiguration english=new YamlConfiguration();
			english.load(fenglish);
			if(!english.contains("command.error.notplayer"))english.set("command.error.notplayer","&cThis command can only can be executed by a player.");
			if(!english.contains("command.error.nopermission"))english.set("command.error.nopermission","&cYou don't have enough privileges for it.");
			if(!english.contains("command.error.noargs"))english.set("command.error.noargs","&cUse: /%label% <Command>");
			if(!english.contains("command.apply.read"))english.set("command.apply.read","&aBreak the command block that you want to read.");
			if(!english.contains("action.read"))english.set("action.read","&3Command readed:\n&f%cmd%");
			if(!english.contains("command.apply.edit"))english.set("command.apply.edit","&aBreak the command block where do you want write:\n&f%cmd%");
			if(!english.contains("action.edit"))english.set("action.edit","&3It has been written on the command block:\n&f%cmd%");
			if(!english.contains("command.apply.continue"))english.set("command.apply.continue","&aBreak the command block where you want to continue writing:\n&f%cmd%");
			if(!english.contains("action.continue"))english.set("action.continue","&3It has been continued writing on the command block and now has the command:\n&f%cmd%");
			if(!english.contains("action.outoftime"))english.set("action.outoftime","&cThe time to apply the action in the command block has run out.");
			if(!english.contains("command.error.havinganothertask"))english.set("command.error.havinganothertask","&cYou sill have an pending action to apply.");
			if(!english.contains("action.notcommandblock"))english.set("action.notcommandblock","&cThat is not a command block.");
			if(!english.contains("command.place.enable"))english.set("command.place.enable","&aThe &3placing command blocks&a mode has been enabled.");
			if(!english.contains("command.place.disable"))english.set("command.place.disable","&cThe &3placing command blocks&c mode has been disabled. Place any block to make it a command block.");
			english.save(fenglish);
			//Create language Spanish
			File fspanish=new File(FLanguage+"/Spanish.yml");
			if(!fspanish.exists())fspanish.createNewFile();
			FileConfiguration spanish=new YamlConfiguration();
			english.load(fspanish);
			if(!spanish.contains("command.error.notplayer"))spanish.set("command.error.notplayer","&cEste comando solo puede ser ejecutado por jugadores in-game.");
			if(!spanish.contains("command.error.nopermission"))spanish.set("command.error.nopermission","&cNo tienes suficientes privilegios para ello.");
			if(!spanish.contains("command.error.noargs"))spanish.set("command.error.noargs","&cUso: /%label% <Comando>");
			if(!spanish.contains("command.apply.read"))spanish.set("command.apply.read","&aRompe el bloque de comandos que quieres leer.");
			if(!spanish.contains("action.read"))spanish.set("action.read","&3Comando leído:\n&f%cmd%");
			if(!spanish.contains("command.apply.edit"))spanish.set("command.apply.edit","&aRompe el bloque de comandos donde quieres escribir:\n&f%cmd%");
			if(!spanish.contains("action.edit"))spanish.set("action.edit","&3Se ha escrito en el bloque de comandos:\n&f%cmd%");
			if(!spanish.contains("command.apply.continue"))spanish.set("command.apply.continue","&aRompe el bloque de comandos donde quieres escribir:\n&f%cmd%");
			if(!spanish.contains("action.continue"))spanish.set("action.continue","&3Se ha continuado escribiendo en el bloque de comandos y ahora contiene el comando:\n&f%cmd%");
			if(!spanish.contains("action.outoftime"))spanish.set("action.outoftime","&cSe ha agotado el tiempo para aplicar esa acción en el bloque de comandos.");
			if(!spanish.contains("command.error.havinganothertask"))spanish.set("command.error.havinganothertask","&cAún tienes una acción pendiente de aplicar.");
			if(!english.contains("action.notcommandblock"))english.set("action.notcommandblock","&cEso no es un bloque de comandos.");
			if(!spanish.contains("command.place.enable"))spanish.set("command.place.enable","&aSe ha activado el modo &3colocar bloques de comandos&a. Coloca cualquier bloque para convertirlo en un bloque de comandos.");
			if(!spanish.contains("command.place.disable"))spanish.set("command.place.disable","&cSe ha desativado el modo &3colocar bloques de comandos&c.");
			spanish.save(fspanish);
			//Create language Catalan
			File fcatalan=new File(FLanguage+"/Catalan.yml");
			if(!fcatalan.exists())fcatalan.createNewFile();
			FileConfiguration catalan=new YamlConfiguration();
			catalan.load(fcatalan);
			if(!catalan.contains("command.error.notplayer"))catalan.set("command.error.notplayer","&cAquest comando solament pot ser executat per jugadors in-game.");
			if(!catalan.contains("command.error.nopermission"))catalan.set("command.error.nopermission","&cNo tens suficients privilegis per això.");
			if(!catalan.contains("command.error.noargs"))catalan.set("command.error.noargs","&cÚs: /%label% <Ordre>");
			if(!catalan.contains("command.apply.read"))catalan.set("command.apply.read","&aTrenca el bloc de ordres que vols llegir.");
			if(!catalan.contains("action.read"))catalan.set("action.read","&3Ordre llegida:\n&f%cmd%");
			if(!catalan.contains("command.apply.edit"))catalan.set("command.apply.edit","&aTrenca el bloc de ordres on vols escriure:\n&f%cmd%");
			if(!catalan.contains("action.edit"))catalan.set("action.edit","&3S'ha escrit al bloc de ordres:\n&f%cmd%");
			if(!catalan.contains("command.apply.continue"))catalan.set("command.apply.continue","&aTrenca el bloc de ordres on vols continuar escribint:\n&f%cmd%");
			if(!catalan.contains("action.continue"))catalan.set("action.continue","&3S'ha continuan escribint al bloc d'ordres y ara conté la ordre:\n&f%cmd%");
			if(!catalan.contains("action.outoftime"))catalan.set("action.outoftime","&cS'ha esgotat el temps per aplicar l'acció al bloque de ordres.");
			if(!catalan.contains("command.error.havinganothertask"))catalan.set("command.error.havinganothertask","&cEncara tens una acció pendent d'aplicar.");
			if(!english.contains("action.notcommandblock"))english.set("action.notcommandblock","&cAixò no es bloc de ordres.");
			if(!catalan.contains("command.place.enable"))catalan.set("command.place.enable","&aS'ha activat el mode &3col·locar blocs de ordres&a. Col·loca qualsevol bloc per convertir-ho en un bloc de ordres.");
			if(!catalan.contains("command.place.disable"))catalan.set("command.place.disable","&cS'ha desativat el mode &3col·locar blocs de ordres&c.");
			catalan.save(fcatalan);
			//Load selected language
			FileConfiguration language=new YamlConfiguration();
			language.load(new File(FLanguage+"/"+config.getString("language")+".yml"));
			for(String s:language.getConfigurationSection("").getValues(true).keySet())MESSAGES.put(s,ChatColor.translateAlternateColorCodes('&',language.getString(s)));
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"["+ChatColor.AQUA+"CommanBlock Edit Plus"+ChatColor.GOLD+"] "+ChatColor.GREEN+"Plugin started!");
		}catch(Exception Ex){
			Ex.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"["+ChatColor.AQUA+"CommanBlock Edit Plus"+ChatColor.GOLD+"] "+ChatColor.RED+"Error starting the plugin! Disabled!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	public boolean onCommand(CommandSender j,Command cmd,String label,String[] args){
		if(!(j instanceof Player)){
			j.sendMessage(MESSAGES.get("command.error.notplayer"));
			return true;
		}if(!j.hasPermission("commandblockeditplus."+label)){
			j.sendMessage(MESSAGES.get("command.error.nopermission"));
			return true;
		}if(Apply.players.contains(j.getName())&&!(cmd.getName().toLowerCase().equals("placecb")&&PlaceCB.placing.containsKey(j.getName()))){
			j.sendMessage(MESSAGES.get("command.error.havinganothertask"));
			return true;
		}switch(cmd.getName().toLowerCase()){
			case "continue":
				String txt=transformText(args);
				if(txt==null){
					j.sendMessage(MESSAGES.get("command.error.noargs").replace("%label%",label));
					break;
				}new Continue().execute(j,txt);
				break;
			case "edit":
				txt=transformText(args);
				if(txt==null){
					j.sendMessage(MESSAGES.get("command.error.noargs").replace("%label%",label));
					break;
				}new Edit().execute(j,txt);
				break;
			case "read":
				new Read().execute(j,"");
				break;
			case "space":
				txt=transformText(args);
				if(txt==null){
					j.sendMessage(MESSAGES.get("command.error.noargs").replace("%label%",label));
					break;
				}new Continue().execute(j," "+txt);
				break;
			case "placecb":
				new PlaceCB().execute(j,"");
				break;
		}return true;
	}
	private String transformText(String[] args){
		if(args.length==0)return null;
		String txt=args[0];
		for(int i=1;i<args.length;i++)txt+=" "+args[i];
		return ChatColor.translateAlternateColorCodes('&',txt);
	}
}
