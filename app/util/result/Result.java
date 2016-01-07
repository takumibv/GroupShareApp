package util.result;

import models.Project;
import models.UserProject;

import java.util.List;

/**
 * Created by 大貴 on 2016/01/08.
 */
public class Result {

	public void updateResult(){
		List<Project> projects = projectsOfOverDeadLine();

		for(Project project : projects){
			UserGroupAssignor uga = new UserGroupAssignor(project.id);
			uga.assign();
		}
	}

	private List<Project> projectsOfOverDeadLine(){
		List<Project> unFinishedProjects = UserProject.getUnFinishedProjects();
		return Project.overDeadLineProjects(unFinishedProjects);
	}
}
