package controllers;

import models.*;
import play.data.validation.Error;
import play.mvc.Before;
import play.mvc.Controller;
import java.util.*;


public class Application extends Controller {
	private final static String SESSION_KEY_USER = "username";
	private final static String SESSION_KEY_LOGIN_STATUS = "login_status";
	private final static String SESSION_LOGIN = "login";
	private final static String SESSION_LOGOUT = "logout";
	private final static String SESSION_MAKEPROJECT_ID = "-1";


	@Before(unless={"index", "signup", "makeAccount", "signin"})
	public static void loginedUserOnlyPage(){
		boolean isLogin;
		final String session_login_status = session.get(SESSION_KEY_LOGIN_STATUS);

		if (session_login_status == null || session_login_status.equals(SESSION_LOGOUT)) {
			isLogin = false;
		}
		//else if(session_login_status.equals(SESSION_LOGIN))
		else{
			isLogin = true;
		}

		if(!isLogin){
			index();
		}
	}


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
				User owner = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
				Project newProject = Project.createNewProject(owner.getId());
				session.put(SESSION_MAKEPROJECT_ID, newProject.getId().toString());
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
        else if(!User.createUser(name, password)){
	        //cannot create user: the user name already exists.
            signup();
        }
        else{
    		session.put(SESSION_KEY_USER, name);
    		session.put(SESSION_KEY_LOGIN_STATUS, SESSION_LOGIN);
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
		    session.put(SESSION_KEY_LOGIN_STATUS, SESSION_LOGIN);
            mypage();
    	}
    	//signin failed
    	else{
    		session.put(SESSION_KEY_USER, name);
    		session.put(SESSION_KEY_LOGIN_STATUS, SESSION_LOGOUT);
    		index();
    	}
    }

    // パスワードを変更する
    public static void changePass(String password, String password_conf){
	    validation.required(password);
	    validation.required(password_conf);
	    validation.equals(password, password_conf);

	    if(validation.hasErrors()) {
		    for(Error error : validation.errors()) {
			    System.out.println(error.message());
		    }
		    setting();
	    }
	    else{
		    if(!User.changePass(session.get(SESSION_KEY_USER), password)){
			    setting();
		    }
		    mypage();
	    }
    }

    // ログアウトする
    public static void logout(){
		session.put(SESSION_KEY_LOGIN_STATUS, SESSION_LOGOUT);
    	index();
    }

    // 退会する
    public static void signout(){
	    User.signOut(session.get(SESSION_KEY_USER));
	    session.put(SESSION_KEY_LOGIN_STATUS, SESSION_LOGOUT);
    	index();
    }

	// プロジェクトを保存する
	public static void saveProject(String name, Date deadline, int assign_system, int wish_limit){
        String project_name             = params.get("project[name]");
        String project_deadline         = params.get("project[deadline]");
        String project_assign_system    = params.get("project[assign_system]");
        String project_wish_limit       = params.get("project[wish_limit]");
        Integer group_num               = Integer.parseInt(params.get("group-num"));    // グループの個数
        Integer user_num                = Integer.parseInt(params.get("user-num"));     // ユーザの個数

        for(int i=0; i<group_num; i++){
            System.out.println("グループ名：" + params.get("group-"+ i +"[name]"));
            System.out.println("定員：" + params.get("group-"+ i +"[capacity]"));
            System.out.println("詳細：" + params.get("group-"+ i +"[detail]"));
        }

        for(int i=0; i<user_num; i++){
            System.out.println("ユーザ名：" + params.get("user-"+ i +"[name]"));
            System.out.println("点数：" + params.get("user-"+ i +"[score]"));
        }

	    validation.required(name);
	    validation.required(deadline);
	    validation.required(assign_system);
	    validation.required(wish_limit);
			Project.editProject(Long.parseLong(session.get(SESSION_MAKEPROJECT_ID)),
													name, deadline, assign_system, wish_limit,
													Project.makeInvitationCode(session.get(SESSION_MAKEPROJECT_ID)));
			System.out.println(session.get(SESSION_MAKEPROJECT_ID) + "\n"
+ name + "\n" + deadline + "\n" + assign_system + "\n" + wish_limit + "\n" + Project.makeInvitationCode(session.get(SESSION_MAKEPROJECT_ID)));
			mypage();
    }

    // 登録を保存する
    public static void saveRegistration(){
    	mypage();
    }
}
