package models;

import play.*;
import play.db.jpa.*;
import models.Group;
import models.Project;
import models.User;

import javax.persistence.*;
import java.util.*;

@Entity
public class Wish extends Model {

	public Long user_id;

	public Long group_id;

	public int rank;

	public Wish(Long user_id, Long group_id, int rank){
		this.user_id = user_id;
		this.group_id = group_id;
		this.rank = rank;
	}		
	
	public static void createWish(Long user_id, Long group_id, int rank){
		new Wish(user_id, group_id, rank).save();
	}

	public static void resetWishByUserID(long user_id){
		List<Wish> wishes = Wish.find("user_id=?", user_id).fetch();
		for(Wish w : wishes){
			w.delete();
		}
	}

	public static List<User> getUsers(Long group_id, int rank){
		return Wish.find("group_id=? AND rank=?", group_id, rank).fetch();
	}
}
