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

}