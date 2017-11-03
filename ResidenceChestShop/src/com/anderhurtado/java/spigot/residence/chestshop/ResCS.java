package com.anderhurtado.java.spigot.residence.chestshop;

import java.text.DecimalFormat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import com.bekvon.bukkit.residence.economy.ResidenceBank;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import net.milkbowl.vault.economy.Economy;

public class ResCS extends JavaPlugin{
	public static ResidenceInterface resAPI;
	public static Economy eco;
	public void onEnable(){
		resAPI=ResidenceApi.getResidenceManager();
		eco=Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
		Bukkit.getPluginManager().registerEvents(new Listener(){
			@EventHandler
			public void alCrear(PreShopCreationEvent e){
				if(!e.getSignLines()[0].equals("#Residence"))return;
				if(!e.getPlayer().hasPermission("rescs.create")){
					e.setOutcome(CreationOutcome.NO_PERMISSION);
					return;
				}if(e.getSignLines()[2].contains("\\.")){
					e.setOutcome(CreationOutcome.INVALID_PRICE);
					return;
				}if(resAPI.getByLoc(e.getSign().getLocation())==null){
					e.setOutcome(CreationOutcome.NO_PERMISSION_FOR_TERRAIN);
					return;
				}e.setOutcome(CreationOutcome.SHOP_CREATED_SUCCESSFULLY);
				e.setCreator(e.getPlayer());
			}
			@EventHandler
			public void alPreTransaccionar(PreTransactionEvent e){
				if(!e.getSign().getLine(0).equals("#Residence"))return;
				if(String.valueOf(e.getPrice()).contains("\\."))e.setCancelled(TransactionOutcome.SHOP_DEPOSIT_FAILED);
				if(e.getTransactionOutcome()==TransactionOutcome.SHOP_DOES_NOT_HAVE_ENOUGH_MONEY)e.setCancelled(TransactionOutcome.TRANSACTION_SUCCESFUL);
			}
			@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
			public void alPreTransaccionarHIGHEST(PreTransactionEvent e){
				if(!e.getSign().getLine(0).equals("#Residence"))return;
				if(e.getTransactionType()==TransactionType.SELL){
					ResidenceBank RB=resAPI.getByLoc(e.getSign().getLocation()).getBank();
					int P=Integer.valueOf(new DecimalFormat("#").format(e.getPrice()));
					if(RB.hasEnough(P))
					eco.depositPlayer(e.getOwner(),P);
					RB.subtract(P);
				}
			}
			@EventHandler
			public void alTransaccionar(TransactionEvent e){
				if(!e.getSign().getLine(0).equals("#Residence"))return;
				if(e.getTransactionType()==TransactionType.BUY){
					ClaimedResidence CR=resAPI.getByLoc(e.getSign().getLocation());
					double P=e.getPrice();
					eco.withdrawPlayer(e.getOwner(),P);
					CR.getBank().add(Integer.valueOf(new DecimalFormat("#").format(P)));
				}
			}
		},this);
	}
}
