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
		User owner = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
		ArrayList<Project> Inviteted_notRegistered = UserProject.findProject(owner.getId(), false, false);
		ArrayList<Project> Inviteted_Registered = UserProject.findProject(owner.getId(), true, false);
		ArrayList<Project> Finished_notRegistered = UserProject.findProject(owner.getId(), false, true);
		ArrayList<Project> Finished_Registered = UserProject.findProject(owner.getId(), true, true);
        List<Project>      maked_project = Project.getMakedProject(owner.getId());
		renderArgs.put("InR", Inviteted_notRegistered);
		renderArgs.put("IR", Inviteted_Registered);
		renderArgs.put("FnR", Finished_notRegistered);
        renderArgs.put("FR", Finished_Registered);
		renderArgs.put("MP", maked_project);
        render();
    }

    // プロジェクト作成ページ
    public static void makeProject() {
        User owner = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
        List<Project> maked_project = Project.getMakedProject(owner.getId());
        renderArgs.put("MP", maked_project);
        render();
    }

    // プロジェクト編集ページ
    public static void editProject(Long id) {
        Project             project         = Project.find("id = ?", id).first();
        List<UserProject>   user_projects   = UserProject.find("project_id = ?", id).fetch();
        List<Group>         groups          = Group.getGroupListByProjectID(id);
        ArrayList<User>     users           = new ArrayList<User>();
        HashMap<Long, Integer> user_score   = new HashMap<Long, Integer>();
        for(UserProject u : user_projects){
            User user = User.find("id = ?", u.user_id).first();
            users.add(user);
            user_score.put(u.user_id, u.score);
        }
        
        renderArgs.put("project", project);
        renderArgs.put("users", users);
        renderArgs.put("user_score", user_score);
        renderArgs.put("groups", groups);

        render();
    }

    // プロジェクト詳細ページ
    public static void project(Long id) {
	    final long projectID = id;
	    Project project = Project.getProjectByID(projectID);

	    List<Group> groups = Group.getGroupListByProjectID(projectID);

	    renderArgs.put("projectName", project.name);
	    renderArgs.put("projectDeadLine", project.deadline);
	    renderArgs.put("groups", groups);
        render();
    }

    // グループ登録ページ
    public static void register(Long id) {
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
        Integer group_num               = Integer.parseInt(params.get("group-num"));    // グループの個数
        Integer user_num                = Integer.parseInt(params.get("user-num"));     // ユーザの個数

        for(int i=0; i<group_num; i++){
            System.out.println("グループ名：" + params.get("group-"+ i +"[name]"));
            System.out.println("定員：" + params.get("group-"+ i +"[capacity]"));
            System.out.println("詳細：" + params.get("group-"+ i +"[detail]"));
        }

	    validation.required(name);
	    validation.required(deadline);
	    validation.required(assign_system);
		validation.required(wish_limit);

		//あとから消す
		String detail = "";
		int trash = 1;
		int allocation_method = 1;
		int public_user = 1;
		int public_number = 1;

		User owner = User.find("name = ?", session.get(SESSION_KEY_USER)).first();

		Project p = Project.makeProject(name, detail, owner.getId(),  deadline, assign_system, wish_limit, trash,  allocation_method, public_user, public_number);
		System.out.println(p.name + "\n" + p.owner_id + "\n" + p.deadline + "\n" + p.assign_system + "\n" + p.wish_limit + "\n" + p.invitation_code);

		final long projectID = p.id;
		//create Group
		for(int i=0; i<group_num; i++){
			String groupName = params.get("group-"+ i +"[name]");
			int groupCapacity = Integer.valueOf(params.get("group-"+ i +"[capacity]"));
			String groupDetail = params.get("group-"+ i +"[detail]");

			Group.createGroup(groupName, groupDetail, groupCapacity, projectID);
		}

		//create UserGroup


        for(int i=0; i<user_num; i++){
            User addUser = User.find("name = ?", params.get("user-"+ i +"[name]")).first();
						UserProject.createUserProject(addUser.getId(), p.getId(),
	            Integer.parseInt(params.get("user-"+ i +"[score]")));
						System.out.println(addUser.getId() + "\n" +  p.getId() + "\n" + 
	            Integer.parseInt(params.get("user-"+ i +"[score]")));
        }
			mypage();
    }

    // 登録を保存する
    public static void saveRegistration(){
    	mypage();
    }

    // ユーザ名が存在するかを返す
    public static void isExistsUser(String name){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("isExists", User.isExists(name));
        renderJSON(result);
    }

    public static void news(){
		User user = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
		List<News> news = News.getAllNews(user.getId());
    	render(news);
    }
}
