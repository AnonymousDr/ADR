package cat.adr.commandblock.editorplus.commands;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import cat.adr.commandblock.editorplus.CBEP;
import cat.adr.commandblock.editorplus.objects.Apply;
import cat.adr.commandblock.editorplus.objects.Command;

public class PlaceCB implements Command{
	public static HashMap<String,PlaceCB> placing=new HashMap<String,PlaceCB>();
	private String nick;
	private Listener listener;
	public void execute(CommandSender j,String txt){
		nick=j.getName();
		if(placing.containsKey(nick)){
			j.sendMessage(CBEP.MESSAGES.get("command.place.disable"));
			placing.get(nick).remove();
			return;
		}j.sendMessage(CBEP.MESSAGES.get("command.place.enable"));
		Apply.players.add(nick);
		placing.put(nick,this);
		Bukkit.getPluginManager().registerEvents(listener=new Listener(){
			@EventHandler
			public void onPlace(BlockPlaceEvent e){
				if(!e.getPlayer().getName().equals(nick))return;
				e.getBlock().setType(Material.COMMAND);
			}
			@EventHandler
			public void onInterackLeft(PlayerInteractEvent e){
				if(!e.getPlayer().getName().equals(nick))return;
				if(!e.getAction().equals(Action.LEFT_CLICK_BLOCK))return;
				e.getClickedBlock().setType(Material.AIR);
			}
			@EventHandler
			public void onQuit(PlayerQuitEvent e){
				if(!e.getPlayer().getName().equals(nick))return;
				remove();
			}
		},CBEP.instance);
	}
	private void remove(){
		Apply.players.remove(nick);
		placing.remove(nick);
		HandlerList.unregisterAll(listener);
	}
}
