package controllers;

import play.*;
import play.mvc.*;
import models.Group;
import models.Project;
import models.User;

import models.*;

import javax.persistence.*;
import java.util.*;


public class Application extends Controller {

    // トップページ
    public static void index() {
        render();
    }

    // アカウント登録ページ
    public static void signup() {
        render();
    }

    // 設定ページ
    public static void setting() {
        render();
    }

    // マイページ
    public static void mypage() {
        render();
    }

    // プロジェクト作成ページ
    public static void makeProject() {
        render();
    }

    // プロジェクト編集ページ
    public static void editProject() {
        render();
    }

    // プロジェクト詳細ページ
    public static void project() {
        render();
    }

    // グループ登録ページ
    public static void register() {
        render();
    }

    // 結果ページ
    public static void result() {
        render();
    }

    // アカウントを作成する
    public static void makeAccount(){
    	mypage();
    }

    // サインインする
    public static void signin() {
        mypage();
    }

    // パスワードを変更する
    public static void changePass(){
    	mypage();
    }

    // ログアウトする
    public static void logout(){
    	index();
    }

    // 退会する
    public static void signout(){
    	index();
    }

		// プロジェクトを保存する
		public static void saveProject(){ 
			// プロジェクト保存
			Project p;
			/*project保存*/
			p.save();

			// ユーザプロジェクト追加
			UserProject up;
			for(int i = 0; i < Integer.parseInt(params.get("user_size"));i++){
				if(User.find("name = ?", params.get("user-" + i + "name").count > 0){
					up = new UserProject(User.find("name = ?", params.get("user-" + i + "name")).first().id, p.id);
					up.save();
				}
			}
			
			// グループ追加
			Group g;
			for(int i = 0; i < Integer.parseInt(params.get("group_size"));i++){
				g = new Group(params.get("Group-" + i + "name")
					, params.get("Group-" + i + "detail")
					, Integer.parseInt(params.get("Group-" + i + "capacity"))
					, p.id);
				g.save();
			}

			mypage();
    }

    // 登録を保存する
    public static void saveRegistration(){
    	mypage();
    }
}
