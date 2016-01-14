package models;

import play.*;
import play.db.jpa.*;
import models.Group;
import models.Project;
import models.User;

import javax.persistence.*;
import java.util.*;

@Entity
public class UserProject extends Model {

	public Long user_id;
	public Long project_id;
	public boolean registered;
	public boolean finished;
	public int score;

	public UserProject(Long user_id, Long project_id, int score){
		this.user_id = user_id;
		this.project_id = project_id;
		this.registered = false;
		this.finished = false;
		this.score = score;
	}

	public static void createUserProject(Long user_id, Long project_id, int score){
		UserProject newUsrPro = new UserProject(user_id, project_id, score);
		newUsrPro.save();
		News.createNews(new Date(), user_id, project_id, 1); 
	}

	public static ArrayList<Project> findProject(Long user_id, boolean registered,  boolean finished){
		List<UserProject> list = UserProject.find("user_id = ? AND registered = ? AND finished = ?", user_id, registered, finished).fetch();
		if(list.size() <= 0)return new ArrayList<Project>();
		ArrayList<Project> ret = new ArrayList<Project>();
		for(UserProject up : list){
			Project addProject = Project.find("ID = ?", up.project_id).first();
			ret.add(addProject);
		}
		return ret;
	}

	public static boolean checkUserProject(Long user_id, Long project_id){
		List<UserProject> list = UserProject.find("project_id = ?", project_id).fetch();
		for(UserProject up : list){
			if(up.user_id == user_id)return true;
		}
		return false;
	}
}
