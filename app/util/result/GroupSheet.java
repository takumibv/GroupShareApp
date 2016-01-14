package util.result;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 大貴 on 2016/01/08.
 */
public class GroupSheet {
	protected final long projectID;
	protected final long groupID;
	protected final int capacity;

	protected boolean isClosed;

	protected List<Long> lastUserIDList = new ArrayList<>();


	public GroupSheet(long projectID, long groupID){
		this.projectID = projectID;
		this.groupID = groupID;
		this.capacity = Group.getGroupById(groupID).capacity;
	}

	public boolean isClosed(){
		return isClosed;
	}

	//when closed
	public void createUserGroup(){
		if(isClosed())return;
		for(Long userID : lastUserIDList){
			UserGroup.createUserGroup(userID,groupID);
		}
	}

	public void finishUserProject(){
		for(Long userID : lastUserIDList) {
			UserProject.finish(projectID, userID);
		}
	}

	public int getSpace(){
		return capacity - lastUserIDList.size();
	}

	public void addUser(long userID){
		lastUserIDList.add(userID);
		UserGroup.createUserGroup(userID,groupID);
		UserProject.finish(projectID, userID);

		if(getSpace() == 0){
			isClosed = true;
		}
	}
}
