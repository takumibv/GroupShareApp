package models;

import play.*;
import play.db.jpa.*;
import models.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class News extends Model {

	public Date date;
	public Long user_id;
	public Long project_id;
	public int type;
	public Boolean isRead;

	public News(Date date, Long user_id, Long project_id, int type){
		this.date = date;
		this.user_id = user_id;
		this.project_id = project_id;
		this.type = type;
		this.isRead = false;
	}

	public static void createNews(Date date, Long user_id, Long project_id, int type){
		News news = new News(date, user_id, project_id, type);
		news.save();
	}

	public static void readNews(Long news_id){
		News news = News.find("ID = ?", news_id).first();
		news.isRead = true;
		news.save();
	}

	public static List<News> getNews(Long user_id, boolean isRead){
		List<News> ret = News.find("user_id = ? AND isRead = ?", user_id, isRead).fetch();
		return ret;
	}

}
