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

	public static List<News> getUnreadNews(Long user_id){
		List<News> unread_news = News.find("user_id = ? AND isRead = ?", user_id, false).fetch();

		// Make isRead true
		for (int i = 0, n = unread_news.size(); i < n; i++) {
			News news = unread_news.get(i);
			news.isRead = true;
			news.save();
		}
		return unread_news;
	}

	public static List<News> getAllNews(Long user_id){
		List<News> ret = News.find("user_id = ?", user_id).fetch();
		return ret;
	}

	public String getSentence(){
		Project project = Project.findById(this.project_id);
		switch (this.type){
			case 1:
				return "プロジェクト「" + project.name + "」に招待されました。";
			case 2:
			case 3:
				return "プロジェクト「" + project.name + "」の結果が出ました。";
			case 4:
				return "プロジェクト「" + project.name + "」の希望を出していたグループが削除されました。希望の再登録をお願いします。";
			case 5:
				return "プロジェクト「" + project.name + "」から退会させられました。";
			case 6:
				return "プロジェクト「" + project.name + "」が編集されました。";
			default:
				return "";
		}
	}

	public String getLink(){
		Project project = Project.findById(this.project_id);
		switch (this.type){
			case 1:
				return "projects/" + this.project_id;
			case 2:
			case 3:
				return "result/" + this.project_id;
			case 4:
				return "register/" + this.project_id;
			case 5:
				return "";
			case 6:
				return "projects/" + this.project_id;
			default:
				return "";
		}
	}

	// objにはProject型のみ入る
	public boolean equals(Object obj){
		if(obj != null){
			return (this.project_id == ((Project)obj).id);
		}
		return false;
	}
}
