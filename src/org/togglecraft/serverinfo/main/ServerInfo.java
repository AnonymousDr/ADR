package org.togglecraft.serverinfo.main;

import java.io.File;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class ServerInfo extends JavaPlugin{
	
	public void onEnable(){
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this,new LagRunners(),100L,1L);
	}
	
	public boolean onCommand(CommandSender j, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("serverinfo")){
			if(!j.hasPermission("serverinfo.command")){
				ChatColor.translateAlternateColorCodes('&',getConfig().getString("nopermission"));
				return true;
			}
			j.sendMessage(ChatColor.AQUA+"TPS: "+getTPS());
			if(j.hasPermission("serverinfo.command.os"))j.sendMessage(ChatColor.AQUA+"OS: "+ChatColor.YELLOW+System.getProperty("os.name"));
			if(j.hasPermission("serverinfo.command.diskspace")){
				double free=new File(getDataFolder()+"/..").getFreeSpace()/1073741824;
				double total=new File(getDataFolder()+"/..").getTotalSpace()/1073741824;
				j.sendMessage(ChatColor.AQUA+"Disk space used: "+ChatColor.GREEN+new DecimalFormat("#.##").format(total-free)+ChatColor.YELLOW+"/"+new DecimalFormat("#.##").format(total)+ChatColor.YELLOW+" GB ("+new DecimalFormat("#.##").format(((total-free)/total)*100)+"% used)");
			}if(j.hasPermission("serverinfo.command.ram")){
				double free=Runtime.getRuntime().freeMemory()/1048576;
				double total=Runtime.getRuntime().totalMemory()/1048576;
				j.sendMessage(ChatColor.AQUA+"RAM Used: "+ChatColor.GREEN+new DecimalFormat("#.###").format(total-free)+ChatColor.YELLOW+"/"+new DecimalFormat("#.###").format(total)+ChatColor.YELLOW+" MB ("+new DecimalFormat("#.##").format(((total-free)/total)*100)+"% used)");
			}if(j.hasPermission("serverinfo.command.cores"))j.sendMessage(ChatColor.AQUA+"Number of cores: "+ChatColor.YELLOW+Runtime.getRuntime().availableProcessors());
			if(j.hasPermission("serverinfo.command.java"))j.sendMessage(ChatColor.AQUA+"Java version: "+ChatColor.YELLOW+System.getProperty("java.version"));
			if(j.hasPermission("serverinfo.command.chunks")){
				int c=0;
				for(World w:Bukkit.getWorlds())c+=w.getLoadedChunks().length;
				j.sendMessage(ChatColor.AQUA+"Chunks loaded: "+ChatColor.YELLOW+c);
			}
		}
		return true;
	}
	
	private String getTPS(){
		double tps=LagRunners.getTPS();
		String t=new DecimalFormat("#.##").format(tps);
		if(tps>=20)return ChatColor.DARK_GREEN+t;
		if(tps>=17)return ChatColor.GREEN+t;
		if(tps>=15)return ChatColor.YELLOW+t;
		if(tps>=10)return ChatColor.GOLD+t;
		if(tps>=5)return ChatColor.RED+t;
		return ChatColor.DARK_RED+t;
	}

}
