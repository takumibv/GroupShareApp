package util.result;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 大貴 on 2016/01/08.
 */
public class GroupSheet {
	private final long projectID;
	private final long groupID;
	private final int capacity;

	private boolean isClosed;
	private boolean isSaved;

	private List<Long> lastUserIDList = new ArrayList<>();
	private List<Long> curRankUserIDList = new ArrayList<>();


	public GroupSheet(long projectID, long groupID){
		this.projectID = projectID;
		this.groupID = groupID;
		this.capacity = Group.getGroupById(groupID).capacity;
	}

	//when not closed
	void fillWithUsers(List<User> unFinishedRegisteredUsers, int rank){
		if(isClosed()){
			if(!isSaved) createUserGroup();
			return;
		}

		//users of nth-rank wish
		List<User> tmp = Wish.getUsers(groupID, rank);
		unFinishedRegisteredUsers.retainAll(tmp);
		List<User> users =unFinishedRegisteredUsers;

		//add current rank user IDs
		curRankUserIDList.clear();
		for(User user : users){
			curRankUserIDList.add(user.id);
		}

		//close
		int tmpUserListSize = lastUserIDList.size() + curRankUserIDList.size();
		if(tmpUserListSize == capacity){
			lastUserIDList.addAll(curRankUserIDList);
			isClosed = true;
		}
		else if(tmpUserListSize > capacity){
			List<Long> chosenUsers = chooseUsers(curRankUserIDList, getSpace());
			lastUserIDList.addAll(chosenUsers);
			isClosed = true;
		}
		//not overflow
		else{
			lastUserIDList.addAll(curRankUserIDList);
		}
	}

	boolean isClosed(){
		return isClosed;
	}

	//when closed
	void createUserGroup(){
		if(isSaved)return;

		for(Long userID : lastUserIDList){
			UserGroup.createUserGroup(userID,groupID);
		}
		isSaved = true;
		isClosed = true;
	}

	//choose the arg num of users from the arg users.
	//return chosen user IDs
	List<Long> chooseUsers(List<Long> users, int num){
		//for debug.
		System.out.println("choosing users...");

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

	int getSpace(){
		return capacity - lastUserIDList.size();
	}

	void addRestUser(long userID){
		lastUserIDList.add(userID);
	}
}
