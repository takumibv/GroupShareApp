package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	public static void resetWishByUserID(long user_id, long project_id){
		List<Wish> wishes = Wish.find("user_id=?", user_id).fetch();
		for(Wish w : wishes){
			Group g = Group.find("ID=?", w.group_id).first();
			if(g.project_id == project_id)w.delete();
		}
	}

	public static List<User> getUsers(Long group_id, int rank){
		List<Wish> wishes = Wish.find("group_id=? AND rank=?", group_id, rank).fetch();
		List<User> users = new ArrayList<>(wishes.size());
		for(Wish w : wishes){
			users.add(User.getUserByID(w.user_id));
		}
		return users;
	}

	public static List<Long> getGroupIDsSortedByRank(Long user_id){
		List<Wish> wishes = Wish.find("user_id=?", user_id).fetch();

		Collections.sort(wishes, new Comparator<Wish>() {
			@Override
			public int compare(Wish o1, Wish o2) {
				int r1 = o1.rank;
				int r2 = o2.rank;

				if(r1 < r2)return -1;
				else if(r1 > r2)return 1;
				else return 0;
			}
		});

		List<Long> ids = new ArrayList<>(wishes.size());
		for(Wish wish : wishes){
			ids.add(wish.group_id);
		}

		return ids;
	}

	public long getProjectId(){
		Group g = Group.find("id = ?", group_id).first();
		return g.project_id;
	}

	public String getGroupName(){
		Group g = Group.find("id = ?", group_id).first();
		return g.name;
	}
}
