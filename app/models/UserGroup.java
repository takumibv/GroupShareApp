package models;

import play.*;
import play.db.jpa.*;
import models.Group;
import models.Project;
import models.User;

import javax.persistence.*;
import java.util.*;

@Entity
public class UserGroup extends Model {

	public Long user_id;

	public Long group_id;

	public UserGroup(Long user_id, Long group_id){
		this.user_id = user_id;
		this.group_id = group_id;
	}

	public static UserGroup createUserGroup(Long user_id, Long group_id){
		UserGroup userGroup = new UserGroup(user_id, group_id);
		userGroup.save();
		return userGroup;
	}

	public static List<User> getUsersByGroupID(Long groupID){
		List<UserGroup> userGroups = UserGroup.find("group_id=?", groupID).fetch();

		List<User> users = new ArrayList<>(userGroups.size());
		for(UserGroup ug : userGroups){
			users.add(User.getUserByID(ug.user_id));
		}

		return users;
	}

	public static Group getGroupByUserProjectId(Long user_id, Long project_id){
		List<UserGroup> ug_list = UserGroup.find("user_id = ?", user_id).fetch();
		Group result = null;
		for(UserGroup ug : ug_list){
			if(project_id == ug.getProjectId()) result = Group.find("id = ?", ug.user_id).first();
		}
		return result;
	}

	public long getProjectId(){
		Group g = Group.find("id = ?", group_id).first();
		return g.project_id;
	}

}