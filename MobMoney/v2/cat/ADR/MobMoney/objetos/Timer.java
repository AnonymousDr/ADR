package cat.ADR.MobMoney.objetos;

import org.bukkit.Bukkit;
import cat.ADR.MobMoney.Principal;

public class Timer{
	public static int TICKS,KILLS;
	public int killed=0;
	public Timer(final User u){
		u.setTimer(this);
		Bukkit.getScheduler().runTaskLater(Principal.instancia,new Runnable(){
			@Override
			public void run(){
				u.setTimer(null);
			}
		},TICKS);
	}
	public boolean addEntity(){
		killed++;
		return(killed<=KILLS);
	}
	public boolean canGiveReward(){
		return(killed<=KILLS);
	}
}
