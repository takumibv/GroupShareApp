package util.result;

import models.Project;
import models.User;
import models.UserProject;

import java.util.List;

/**
 * Created by 大貴 on 2016/01/08.
 */
public class Result {

	public Result(){
	}

	public void updateResult(){
		System.out.println("updating result...");

		List<Project> projects = projectsOfOverDeadLine();

		for(Project project : projects){
			if(!project.hasUnFinishedUser()){
				System.out.println("starting result calculation of project : " + project.name);
				UserGroupAssignor uga = new UserGroupAssignor(project.id);
				uga.assign();
				project.createNewsType2and3();
			}else{
				project.createNewsType7();
			}
		}
		List<Project> p_list = Project.getNotValidProjects();
		for(Project p : p_list)p.createNewsType2and3();
	}

	private List<Project> projectsOfOverDeadLine(){
		List<Project> unFinishedProjects = UserProject.getUnFinishedProjects();
		return Project.projectsOfOverDeadLine(unFinishedProjects);
	}
}
