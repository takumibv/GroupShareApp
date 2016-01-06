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


}