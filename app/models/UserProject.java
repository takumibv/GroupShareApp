package models;

import play.*;
import play.db.jpa.*;
import models.Group;
import models.Project;
import models.User;
import sun.rmi.runtime.Log;

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
	}

	public static UserProject getUserProject(Long project_id, Long user_id) {
		UserProject userProject = UserProject.find("project_id=? AND user_id=?", project_id, user_id).first();
		return userProject;
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

	public static List<User> unFinishedRegisteredUsers(Long project_id){
		List<User> users = UserProject.find("project_id = ? AND registered = ? AND finished = ?", project_id, true, false).fetch();
		return users;
	}

	public static int getUserScore(Long project_id, Long user_id){
		UserProject userProject = getUserProject(project_id, user_id);
		return userProject.score;
	}

	public static List<Project> getUnFinishedProjects(){
		List<UserProject> userProjects = UserProject.find("finished=?", false).fetch();
		return getProjects(userProjects);
	}

	public static List<Project> getProjects(List<UserProject> userProjects){
		List<Long> projectIDList = new ArrayList<>();
		List<Project> projectList = new ArrayList<>();

		//create projectIDList
		for(UserProject userProject : userProjects){
			if(!projectIDList.contains(userProject.project_id)){
				long id = userProject.project_id;
				projectIDList.add(id);

				//create projectList
				projectList.add(Project.getProjectByID(id));
			}
		}
		return projectList;
	}

	public static void finish(Long project_id, Long user_id){
		UserProject userProject = getUserProject(project_id, user_id);
		userProject.finished = true;
		userProject.save();
	}

	public static void register(Long user_id, Long project_id){
		UserProject userProject = UserProject.find("user_id = ? AND project_id", user_id, project_id).first();
		userProject.registered = true;
		userProject.save();
	}
}
