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
	
	public static String createHashedPassword(String password) {
		return DigestGenerator.getSHA256(password);
	}
	
	public static boolean isAbleToLogin(String name, String password) {
		if(!isExists(name)){
			return false;
		}
		
		List<User> users = User.find("name=?", name).fetch();
		final User user = users.get(0);
		
		if(!user.isPasswordEqual(password)){
			return false;
		}
		
		return true;		
	}
	
	public static boolean isExists(String name){
		List<User> users = User.find("name=?", name).fetch();
		return (users != null) && (users.size() == 1);
	}
	
	private boolean isPasswordEqual(String password){
		return this.password.equals(createHashedPassword(password));
	}

}