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

	public static Group getGroupById(Long id){
		return Group.find("id=?", id).first();
	}

	public static Group getGroupByUserProjectId(Long user_id, Long project_id){
		List<UserGroup> ug_list = UserGroup.find("user_id = ?", user_id).fetch();
		Group result = null;
		for(UserGroup ug : ug_list){
			if(project_id == ug.getProjectId()) result = Group.find("id = ?", ug.group_id).first();
		}
		return result;
	}

	public void setAttributes(String name, String detail, int capacity){
		this.name = name;
		this.detail = detail;
		this.capacity = capacity;
	}

	public void deleteWithWishes(){
		long group_id = this.getId();
		List<Wish> wishes = Wish.find("group_id = ?", group_id).fetch();
		for (int j = 0, n = wishes.size(); j < n; j++){
			Wish wish = wishes.get(j);
			News news = new News(new Date(), wish.user_id, this.project_id, 4);
			wish.delete();
			news.save();
		}

		this.delete();
	}
}
