package util.result;

import models.Group;
import models.Project;
import models.User;
import models.UserProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 大貴 on 2016/01/07.
 */
public class UserGroupAssignor {
	private final long projectID;
	private final List<Group> groups;
	private final Map<Long, GroupSheet> assignBoard;



	public UserGroupAssignor(long projectID){
		this.projectID = projectID;

		groups = Group.getGroupListByProjectID(projectID);
		assignBoard = new HashMap<>(groups.size());

		//init assignBoard
		Project project = Project.getProjectByID(projectID);
		switch (project.assign_system){
			case 1://score
				for(Group group : groups){
					assignBoard.put(group.id, new GroupSheet(projectID, group.id));
				}
				break;
			case 2://janken
				for(Group group : groups){
					assignBoard.put(group.id, new GroupSheet(projectID, group.id));
				}
				break;
		}
	}


	public void assign(){
		int wishLimit = Project.getWishLimit(projectID);
		List<User> unFinishedRegisteredUsers;
		for(int rank=1; rank<=wishLimit; rank++) {
			unFinishedRegisteredUsers = UserProject.unFinishedRegisteredUsers(projectID);
			fillGroupSheets(unFinishedRegisteredUsers, rank);
		}

		unFinishedRegisteredUsers = UserProject.unFinishedRegisteredUsers(projectID);
		assignRestUsers(unFinishedRegisteredUsers);

		for(GroupSheet groupSheet : assignBoard.values()){
			groupSheet.createUserGroup();
		}

		UserProject.finish(projectID);
	}

	void assignRestUsers(List<User> unLuckyUsers){
		List<GroupSheet> restGroupSheets = new ArrayList<>();
		for(GroupSheet groupSheet : assignBoard.values()){
			if(!groupSheet.isClosed()){
				restGroupSheets.add(groupSheet);
			}
		}

		for(User user : unLuckyUsers){
			addRestUser(restGroupSheets, user);
		}
	}

	void addRestUser(List<GroupSheet> restGroupSheets, User user){
		int maxSpace = Integer.MIN_VALUE;
		GroupSheet minGroupSheet = null;
		for(GroupSheet groupSheet : restGroupSheets){
			if(maxSpace < groupSheet.getSpace()){
				maxSpace = groupSheet.getSpace();
				minGroupSheet = groupSheet;
			}
		}

		assert minGroupSheet != null;

		minGroupSheet.addRestUser(user.id);
	}

	private void fillGroupSheets(List<User> unFinishedRegisteredUsers, int rank){
		for(GroupSheet groupSheet : assignBoard.values()){
			groupSheet.fillWithUsers(unFinishedRegisteredUsers, rank);
		}
	}

}
