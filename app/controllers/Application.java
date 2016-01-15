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


	@Before(unless={"index", "signup", "makeAccount", "signin", "isExistsUser"})
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
				User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
				Project p = Project.find("ID = ?", id).first();
				if(p.owner_id != u.getId()){
					mypage();
				}
        render();
    }

    // プロジェクト詳細ページ
    public static void project(Long id) {
			User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
			Project p = Project.find("ID = ?", id).first();
			if(p.owner_id != u.getId() && !UserProject.checkUserProject(u.getId(), id)){
				mypage();
			}
	    final long projectID = id;
	    Project project = Project.getProjectByID(projectID);
        User owner = User.find("id = ?",project.owner_id).first();

	    List<Group> groups = Group.getGroupListByProjectID(projectID);
        List<User> users  = UserProject.getUsersByProjectID(projectID);
        System.out.println("あああ："+users.size());

        renderArgs.put("project", project);
        renderArgs.put("u", u);
        renderArgs.put("owner_name", owner.name);
        renderArgs.put("groups", groups);
	    renderArgs.put("joinUsers", users);
        render();
    }

    // グループ登録ページ
    public static void register(Long id) {
			User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
			Project p = Project.find("ID = ?", id).first();
			if(!UserProject.checkUserProject(u.getId(), id)){
				mypage();
			}
	    render();
    }

    // 結果ページ
    public static void result(Long id) {
			User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
			Project p = Project.find("ID = ?", id).first();
			if(p.owner_id != u.getId() && !UserProject.checkUserProject(u.getId(), id)){
				mypage();
			}
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
	public static void saveProject(String name, String detail, Date deadline, int assign_system, int wish_limit, int trash, int allocation_method, int public_user, int public_register_user, int public_register_number){
        Integer group_num               = Integer.parseInt(params.get("group-num"));    // グループの個数
        Integer user_num                = Integer.parseInt(params.get("user-num"));     // ユーザの個数

	    validation.required(name);
	    validation.required(deadline);
	    validation.required(assign_system);
		validation.required(wish_limit);

		User owner = User.find("name = ?", session.get(SESSION_KEY_USER)).first();

		Project p = Project.makeProject(name, detail, owner.getId(),  deadline, assign_system, wish_limit, trash,  allocation_method, public_user, public_register_user, public_register_number);
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

		// 招待コードが有効かを返し、有効ならばUserProjectを作成する
		public static void isValidInvitationCode(String invitation_code){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("isValid", Project.isValidInvitationCode(invitation_code));
        renderJSON(result);
		}

		// 招待コードに対応するProjectの情報を返す
		public static void informationProject(String invitation_code){
				Project p = Project.find("invitation_code = ?", invitation_code).first();
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("project_id", p.getId());
				result.put("project_name", p.name);
				result.put("project_deadline", p.deadline);
				renderJSON(result);
		}

    public static void news(){
		User user = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
		List<News> news = News.getAllNews(user.getId());
    	render(news);
    }
}
