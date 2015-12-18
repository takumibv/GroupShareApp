package controllers;

import java.util.List;

import models.User;
import play.data.validation.Error;
import play.mvc.Controller;


public class Application extends Controller {
	private final static String SESSION_KEY_USER = "username";
	private final static String SESSION_KEY_LOGIN_STATUS = "session_status";
	private final static String SESSION_STATUS_LOGIN = "login";
	private final static String SESSION_STATUS_LOGOUT = "logout";
	
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
    public static void makeAccount(String name, String password, String password_conf){
    	validation.required(name); 
    	validation.required(password);
    	validation.required(password_conf);
    	validation.equals(password, password_conf);
    	
        if(validation.hasErrors()) {
            for(Error error : validation.errors()) {
                System.out.println(error.message());
            }
            signup();
        }
        else if(User.isExists(name)){
            signup();
        }
        else{
        	User newUser = new User(name, password);
        	newUser.save();

    		session.put(SESSION_KEY_USER, name);
    		session.put(SESSION_KEY_LOGIN_STATUS, SESSION_STATUS_LOGIN);
          	
        	mypage();
        }
    }

    // サインインする
    public static void signin(String name, String password) {
    	validation.required(name); 
    	validation.required(password);
    	
    	//signin
    	if(User.isAbleToLogin(name, password)){
    		session.put(SESSION_KEY_USER, name);
    		session.put(SESSION_KEY_LOGIN_STATUS, SESSION_STATUS_LOGIN);
            mypage();
    	}
    	//signin failed
    	else{
    		session.put(SESSION_KEY_USER, name);
    		session.put(SESSION_KEY_LOGIN_STATUS, SESSION_STATUS_LOGOUT);
    		index();
    	}
    }

    // パスワードを変更する
    public static void changePass(){
    	mypage();
    }

    // ログアウトする
    public static void logout(){
		session.put(SESSION_KEY_LOGIN_STATUS, SESSION_STATUS_LOGOUT);
    	index();
    }

    // 退会する
    public static void signout(){
    	index();
    }

    // プロジェクトを保存する
    public static void saveProject(){
    	mypage();
    }

    // 登録を保存する
    public static void saveRegistration(){
    	mypage();
    }
}
