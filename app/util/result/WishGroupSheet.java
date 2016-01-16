package util.result;

import models.User;
import models.Wish;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 大貴 on 2016/01/14.
 */
public abstract class WishGroupSheet extends GroupSheet{
	private List<Long> curRankUserIDList = new ArrayList<>();

	public WishGroupSheet(long projectID, long groupID){
		super(projectID, groupID);
	}

	//when not closed
	public void fillWithUsers(List<User> unFinishedRegisteredUsers, int rank){
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

	//choose the num of users from the users.(if users.size() > num).
	//return chosen user IDs
	protected abstract List<Long> chooseUsers(List<Long> users, int num);
}
