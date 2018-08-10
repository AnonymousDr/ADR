package cat.adr.commandblock.editorplus.commands;

import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import cat.adr.commandblock.editorplus.CBEP;
import cat.adr.commandblock.editorplus.objects.Apply;
import cat.adr.commandblock.editorplus.objects.Command;
import cat.adr.commandblock.editorplus.objects.Run;

public class Edit implements Command{
	public void execute(CommandSender j,String txt){
		j.sendMessage(CBEP.MESSAGES.get("command.apply.edit").replace("%cmd%",txt));
		new Apply((Player)j,new Run(){
			public void run(Block b){
				CommandBlock cb=(CommandBlock)b.getState();
				cb.setCommand(txt);
				cb.update();
				j.sendMessage(CBEP.MESSAGES.get("action.edit").replace("%cmd%",cb.getCommand()));
			}
		});
	}
}
