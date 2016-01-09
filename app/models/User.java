package models;

import play.*;
import play.db.jpa.*;
import play.mvc.Scope;
import util.DigestGenerator;
import models.Group;
import models.Project;

import javax.persistence.*;

import java.util.*;
import java.text.SimpleDateFormat;

@Entity
public class User extends Model {
	public String name;
	public String password;
	public boolean isDeleted;
	
	public User(String name, String password){
		this.name = name;
		this.password = createHashedPassword(password);
		this.isDeleted = false;
	}

	public static User getUserByID(Long id){
		return User.find("id=?", id).first();
	}

	public static boolean createUser(String name, String password){
		if(isExists(name)){
			return false;
		}

		User newUser = new User(name, password);
		newUser.save();
		return true;
	}

	public static void signOut(String name){
		if(!isExists(name))return;

		List<User> users = User.find("name=?", name).fetch();
		final User user = users.get(0);

		user.isDeleted = true;
		user.save();
	}

	public static boolean isAbleToLogin(String name, String password) {
		if(!isExists(name)){
			return false;
		}

		List<User> users = User.find("name=?", name).fetch();
		final User user = users.get(0);

		if(user.isDeleted){
			return false;
		}
		else if(!user.isPasswordEqual(password)){
			return false;
		}
		
		return true;		
	}

	public static boolean changePass(String name, String password){
		if(!isExists(name)){
			return false;
		}

		List<User> users = User.find("name=?", name).fetch();
		final User user = users.get(0);

		user.password = createHashedPassword(password);
		user.save();

		return true;
	}
	
	public static boolean isExists(String name){
		if(name == null)return false;
		List<User> users = User.find("name=?", name).fetch();
		return (users != null) && (users.size() == 1);
	}
	
	public boolean isPasswordEqual(String password){
		return this.password.equals(createHashedPassword(password));
	}

	private static String createHashedPassword(String password) {
		return DigestGenerator.getSHA256(password);
	}
}
