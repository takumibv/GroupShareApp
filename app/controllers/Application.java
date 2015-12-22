package controllers;

import models.User;
import play.data.validation.Error;
import play.mvc.Before;
import play.mvc.Controller;


public class Application extends Controller {
	private final static String SESSION_KEY_USER = "username";
	private final static String SESSION_KEY_LOGIN_STATUS = "login_status";
	private final static String SESSION_LOGIN = "login";
	private final static String SESSION_LOGOUT = "logout";


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
