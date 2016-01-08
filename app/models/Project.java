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

	public Project(String name, Long owner_id, Date deadline, int assign_system, int wish_limit){
		this.name = name;
		this.owner_id = owner_id;
		this.deadline = deadline;
		this.assign_system = assign_system;
		this.wish_limit = wish_limit;
	}

	public static Project getProjectByID(long projectID){
		Project p = Project.find("id = ?", projectID).first();
		return p;
	}

	public static Project makeProject(String name, Long owner_id, Date deadline, int assign_system, int wish_limit){
		Project newProject = new Project(name, owner_id, deadline, assign_system, wish_limit);
		newProject.save();
		setInvitationCode(newProject.getId());
		return newProject;
	}

	public static void setInvitationCode(Long id){
		Project p = Project.find("ID = ?", id).first();
		p.invitation_code = DigestGenerator.getSHA256(id.toString()).substring(0,6);
		p.save();
	}

	public static List<Project> getMakedProject(Long owner_id){
		List<Project> ret = Project.find("owner_id = ?", owner_id).fetch();
		return ret;
	}
}
