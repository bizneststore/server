package handlers.custom;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class dailyschedule
{
	public static final dailyschedule INSTANCE = new dailyschedule();
	public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public dailyschedule()
	{
	
	}
	
	public static dailyschedule getInstance()
	{
		return INSTANCE;
	}
	
	public void start()
	{
		long initialDelay = computeInitialDelay();
		long period = TimeUnit.DAYS.toMillis(1); // 24 hours
		
		scheduler.scheduleAtFixedRate(this::resetDailyQuests, initialDelay, period, TimeUnit.MILLISECONDS);
	}
	
	public long computeInitialDelay()
	{
		Calendar now = Calendar.getInstance();
		Calendar nextRun = Calendar.getInstance();
		nextRun.set(Calendar.HOUR_OF_DAY, 6);
		nextRun.set(Calendar.MINUTE, 0);
		nextRun.set(Calendar.SECOND, 0);
		nextRun.set(Calendar.MILLISECOND, 0);
		
		if (now.after(nextRun))
		{
			nextRun.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return nextRun.getTimeInMillis() - now.getTimeInMillis();
	}
	
	public void resetDailyQuests()
	{
		dailyhandler.getInstance().resetDailyQuests();
	}
}
