package org.togglecraft.serverinfo.main;

public class LagRunners implements Runnable{

	  public static int TICK_COUNT= 0;
	  public static long[] TICKS= new long[600];
	  public static long LAST_TICK= 0L;
	 
	  public static double getTPS(){
		  return getTPS(100);
	  }
	 
	  public static double getTPS(int ticks){
	    if(TICK_COUNT< ticks)return 20;
	    int target=(TICK_COUNT-1-ticks)%TICKS.length;
	    double elapsed =System.currentTimeMillis()-TICKS[target];
	    return ticks/(elapsed/1000);
	  }
	 
	  public static double getElapsed(int tickID){
	    double time = TICKS[(tickID % TICKS.length)];
	    return System.currentTimeMillis() - time;
	  }
	 
	  public void run(){
	    TICKS[(TICK_COUNT% TICKS.length)] = System.currentTimeMillis();
	    TICK_COUNT+= 1;
	  }
}
