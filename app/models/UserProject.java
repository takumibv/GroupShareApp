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

	public boolean is_wish;

	public int score;

	public UserProject(Long user_id, Long project_id, int score){
		this.user_id = user_id;
		this.project_id = project_id;
		this.is_wish = false;
		this.score = score;
	}

	public static void createUserProject(Long user_id, Long project_id, int score){
		UserProject newUsrPro = new UserProject(user_id, project_id, score);
		newUsrPro.save();
	}
}
