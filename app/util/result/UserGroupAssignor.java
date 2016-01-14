package util.result;

import models.*;

import java.util.*;

/**
 * Created by 大貴 on 2016/01/07.
 */
public class UserGroupAssignor {
	private final long projectID;
	private final List<Group> groups;
	private final Map<Long, GroupSheet> assignBoard;
	private final AllocationMethod allocationMethod;

	private final int wishLimit;
	private final int trash;


	public UserGroupAssignor(long projectID){
		this.projectID = projectID;

		groups = Group.getGroupListByProjectID(projectID);
		assignBoard = new HashMap<>(groups.size());

		Project project = Project.getProjectByID(projectID);
		wishLimit  = Project.getWishLimit(projectID);
		trash = project.trash;


		/*
		 * init assignBoard
		 */

		//wish first
		if(project.allocation_method == 1) {
			allocationMethod = new WishFirstAssignor();

			switch (project.assign_system) {
				case 1://score
					for (Group group : groups) {
						assignBoard.put(group.id, new AssignByWishScore(projectID, group.id));
					}
					break;
				case 2://janken
					for (Group group : groups) {
						assignBoard.put(group.id, new AssignByWishJanken(projectID, group.id));
					}
					break;
			}
		}
		//score first
		else{
			allocationMethod = new ScoreFirstAssignor();
			for (Group group : groups) {
				assignBoard.put(group.id, new GroupSheet(projectID, group.id));
			}
		}
	}


	public void assign(){
		allocationMethod.assign();

		//for unlucky users
		if(trash == 1) {
			assignRestUsers(UserProject.unFinishedRegisteredUsers(projectID));
		}

		//finish rest UserProjects
		UserProject.finish(projectID);
	}

	private void assignRestUsers(List<User> unLuckyUsers){
		List<GroupSheet> restGroupSheets = new ArrayList<>();
		
		//get groupSheets which still have space
		for(GroupSheet groupSheet : assignBoard.values()){
			if(!groupSheet.isClosed()){
				restGroupSheets.add(groupSheet);
			}
		}

		//add rest users to proper group sheets. 
		for(User user : unLuckyUsers){
			addRestUserToMinGroupSheet(restGroupSheets, user);
		}
	}

	private void addRestUserToMinGroupSheet(List<GroupSheet> restGroupSheets, User user){
		int maxSpace = Integer.MIN_VALUE;
		GroupSheet minGroupSheet = null;
		for(GroupSheet groupSheet : restGroupSheets){
			if(maxSpace < groupSheet.getSpace()){
				maxSpace = groupSheet.getSpace();
				minGroupSheet = groupSheet;
			}
		}

		assert minGroupSheet != null;

		minGroupSheet.addUser(user.id);
	}


	private interface AllocationMethod {
		void assign();
	}

	private class WishFirstAssignor implements AllocationMethod{

		public void assign(){
			//for users assigned by their wishes
			for(int rank=1; rank<=wishLimit; rank++) {
				fillGroupSheets(UserProject.unFinishedRegisteredUsers(projectID), rank);
			}
		}

		private void fillGroupSheets(List<User> unFinishedRegisteredUsers, int rank){
			for(GroupSheet groupSheet : assignBoard.values()){
				WishGroupSheet wishGroupSheet = (WishGroupSheet)groupSheet;
				wishGroupSheet.fillWithUsers(unFinishedRegisteredUsers, rank);
			}
		}
	}

	private class ScoreFirstAssignor implements AllocationMethod{

		public void assign(){
			List<User> users = UserProject.unFinishedRegisteredUsers(projectID);

			Collections.sort(users, new Comparator<User>() {
				@Override
				public int compare(User o1, User o2) {
					int score1 = UserProject.getUserScore(projectID, o1.id);
					int score2 = UserProject.getUserScore(projectID, o2.id);

					if(score1 < score2) return -1;
					else if(score1 > score2) return 1;
					else return 0;
				}
			});

			Collections.reverse(users);

			for(User user : users){
				List<Long> groupIDsSortedByRank = Wish.getGroupIDsSortedByRank(user.id);

				//try to add user into all wish ranks of groupSheet
				for(long id : groupIDsSortedByRank){
					GroupSheet groupSheet = assignBoard.get(id);

					if(!groupSheet.isClosed()){
						groupSheet.addUser(user.id);
						break;
					}
				}

			}

		}
	}
}
