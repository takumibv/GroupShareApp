package models;

import play.*;
import play.db.jpa.*;
import models.Group;
import models.User;

import javax.persistence.*;
import java.util.*;

@Entity
public class Project extends Model {
	public String name;
	public long owner_id;
	public Date deadline;
	public int assign_system;
	public int wish_limit;
	public String invitation_code;
}
