package util.result;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 大貴 on 2016/01/08.
 */
public abstract class GroupSheet {
	protected final long projectID;
	protected final long groupID;
	protected final int capacity;

	private boolean isClosed;

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
			return;
		}

		//users of nth-rank wish
		List<User> tmp = Wish.getUsers(groupID, rank);
		//remove other rank users
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

			createUserGroup();
			finishUserProject();

			isClosed = true;
		}
		else if(tmpUserListSize > capacity){
			List<Long> chosenUsers = chooseUsers(curRankUserIDList, getSpace());
			lastUserIDList.addAll(chosenUsers);

			createUserGroup();
			finishUserProject();

			isClosed = true;
		}
		//not overflow
		else{
			lastUserIDList.addAll(curRankUserIDList);
			finishUserProject();
		}
	}

	boolean isClosed(){
		return isClosed;
	}

	//when closed
	void createUserGroup(){
		if(isClosed())return;
		for(Long userID : lastUserIDList){
			UserGroup.createUserGroup(userID,groupID);
		}
	}

	private void finishUserProject(){
		for(Long userID : lastUserIDList) {
			UserProject.finish(projectID, userID);
		}
	}

	//choose the num of users from the users.(if users.size() > num).
	//return chosen user IDs
	protected abstract List<Long> chooseUsers(List<Long> users, int num);

	int getSpace(){
		return capacity - lastUserIDList.size();
	}

	void addRestUser(long userID){
		lastUserIDList.add(userID);
	}
}
