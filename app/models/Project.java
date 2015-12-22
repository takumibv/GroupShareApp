package models;

import play.*;
import play.db.jpa.*;
import models.Group;
import models.User;

import javax.persistence.*;
import java.util.*;

@Entity
public class Project extends Model {
	public String name;
	public Long owner_id;
	public Date deadline;
	public int assign_system;
	public int wish_limit;
	public String invitation_code;

	public Project(String name, Long owner_id, Date deadline, int assign_system, int wish_limit, String invitation_code){
		this.name = name;
		this.owner_id = owner_id;
		this.deadline = deadline;
		this.assign_system = assign_system;
		this.wish_limit = wish_limit;
		this.invitation_code = invitation_code;
	}

}
