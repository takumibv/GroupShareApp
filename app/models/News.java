package models;

import play.*;
import play.db.jpa.*;
import models.Group;
import models.Project;
import models.User;

import javax.persistence.*;
import java.util.*;

@Entity
public class News extends Model {

	public String name;

	public Date date;

	public Long user_id;

	public Boolean isRead;

}