package models;

import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="exp4_Group")
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

	public static Group createGroup(String name, String detail, int capacity, long project_id){
		Group newGroup = new Group(name, detail, capacity, project_id);
		newGroup.save();
		return newGroup;
	}
	
	public static List<Group> getGroupListByProjectID(long projectID){
		return Group.find("project_id=?", projectID).fetch();
	}
}
