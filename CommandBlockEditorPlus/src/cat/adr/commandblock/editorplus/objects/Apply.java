package cat.adr.commandblock.editorplus.objects;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import cat.adr.commandblock.editorplus.CBEP;

public class Apply{
	public static List<String> players=new ArrayList<String>();
	private boolean cancel;
	private String nick;
	public Apply(Player j,final Run r){
		nick=j.getName();
		players.add(nick);
		Bukkit.getPluginManager().registerEvents(new Listener(){
			@EventHandler
			public void onInteract(PlayerInteractEvent e){
				if(!e.getPlayer().equals(j))return;
				if(!e.getAction().equals(Action.LEFT_CLICK_BLOCK))return;
				e.setCancelled(true);
				unregister();
				Block b=e.getClickedBlock();
				if(!b.getType().name().startsWith("COMMAND")){
					j.sendMessage(CBEP.MESSAGES.get("action.notcommandblock"));
					return;
				}r.run(b);
			}
			@EventHandler
			public void onDisconnect(PlayerQuitEvent e){
				if(e.getPlayer().equals(j))unregister();
			}
			private void unregister(){
				cancel=true;
				HandlerList.unregisterAll(this);
				remove();
			}
		},CBEP.instance);
		Bukkit.getScheduler().runTaskLater(CBEP.instance,new Runnable(){
			@Override
			public void run(){
				if(cancel)return;
				remove();
				j.sendMessage(CBEP.MESSAGES.get("action.outoftime"));
			}
		},CBEP.MAX_TIME);
	}
	private void remove(){
		players.remove(nick);
	}
}
