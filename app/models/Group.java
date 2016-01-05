package models;

import play.*;
import play.db.jpa.*;
import models.User;
import models.Project;

import javax.persistence.*;
import java.util.*;

@Entity
public class Group extends Model {
  public String name;
  public String detail;
  public int capacity;
  public long project_id;

  public Group(String name, String detail, int capacity, long project_id){
    this.name = name;
    this.detail = detail;
    this.capacity = capacity;
    this.project_id = project_id;
  }

	public static boolean createGroup(String name, String detail, int capacity, long project_id){
		Group newGroup = new Group(name, detail, capacity, project_id);
		newGroup.save();
		return true;
	}
	
	public static List<Group> getGroupListByProjectID(long projectID){
		return Group.find("project_id=?", projectID).fetch();
	}
}
