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
	public int trash;					// 1:すべての志望が通らなかった人をランダムで空いてるグループに振り分ける、2:振り分けない
	public int allocation_method;		// 1:志望を優先、2:点数を優先
	public int public_user;				// 1:参加ユーザが他の参加ユーザの名前を閲覧可能、2:閲覧不可
	public int public_register_user;	// 1:グループに誰が登録したのかを閲覧可能、2:閲覧不可
	public int public_register_number;	// 1:グループに何人登録したのかを閲覧可能、2:閲覧不可
	public String detail;
	public String deadline_ymd;
	public String deadline_hm;
	public boolean valid;


	public Project(String name, String detail, Long owner_id, Date deadline, int assign_system, int wish_limit, int trash, int allocation_method, int public_user, int public_register_user, int public_register_number, String deadline_ymd, String deadline_hm){
		this.name = name;
		this.detail = detail;
		this.owner_id = owner_id;
		this.deadline = deadline;
		this.assign_system = assign_system;
		this.wish_limit = wish_limit;
		this.trash = trash;
		this.allocation_method = allocation_method;
		this.public_user = public_user;
		this.public_register_user = public_register_user;
		this.public_register_number = public_register_number;
		this.deadline_ymd = deadline_ymd;
		this.deadline_hm = deadline_hm;
		this.valid = true;
	}

	public static Project getProjectByID(long projectID){
		Project p = Project.find("id = ?", projectID).first();
		return p;
	}

	public static Project makeProject(String name, String detail, Long owner_id, Date deadline, int assign_system, int wish_limit, int trash, int allocation_method, int public_user, int public_register_user, int public_register_number, String deadline_ymd, String deadline_hm){
		Project newProject = new Project(name, detail, owner_id, deadline, assign_system, wish_limit, trash, allocation_method, public_user, public_register_user, public_register_number, deadline_ymd, deadline_hm);
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
	
	public static int getWishLimit(long id){
		Project project = Project.find("id=?", id).first();
		return project.wish_limit;
	}

	public static List<Project> projectsOfOverDeadLine(List<Project> projects){
		final Date now = new Date();

		List<Project> overDeadLineProjects = new ArrayList<>();
		for(Project project : projects){
			if(now.after(project.deadline)){
				overDeadLineProjects.add(project);
			}
		}

		return overDeadLineProjects;
	}

	public static List<Project> getMakedProject(Long owner_id){
		List<Project> ret = Project.find("owner_id = ?", owner_id).fetch();
		return ret;
	}

	public String getDeadlineTime(){
		return (deadline.getMonth()+1) +"/"+ deadline.getDate() +" "+ String.format("%1$02d", deadline.getHours()) +":"+ String.format("%1$02d", deadline.getMinutes());
	}

	public Boolean isFinished(){
		Date now = new Date();
		return now.after(deadline);
	}

	public void createNewsType2and3(){
		News.createNews(new Date(), this.owner_id, this.getId(), 3);
		List<User> list = UserProject.getUsersByProjectID(this.getId());
		for(User u : list){
			News.createNews(new Date(), u.getId(), this.getId(), 2);
		}
	}

	public static List<Project> getNotValidProjects(){
		List<Project> p_list = Project.find("").fetch();
		List<Project> ret = new ArrayList<Project>();
		List<UserProject> up_list;
		for(Project p : p_list){
			if(p.isFinished()){
				up_list = UserProject.find("project_id = ?", p.getId()).fetch();
				if(up_list.isEmpty()){
					ret.add(p);
					p.valid = false;
				}
			}
		}
		return ret;
	}
}
