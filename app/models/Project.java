package models;

import play.*;
import play.db.jpa.*;
import models.Group;
import models.User;
import util.DigestGenerator;

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

	public static Project createNewProject(Long owner_id){
		Project newProject = new Project(null, owner_id, new Date(), -1, -1, null);
		newProject.save();
		return newProject;
	}

	public static void editProject(Long id, String name, Date deadline, int assign_system, int wish_limit, String invitation_code){
		Project project = Project.find("ID = ?", id).first();
		project.name = name;
		project.deadline = deadline;
		project.assign_system = assign_system;
		project.wish_limit = wish_limit;
		project.invitation_code = invitation_code;
		project.save();
	}

	public static String makeInvitationCode(String id){
		return DigestGenerator.getSHA256(id).substring(0,6);
	}
	
	public static int getWishLimit(long id){
		Project project = Project.find("id=?", id).first();
		return project.wish_limit;
	}
}
