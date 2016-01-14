package util.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import models.UserProject;

public class ScoreAssign extends GroupSheet {

	public ScoreAssign(long projectID, long groupID) {
		super(projectID, groupID);
	}

	protected List<Long> chooseUsers(List<Long> users, int num){
		if(users.size() <= num)return users;
		
		//for debug.
		System.out.println("choosing users by score...");

		//create userScores
		Map<Long, Integer> userScores = new HashMap<>(users.size());
		for(Long userID : users){
			userScores.put(userID, UserProject.getUserScore(projectID, userID));
		}
		
		//scores are unique.
		List<Integer> scores = new ArrayList<>(new HashSet(userScores.values()));
		//sort score
		Collections.sort(scores);
		Collections.reverse(scores);

		//choose
		List<Long> chosenUsers = new ArrayList<>(num);
		
		//choose user by score order 
		for(int score : scores){
			//add userIDs of the current score
			for(long id : getUserIDsOfScore(userScores, score)){
				chosenUsers.add(id);
				if(chosenUsers.size() == num)break;
			}
			if(chosenUsers.size() == num)break;
		}


		return chosenUsers;
	}
	
	private List<Long> getUserIDsOfScore(Map<Long, Integer> userScores, int score){
		List<Long> ids = new ArrayList<>();
		
		for(Long userID : userScores.keySet()){
			int s = userScores.get(userID);
			if(s == score){
				ids.add(userID);
			}
		}
		
		return ids;
	}
	
}
