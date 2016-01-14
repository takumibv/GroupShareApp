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
	public int trash;
	public int allocation_method;
	public int public_user;
	public int public_number;
	public String detail;
	public String deadline_ymd;
	public String deadline_hm;

	public Project(String name, String detail, Long owner_id, Date deadline, int assign_system, int wish_limit, int trash, int allocation_method, int public_user, int public_number, String deadline_ymd, String deadline_hm){
		this.name = name;
		this.detail = detail;
		this.owner_id = owner_id;
		this.deadline = deadline;
		this.assign_system = assign_system;
		this.wish_limit = wish_limit;
		this.trash = trash;
		this.allocation_method = allocation_method;
		this.public_user = public_user;
		this.public_number = public_number;
		this.deadline_ymd = deadline_ymd;
		this.deadline_hm = deadline_hm;
	}

	public static Project getProjectByID(long projectID){
		Project p = Project.find("id = ?", projectID).first();
		return p;
	}

	public static Project makeProject(String name, String detail, Long owner_id, Date deadline, int assign_system, int wish_limit, int trash, int allocation_method, int public_user, int public_number, String deadline_ymd, String deadline_hm){
		Project newProject = new Project(name, detail, owner_id, deadline, assign_system, wish_limit, trash, allocation_method, public_user, public_number, deadline_ymd, deadline_hm);
		newProject.save();
		setInvitationCode(newProject.getId());
		return newProject;
	}

	public static void setInvitationCode(Long id){
		Project p = Project.find("ID = ?", id).first();
		String suffix = "@" + String.valueOf(p.id);
		String random_code = DigestGenerator.getSHA256(id.toString()).substring(0,6);
		p.invitation_code = random_code + suffix;
		p.save();
	}

	public static List<Project> getMakedProject(Long owner_id){
		List<Project> ret = Project.find("owner_id = ?", owner_id).fetch();
		return ret;
	}
}
