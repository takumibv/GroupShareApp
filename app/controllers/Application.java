package controllers;

import models.*;
import play.data.validation.Error;
import play.mvc.Before;
import play.mvc.Controller;
import util.result.Result;

import java.util.*;
import java.text.SimpleDateFormat;


public class Application extends Controller {
	private final static String SESSION_KEY_USER = "username";
	private final static String SESSION_KEY_LOGIN_STATUS = "login_status";
	private final static String SESSION_LOGIN = "login";
	private final static String SESSION_LOGOUT = "logout";
	private final static String SESSION_PROJECT_ID = "-1";


	@Before(unless={"index", "signup", "makeAccount", "signin", "isExistsUser", "resultTrigger"})
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

	@Before(unless={"index", "signup", "makeAccount", "signin", "loginedUserOnlyPage"})
	public static void resultTrigger(){
		Result result = new Result();
		result.updateResult();
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

        HashMap<Long, Group> ug_map = new HashMap<Long, Group>();
        List<UserGroup> ug_list = UserGroup.find("user_id = ?", owner.id).fetch();
        for(UserGroup ug : ug_list){
            Group g = Group.find("id = ?", ug.group_id).first();
            ug_map.put(ug.getProjectId(), g);
        }

		renderArgs.put("InR", Inviteted_notRegistered);
		renderArgs.put("IR", Inviteted_Registered);
		renderArgs.put("FnR", Finished_notRegistered);
        renderArgs.put("FR", Finished_Registered);
        renderArgs.put("MP", maked_project);
		renderArgs.put("ug_map", ug_map);

		List<News> unread_news = News.getUnreadNews(owner.getId());

        render(unread_news);
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
				session.put(SESSION_PROJECT_ID, String.valueOf(id));

        if(p.owner_id != u.getId()){
            mypage();
        }

        Project             project         = Project.find("id = ?", id).first();
        List<UserProject>   user_projects   = UserProject.find("project_id = ?", id).fetch();
        List<Group>         groups          = Group.getGroupListByProjectID(id);
        ArrayList<User>     users           = new ArrayList<User>();
        HashMap<Long, Integer> user_score   = new HashMap<Long, Integer>();
        for(UserProject usr : user_projects){
            User user = User.find("id = ?", usr.user_id).first();
            users.add(user);
            user_score.put(usr.user_id, usr.score);
        }
        
        renderArgs.put("project", project);
        renderArgs.put("users", users);
        renderArgs.put("user_score", user_score);
        renderArgs.put("groups", groups);

        render();
    }

    // プロジェクト詳細ページ
    public static void project(Long id) {
		User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
		Project p = Project.find("ID = ?", id).first();
		if(p.owner_id != u.getId() && !UserProject.checkUserProject(u.getId(), id)){
			mypage();
		}
	    final long projectID = p.id;
	    List<Group> groups = Group.getGroupListByProjectID(projectID);
        List<User> users  = UserProject.getUsersByProjectID(projectID);
        HashMap<Long, Wish> wishes_map = new HashMap<Long, Wish>();
        for(User usr : users){
            List<Wish> w_list = Wish.find("user_id = ? AND rank = 1", usr.id).fetch();
            for(Wish w : w_list){
                if(w.getProjectId() == id) wishes_map.put(usr.id, w);
            }
        }
        HashMap<Integer, Wish> my_wishes_map = new HashMap<Integer, Wish>();
        List<Wish> my_w_list = Wish.find("user_id = ?", u.id).fetch();
        for(Wish w : my_w_list){
            if(w.getProjectId() == id) my_wishes_map.put(w.rank, w);
        }

        renderArgs.put("project", p);
        renderArgs.put("u", u);
        renderArgs.put("owner_name", u.name);
        renderArgs.put("groups", groups);
        renderArgs.put("joinUsers", users);
        renderArgs.put("joinUsers", users);
        renderArgs.put("wishes_map", wishes_map);
	    renderArgs.put("my_wishes_map", my_wishes_map);
        render();
    }

    // グループ登録ページ
    public static void register(Long id) {
		User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
		Project p = Project.find("ID = ?", id).first();
		if(!UserProject.checkUserProject(u.getId(), id)){
			mypage();
		}

     	validation.required(id);

        if(validation.hasErrors()) {
            for(Error error : validation.errors()) {
                System.out.println(error.message());
            }
            mypage();
        }

	    final long projectID = id;

    	List<Group> groups = Group.getGroupListByProjectID(projectID);

     	int wishLimit = Project.getWishLimit(projectID);
     	
     	//1-origin
    	List<Integer> wishRank = new ArrayList<>(wishLimit);
    	for(int i=0; i<wishLimit; i++){
    		wishRank.add(i+1);
    	}
    	
        renderArgs.put("project", p);
        renderArgs.put("u", u);
    	renderArgs.put("projectID", projectID);
    	renderArgs.put("groups", groups);
    	renderArgs.put("wishLimit", wishLimit);
    	renderArgs.put("wishRank", wishRank);

	    render();
    }

    // 結果ページ
    public static void result(Long id) {
		User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
		Project p = Project.find("ID = ?", id).first();
		if(p.owner_id != u.getId() && !UserProject.checkUserProject(u.getId(), id)){
			mypage();
		}
        Group my_group = Group.getGroupByUserProjectId(u.id, p.id);
        
	    List<Group> groups = Group.getGroupListByProjectID(id);
        renderArgs.put("project", p);
        renderArgs.put("u", u);
        renderArgs.put("groups", groups);
	    renderArgs.put("my_group", my_group);
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
	public static void saveProject(String name, String detail, Date deadline_ymd, String deadline_hm, int assign_system, int wish_limit, int trash, int allocation_method, int public_user, int public_register_user, int public_register_number){
        Integer group_num               = Integer.parseInt(params.get("group-num"));    // グループの個数
        Integer user_num                = Integer.parseInt(params.get("user-num"));     // ユーザの個数

	    validation.required(name);
	    validation.required(deadline_ymd);
	    validation.required(deadline_hm);
	    validation.required(assign_system);
		validation.required(wish_limit);
		long hm = (Long.valueOf(deadline_hm.split(":")[0]) * 60 * 60
			+ Long.valueOf(deadline_hm.split(":")[1]) * 60) * 1000;
						
		Date deadline = new Date(deadline_ymd.getTime() + hm);

		User owner = User.find("name = ?", session.get(SESSION_KEY_USER)).first();

		Project p = Project.makeProject(name, detail, owner.getId(),  deadline, assign_system, wish_limit, trash,  allocation_method, public_user, public_register_user, public_register_number, params.get("deadline_ymd"), deadline_hm);
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

    public static void updateProject(long project_id, String name, String detail, Date deadline_ymd, String deadline_hm, int assign_system, int wish_limit, int trash, int allocation_method, int public_user, int public_register_user, int public_register_number){
		// Update project
		Project project = Project.findById(project_id);
		project.setAttributes(name, detail, deadline_ymd, deadline_hm, assign_system, wish_limit, trash, allocation_method, public_user, public_register_user, public_register_number);
		project.save();

/*
		// Delete group
		int d_group_num = Integer.parseInt(params.get("d-group-num"));
		for (int i = 0; i < d_group_num; i++){
			long group_id = Long.parseLong(params.get("d-group-"+ i +"[id]"));
			Group group = Group.findById(group_id);
			group.deleteWithWishes();
		}

		// Update or Create group!
		int group_num = Integer.parseInt(params.get("group-num"));
		for (int i = 0; i < group_num; i++){
			String group_name = params.get("group-"+ i +"[name]");
			String group_detail = params.get("group-"+ i +"[detail]");
			int group_capacity = Integer.parseInt(params.get("group-"+ i +"[capacity]"));
			Group group;

			if (params.get("group-"+ i +"[id]").equals("new")){
				group = new Group(group_name, group_detail, group_capacity, project_id);
			} else {
				long group_id = Long.parseLong(params.get("group-"+ i +"[id]"));
				group = Group.findById(group_id);
				group.setAttributes(group_name, group_detail, group_capacity);
			}
			group.save();
		}

		// Delete user_project
		int d_user_num = Integer.parseInt(params.get("d-user-num"));
		for (int i = 0; i < d_user_num; i++){
			long user_id = Long.parseLong(params.get("d-user-"+ i +"[id]"));
			UserProject user_project = UserProject.find("project_id=? AND user_id=?", project_id, user_id).first();
			user_project.deleteWithWishes();
			News news = new News(new Date(), user_id, project_id, 5);
			news.save();
		}

		// Update ot Create user_project
        int user_num = Integer.parseInt(params.get("user-num"));
		for (int i = 0; i < user_num; i++){
			int user_score = Integer.parseInt(params.get("user-"+ i +"[score]"));

			if (params.get("user-"+ i +"[id]").equals("new")){
				User user = User.find("name = ?", params.get("user-"+ i +"[name]")).first();
				UserProject.createUserProject(user.getId(), project_id, user_score);
			} else {
				long user_id = Long.parseLong(params.get("user-"+ i +"[id]"));
				UserProject user_project = UserProject.find("project_id=? AND user_id=?", project_id, user_id).first();
				user_project.score = user_score;
				user_project.hasScore = true;
				user_project.save();
				News news = new News(new Date(), user_id, project_id, 6);
				news.save();
			}
		}
*/
		mypage();
    }

    // 登録を保存する
	//This creates new wishes.
    //required HTML form params : projectID, wishLimit, wish-[rank]
    public static void saveRegistration(long projectID, int wishLimit){
    	validation.required(projectID);
    	validation.required(wishLimit);
    	validation.equals(wishLimit, Project.getWishLimit(projectID));
    	
        if(validation.hasErrors()) {
            for(Error error : validation.errors()) {
                System.out.println(error.message());
            }
            mypage();
        }
    	
    	long userID = User.getIDByName(session.get(SESSION_KEY_USER));

	    UserProject.register(userID, projectID);

	    Wish.resetWishByUserID(userID, projectID);
    	
        for(int wishRank=1; wishRank<=wishLimit; wishRank++){
        	long groupID = Long.valueOf(params.get("wish-"+ wishRank));
            System.out.println("group ID of wish rank" + wishRank + " is "  + groupID);
            
            Wish.createWish(userID, groupID, wishRank);
        }
        	
    	mypage();
    }

    // ユーザ名が存在するかを返す
    public static void isExistsUser(String name){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("isExists", User.isExists(name));
        if(User.isExists(name)){
            result.put("id", User.getIDByName(name));
        }
        renderJSON(result);
    }

    public static void getProjectById(Long id){
        Map<String, Object> result = new HashMap<String, Object>();

        User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
        Project p = Project.find("ID = ?", id).first();

        List<Group>         groups          = Group.getGroupListByProjectID(p.id);
        List<UserProject>   user_projects   = UserProject.find("project_id = ?", p.id).fetch();
        ArrayList<User>     users           = new ArrayList<User>();
        HashMap<Long, Integer> user_score   = new HashMap<Long, Integer>();
        for(UserProject usr : user_projects){
            User user = User.find("id = ?", usr.user_id).first();
            users.add(user);
            user_score.put(usr.user_id, usr.score);
        }

        // ユーザ情報がすべてクライアントに送られるため、修正すべき
        result.put("project", p);
        result.put("users", users);
        result.put("groups", groups);
        result.put("user_score", user_score);
        renderJSON(result);
    }

		// 招待コードが有効かを返し、有効ならばUserProjectを作成する
		public static void isValidInvitationCode(String invitation_code){
        Map<String, Object> result = new HashMap<String, Object>();
				User u = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
        result.put("isValid", Project.isValidInvitationCode(invitation_code, u.getId()));
        renderJSON(result);
		}

		// 招待コードに対応するProjectの情報を返す
		public static void informationProject(String invitation_code){
				Project p = Project.find("invitation_code = ?", invitation_code).first();
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("project_id", p.getId());
				result.put("project_name", p.name);
				result.put("project_deadline", p.getDeadlineTime());
				renderJSON(result);
		}

    public static void news(){
		User user = User.find("name = ?", session.get(SESSION_KEY_USER)).first();
		List<News> news = News.getAllNews(user.getId());
    	render(news);
    }

    public static void getInvitationCode(Long project_id, Boolean is_valid){
        Map<String, Object> result = new HashMap<String, Object>();
        Project p = Project.find("id=?", project_id).first();
        String code = null;
        if(p.setValidInvitation(is_valid) && is_valid){
            code = p.invitation_code;
        }
        result.put("code", code);
        renderJSON(result);
    }

		// Delete group
		public static void deleteGroup(Long group_id){
			Group group = Group.findById(group_id);
			group.deleteWithWishes();
		}

		// Update or Create group
		public static void updateOrCreateGroup(String name, String detail, int capacity, String group_id_str){
			Long project_id = Long.parseLong(session.get(SESSION_PROJECT_ID));
			Group group;
			if (group_id_str.equals("new")){
				group = new Group(name, detail, capacity, project_id);
			} else {
				Long group_id = Long.parseLong(group_id_str);
				group = Group.findById(group_id);
				group.setAttributes(name, detail, capacity);
			}
			group.save();
		}

		// Delete user_project
		public static void deleteUserProject(Long user_id){
			Long project_id = Long.parseLong(session.get(SESSION_PROJECT_ID));
			UserProject user_project = UserProject.find("project_id=? AND user_id=?", project_id, user_id).first();
			user_project.deleteWithWishes();
			News news = new News(new Date(), user_id, project_id, 5);
			news.save();
		}

		// Update ot Create user_project
		public static void updateOrCreateUserProject(String user_name, int user_score){
			Long project_id = Long.parseLong(session.get(SESSION_PROJECT_ID));
			User user = User.find("name = ?", user_name).first();
			if(UserProject.count("user_id=? AND project_id=?", user.getId(), project_id) < 1){
				UserProject.createUserProject(user.getId(), project_id, user_score);
			} else {
				UserProject user_project = UserProject.find("project_id=? AND user_id=?", project_id, user.getId()).first();
				user_project.score = user_score;
				user_project.hasScore = true;
				user_project.save();
				News news = new News(new Date(), user.getId(), project_id, 6);
				news.save();
			}
		}
}
