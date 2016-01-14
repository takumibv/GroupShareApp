package util.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JankenAssign extends GroupSheet {

	public JankenAssign(long projectID, long groupID) {
		super(projectID, groupID);
	}

	protected List<Long> chooseUsers(List<Long> users, int num){
		if(users.size() <= num)return users;
		
		//for debug.
		System.out.println("choosing users by janken...");

		List<Long> chosenUsers = new ArrayList<>(num);

		Random random = new Random(System.currentTimeMillis());

		//create random index of the list users
		List<Integer> rndIndex = new ArrayList<>(num);
		while(true){
			int rnd = random.nextInt(users.size());
			if(!rndIndex.contains(rnd)){
				rndIndex.add(rnd);
				if(rndIndex.size() == num)break;
			}
		}

		for(int i : rndIndex){
			chosenUsers.add(users.get(i));
		}

		return chosenUsers;
	}
	
}
