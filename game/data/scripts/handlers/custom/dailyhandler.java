package handlers.custom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import l2r.gameserver.model.actor.instance.L2PcInstance;

/*author vmilon*/
public class dailyhandler
{
	private static final dailyhandler INSTANCE = new dailyhandler();
	
	private final Map<Integer, Set<Integer>> playerCompletedQuests = new HashMap<>();
	
	public static dailyhandler getInstance()
	{
		return INSTANCE;
	}
	
	public synchronized void markQuestAsCompleted(L2PcInstance player, int questId)
	{
		Set<Integer> completedQuests = playerCompletedQuests.computeIfAbsent(player.getObjectId(), k -> new HashSet<>());
		completedQuests.add(questId);
	}
	
	public synchronized boolean hasCompletedQuest(L2PcInstance player, int questId)
	{
		Set<Integer> completedQuests = playerCompletedQuests.get(player.getObjectId());
		return (completedQuests != null) && completedQuests.contains(questId);
	}
	
	public synchronized void resetDailyQuests()
	{
		playerCompletedQuests.clear();
	}
}
