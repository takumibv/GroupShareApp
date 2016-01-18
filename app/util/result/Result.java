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
			System.out.println("starting result calculation of project : " + project.name);
			UserGroupAssignor uga = new UserGroupAssignor(project.id);
			uga.assign();
		}
	}

	private List<Project> projectsOfOverDeadLine(){
		List<Project> unFinishedProjects = UserProject.getUnFinishedProjects();
		return Project.projectsOfOverDeadLine(unFinishedProjects);
	}
}