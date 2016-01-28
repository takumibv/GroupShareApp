$(document).ready(function(){
	// 日付処理
	var today = new Date();
	$(".input-group .form-date").attr({"min" : today.getFullYear() +"-"+ ('0'+(today.getMonth()+1)).slice( -2 ) +"-"+ ('0'+today.getDate()).slice( -2 )});
/****
 * サインアップページ
 ****/
 	// ユーザー名の有効、無効の切り替え
	$("#signup-form input[name=name]").keyup(function(){
		var name 		= $("#signup-form input[name=name]").val();
		if(name=="" || isValidUser(name)){
			$("#signup-btn").prop("disabled", true);
		}else{
			$("#signup-btn").prop("disabled", false);
		}
		// ユーザが有効かどうかのチェック
		if(isValidUser(name)){
			$("#signup-form .alert-user").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>このアカウント名は既に存在しています。");
		}else if(name==""){
			$("#signup-form .alert-user").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>アカウント名を入力してください。");
		}else{
			$("#signup-form .alert-user").removeClass("ng").addClass("ok").html("<i class='fa fa-check-circle'></i>このアカウント名は有効です。");
		}
	});

/****
 * マイページ
 ****/
 	// テーブルの表示・非表示の切り替え
 	$(".view-toggle").on("click", function(){
 		var table = $(this).parent().find(".table");
 		if(table.css("display")=="none"){
			table.css({"display":"table"});
			$(this).html("<i class='fa fa-caret-up'></i>");
		}else if(table.size() > 0){
			table.css({"display":"none"});
			$(this).html("<i class='fa fa-caret-down'></i>");
		}
 	});

	$("#input-invitation-code").on("click",function(){
		return false;
	});

	// モーダル　招待コード入力
	$("#input-invitation-code-modal .form").keyup(function(){
		var invitation_code = $("#input-invitation-code-modal input[name=invitation_code]").val();
		// 入力がない場合ボタンを無効にする
		if(invitation_code==""){
			$("#input-invitation-code-btn").prop("disabled", true);
		}else{
			$("#input-invitation-code-btn").prop("disabled", false);
		}
	});

	// 招待コード送信ボタン
	$("#input-invitation-code-btn").on("click", function(){
		var invitation_code = $("#input-invitation-code-modal input[name=invitation_code]").val();
		if(isValidInvitationCode(invitation_code)){
			var project = informationProject(invitation_code);
			if($("#nothing-project").length){
				$("#nothing-project").after(
					"<table class='table table-bordered project-table'>"
					+ "<tbody><tr id='invitated-project-header'>"
					+ "<th>プロジェクト</th><th>締め切り</th><th>登録</th></tr>"
					+ "<tr><td class='column-1'><a href='project?id=" + project[0] + "'>" + project[1] + "</a></td>"
					+ "<td class='column-2'>" + project[2] + "</td>"
				 	+ "<td class='column-3'><a class='register-btn btn' href='register?id=" + project[0] + "'>登録する</a></td></tr>"
					+ "</tbody></table>");
				$("#nothing-project").remove();
			}else{
				$("#invitated-project-header").after(
					"<tr><td class='column-1'><a href='project?id=" + project[0] + "'>" + project[1] + "</a></td>"
					+ "<td class='column-2'>" + project[2] + "</td>"
				 	+ "<td class='column-3'><a class='register-btn btn' href='register?id=" + project[0] + "'>登録する</a></td></tr>");
			}
			alert("プロジェクト「" + project[1] + "」に参加しました。");
		}else{
			alert("無効な招待コードです.");
		}

		resetModal();
	});

	// 招待コードの有効・無効の切り替え	
	$(".select-valid-invitation").on("change", function(){
		var is_valid;
		if($(this).val()=="1"){
			is_valid = true;
		}else{
			is_valid = false;
		}
		var project_id = $(this).attr("id").substring(20);
		var code = getInvitationCode(project_id, is_valid);

		is_valid = (code != null);
		if(is_valid){
			$("#invitation-code-modal-"+project_id+" p.invitation-code").html(code);
			$("#is-valid-invitation-"+project_id).val("1");
			$("a[data-target=#invitation-code-modal-"+project_id+"]").addClass("share--active");
		}else{
			$("#invitation-code-modal-"+project_id+" p.invitation-code").html("招待コードを有効にしてください");
			$("#is-valid-invitation-"+project_id).val("2");
			$("a[data-target=#invitation-code-modal-"+project_id+"]").removeClass("share--active");
		}
	});

/*
/****
 * プロジェクト詳細ページ
 ****/
 	$("#project .table .group").on("click", function(){
 		var target = $(this).attr("target");
 		if($(target).css("display")=="none"){
			$(target).slideDown();
		}else{
			$(target).slideUp();
		}
 		
 	});

/****
 * プロジェクト作成ページ
 ****/
	$("#add-group").on("click",function(){
		return false;
	});

	// モーダル　グループ追加ボタンを有効、無効の切り替え
	$("#add-group-modal .form").keyup(function(){
		var name 		= escapeText($("#add-group-modal input[name=name]").val());
		var capacity 	= $("#add-group-modal input[name=capacity]").val();
		var detail 		= escapeText($("#add-group-modal textarea[name=detail]").val());
		if(name=="" || capacity=="" || detail=="" || !isValidGroup(name) || !isValidNumber(capacity)){
			$("#add-group-btn").prop("disabled", true);
		}else{
			$("#add-group-btn").prop("disabled", false);
		}
		// グループが有効かどうかのチェック
		if(!isValidGroup(name)){
			$("#validation-group-name").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>このグループ名は無効です。");
		}else{
			$("#validation-group-name").removeClass("ng").addClass("ok").html("<i class='fa fa-check-circle'></i>このグループ名は有効です。");
		}
		// 定員項目が数字かどうかのチェック
		if(!isValidNumber(capacity)){
			$("#validation-group-capacity").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>無効な入力です。");
		}else{
			$("#validation-group-capacity").removeClass("ng").addClass("ok").html("");
		}
	});

	// グループ追加ボタン
	$("#add-group-btn").on("click", function(){
		var name 		= escapeText($("#add-group-modal input[name=name]").val());
		var capacity 	= parseInt($("#add-group-modal input[name=capacity]").val());
		var detail 		= escapeText($("#add-group-modal textarea[name=detail]").val());
		$("#groups-field").append(
			  "<tr id='group-new' class='group'>"
			+ "<td><span class='name'>" + name + "</span></td>"
			+ "<td><span class='capacity'>" + capacity + "</span></td>"
			+ "<td><span class='detail'>" + detail + "</span></td>"
			+ "<td><a class='delete'>削除</a></td>"
			+ "</tr>");

		resetModal();
		checkTable();
	});

	// モーダル　参加ユーザ追加ボタンを有効、無効の切り替え
	$("#add-user-modal .form").keyup(function(){
		var name 		= escapeText($("#add-user-modal input[name=name]").val());
		var score 		= $("#add-user-modal input[name=score]").val();
		if(name=="" || score=="" || !isValidUser(name) || !isValidNumber(score)){
			$("#add-user-btn").prop("disabled", true);
		}else{
			$("#add-user-btn").prop("disabled", false);
		}
		// ユーザが有効かどうかのチェック
		if(!isValidUser(name)){
			$("#validation-user-name").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>このアカウント名は存在しないか、既に追加しています。");
			$("#add-user-modal input[name=id]").val("");
		}else{
			$("#validation-user-name").removeClass("ng").addClass("ok").html("<i class='fa fa-check-circle'></i>このアカウント名は有効です。");
			$("#add-user-modal input[name=id]").val(isValidUser(name)); // 有効であれば、idをフォームに追加
		}
		// 得点が数字かどうかのチェック
		if(!isValidNumber(score)){
			$("#validation-user-score").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>無効な入力です。");
		}else{
			$("#validation-user-score").removeClass("ng").addClass("ok").html("");
		}
	});

	// 参加ユーザ追加ボタン
	$("#add-user-btn").on("click", function(){
		var id 			= $("#add-user-modal input[name=id]").val();
		var name 		= escapeText($("#add-user-modal input[name=name]").val());
		var score 		= parseInt($("#add-user-modal input[name=score]").val());
		if(isValidUser(name)){
			$("#users-field").append(
				  "<tr id='user-new' class='user'>"
				+ "<td><span class='name'>" + name + "</span></td>"
				+ "<td class='td-score'><span class='score'>" + score + "</span></td>"
				+ "<td><a class='delete'>削除</a></td>"
				+ "</tr>");

			resetModal();
			checkTable();
			checkAssignSystem();
		}else{
			return false;
		}
	});

	// 表の削除ボタン
	$(document).on("click", ".table .delete", function(){
		if(confirm("削除しますか？")){
			$(this).parent().parent().fadeOut().queue(function() {
				this.remove();
				checkTable();
			});
		}
	});

	// 表の削除ボタン
	$(document).on("click", ".table .delete-existing", function(){
		if(confirm("グループ登録されていた情報が失われてしまいます。削除しますか？")){
			var del_table = $(this).parent().parent(); 
			$(this).parent().parent().fadeOut().queue(function() {
				this.remove();
				checkTable();
			});
			if(del_table.attr("class") == "user"){
				$("#deleted-users-field").append(
					"<tr id='" + del_table.attr("id") + "' class='user'>"
					+ del_table.html()
					+ "</tr>");
			}
			if(del_table.attr("class") == "group"){
				$("#deleted-groups-field").append(
					"<tr id='" + del_table.attr("id") + "' class='group'>"
					+ del_table.html()
					+ "</tr>");
			}
		}
	});

	// 表の編集
	$(document).on("click", ".group .name, .group .capacity, .group .detail, .user .score", function(){
		var parent = $(this).parent();
		if($(this).attr("class")=="name" || $(this).attr("class")=="capacity" || $(this).attr("class")=="score"){
			$(this).replaceWith("<input type='text' class='replace-input' name='"+ $(this).attr("class") +"' value='"+ $(this).html() +"'>");
			parent.find("input").select();
		}else{
			$(this).replaceWith("<textarea class='replace-input' name='"+ $(this).attr("class") +"'>"+ $(this).html() +"</textarea>");
			parent.find("textarea").select();
		}
	});
	// フォームの送信を阻止
	$(document).on("submit", ".replace-input", function(){
		return false;
	});
	$(document).on("keydown", ".group .replace-input, .user .replace-input", function(e){
		if(!event.shiftKey){
			if ( e.which == 13 ) {
				if($(this).parent().parent().attr("class")=="group" && $(this).attr("name")=="name"){
					if(isValidGroup($(this).val())){
						$(this).replaceWith("<span class='"+ escapeText($(this).attr("name")) +"'>"+ escapeText($(this).val()) +"</span>");
					}else{
						alert("無効なグループ名です。");
					}
				}else if($(this).attr("name")=="score" || $(this).attr("name")=="capacity"){
					if($(this).val()!="" && isValidNumber($(this).val())){
						$(this).replaceWith("<span class='"+ escapeText($(this).attr("name")) +"'>"+ parseInt($(this).val()) +"</span>");
					}else{
						alert("無効な数値です。");
					}
				}else{
					$(this).replaceWith("<span class='"+ $(this).attr("name") +"'>"+ escapeText($(this).val()) +"</span>");
				}
			}
		}
	});
	// 表の編集ここまで

	$("select[name=assign_system]").on("change", function(){
		checkAssignSystem();
	});


	// プロジェクトを保存ボタン
	$("#make-project").on("submit", function(){
		$(window).off('beforeunload');

		var group_num = 0;
		$("#input-groups-field").html("");
		$('#groups-field .group').each(function(){
			$("#input-groups-field").append(
				"<input type='text' name='group-"+group_num+"[name]' value='"+ $(this).find(".name").html() +"'>"
				+"<input type='text' name='group-"+group_num+"[capacity]' value='"+ $(this).find(".capacity").html() +"'>"
				+"<input type='text' name='group-"+group_num+"[detail]' value='"+ $(this).find(".detail").html() +"'>");
			group_num++;
		});

		var user_num = 0;
		$("#input-users-field").html("");
		$('#users-field .user').each(function(){
			$("#input-users-field").append(
				  "<input type='text' name='user-"+user_num+"[id]' value='"+ $(this).attr("id").substring(5) +"'>"
				+ "<input type='text' name='user-"+user_num+"[name]' value='"+ $(this).find(".name").html() +"'>"
				+ "<input type='text' name='user-"+user_num+"[score]' value='"+ $(this).find(".score").html() +"'>");
			user_num++;
		});

		$("input[name=group-num]").val(group_num);
		$("input[name=user-num]").val(user_num);

		return checkBeforeSaveProject();
	});

	$("#use-past-project").on("change", function(){
		var project_id = $(this).val();
		if(project_id=="0"){
		}else{
			$("#users-field .user").remove();
			$("#groups-field .group").remove();

			var data 		= getProjectById(project_id);
			var project 	= data.project;
			var users 		= data.users;
			var groups  	= data.groups;
			var user_score	= data.user_score;

			$("#make-project input[name=name]").val(project.name);
			$("#make-project textarea[name=detail]").html(project.detail);
			// $("#make-project input[name=deadline_ymd]").val(project.deadline_ymd);
			// $("#make-project input[name=deadline_hm]").val(project.deadline_hm);
			$("#make-project select[name=assign_system]").val(project.assign_system);
			$("#make-project select[name=wish_limit]").val(project.wish_limit);
			$("#make-project select[name=public_register_user]").val(project.public_register_user);
			$("#make-project select[name=public_register_number]").val(project.public_register_number);
			$("#make-project select[name=public_user]").val(project.public_user);
			$("#make-project select[name=allocation_method]").val(project.allocation_method);
			$("#make-project select[name=trash]").val(project.trash);

			for (var i = users.length - 1; i >= 0; i--) {
				var user = users[i];
				$("#users-field").append(
					  "<tr id='user-" + user.id + "' class='user'>"
					+ "<td><span class='name'>" + user.name + "</span></td>"
					+ "<td class='td-score'><span class='score'>" + user_score[user.id] + "</span></td>"
					+ "<td><a class='delete'>削除</a></td>"
					+ "</tr>");
			}

			for (var i = groups.length - 1; i >= 0; i--) {
				var group = groups[i];
				$("#groups-field").append(
					  "<tr class='group'>"
					+ "<td><span class='name'>" + group.name + "</span></td>"
					+ "<td><span class='capacity'>" + group.capacity + "</span></td>"
					+ "<td><span class='detail'>" + group.detail + "</span></td>"
					+ "<td><a class='delete'>削除</a></td>"
					+ "</tr>");
			}

			checkTable();
			checkAssignSystem();
		}
	});
/****
 * プロジェクト編集ページ
 ****/
 	// URL指定してないため、すべてのページで呼ばれるが、編集ページでのみ使用
 	checkAssignSystem();

	// (編集ページ)プロジェクトの変更を保存ボタン
	$("#update-project").on("submit", function(){
		$(window).off('beforeunload');

		var group_num = 0;
		$("#input-groups-field").html("");
		$('#groups-field .group').each(function(){
/*
			$("#input-groups-field").append(
				 "<input type='text' name='group-"+group_num+"[id]' value='"+ $(this).attr("id").substring(6) +"'>"
				+"<input type='text' name='group-"+group_num+"[name]' value='"+ $(this).find(".name").html() +"'>"
				+"<input type='text' name='group-"+group_num+"[capacity]' value='"+ $(this).find(".capacity").html() +"'>"
				+"<input type='text' name='group-"+group_num+"[detail]' value='"+ $(this).find(".detail").html() +"'>");
*/
			if($(this).attr("id").substring(6) == "new")var id = -1;
			else var id = $(this).attr("id").substring(6);
			var name = $(this).find(".name").html();
			var detail = $(this).find(".detail").html();
			var capacity = $(this).find(".capacity").html();

			$.ajax({
        type: 'GET',
        url: '/updateOrCreateGroup',
        data: {name:name,detail:detail,capacity:capacity,group_id:id},
        dataType: "json",
        async: false // 同期的
			});
			group_num++;
		});

		var deleted_group_num = 0;
		$("#input-deleted-groups-field").html("");
		$('#deleted-groups-field .group').each(function(){
/*
			$("#input-deleted-groups-field").append(
				 "<input type='text' name='d-group-"+deleted_group_num+"[id]' value='"+ $(this).attr("id").substring(6) +"'>"
				);
*/
			$.ajax({
        type: 'GET',
        url: '/deleteGroup',
        data: "group_id="+$(this).attr("id").substring(6),
        dataType: "json",
        async: false // 同期的
    	});
			deleted_group_num++;
		});

		var user_num = 0;
		$("#input-users-field").html("");
		$('#users-field .user').each(function(){
/*
			$("#input-users-field").append(
				  "<input type='text' name='user-"+user_num+"[id]' value='"+ $(this).attr("id").substring(5) +"'>"
				+ "<input type='text' name='user-"+user_num+"[name]' value='"+ $(this).find(".name").html() +"'>"
				+ "<input type='text' name='user-"+user_num+"[score]' value='"+ $(this).find(".score").html() +"'>");
*/
			var user_name = $(this).find(".name").html();
			var user_score = $(this).find(".score").html();
console.log(user_name);
console.log(user_score);
			$.ajax({
        type: 'GET',
        url: '/updateOrCreateUserProject',
        data: {user_name:user_name,user_score:user_score},
        dataType: "json",
        async: false // 同期的
			});
			user_num++;
		});

		var deleted_user_num = 0;
		$("#input-deleted-users-field").html("");
		$('#deleted-users-field .user').each(function(){
/*
			$("#input-deleted-users-field").append(
				  "<input type='text' name='d-user-"+deleted_user_num+"[id]' value='"+ $(this).attr("id").substring(5) +"'>"
				);
*/
			var id = $(this).attr("id").substring(5);
			$.ajax({
        type: 'GET',
        url: '/deleteUserProject',
        data: {user_id: $(this).attr("id").substring(5)},
        dataType: "json",
        async: false // 同期的
			});
			deleted_user_num++;
		});

		$("input[name=group-num]").val(group_num);
		$("input[name=d-group-num]").val(deleted_group_num);
		$("input[name=user-num]").val(user_num);
		$("input[name=d-user-num]").val(deleted_user_num);
		return checkBeforeSaveProject();
	});

/****
 * 登録ページ
 ****/
	$("#registration-form").on("submit", function(){
		if(!confirm('この内容で登録しますか？')){
			return false;
		}
		var num = $("#register select").size();
		var value = "";
		var response = true;
		$("#register select").each(function(){
			value = $(this).val();
			if($("#register option[value="+value+"]:selected").size() > 1){
				response = false;
				$(".validation-register").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>同じものを志望することはできません。");
				return false;
			}
		});
		return response;
	});

});
/****
 * 関数
 ****/
// 引数のユーザ名が有効であればidを返す ajax
function isValidUser(name){
	var valid_flg = true;

	var result = $.ajax({
        type: 'GET',
        url: '/isExistsUser',
        data: "name="+name,
        dataType: "json",
        async: false // 同期的
    }).responseJSON;

	var isExists = result.isExists;
	if(isExists){
		valid_flg = result.id; // 存在するユーザ名ならば、有効
	}else{
		valid_flg = false;
	}

	// リストに既に存在する名前であるかどうか
	$('#users-field .user .name').each(function(){
        if(name == $(this).html()){
        	valid_flg = false;
        }
    });

	return valid_flg; 
}

// 引数のidのプロジェクト情報を返す
function getProjectById(id){
	var result = $.ajax({
        type: 'GET',
        url: '/getProjectById',
        data: "id="+id,
        dataType: "json",
        async: false // 同期的
    }).responseJSON;

	return result; 
}

// 引数のグループ名が有効かどうかを返す
function isValidGroup(name){
	var valid_flg = true;
	if(name==""){ valid_flg = false; }
	$('#groups-field .group .name').each(function(){
        if(name == $(this).html()){
        	valid_flg = false;
        }
    });
    return valid_flg;
}

function isValidNumber(num){
	if(isFinite(num)){
		return (parseInt(num) >= 0 && parseInt(num) <= 2147483647);
	}else{
		return false;
	}
}

// モーダルを閉じる時にリセットする
function resetModal(){
	//グループ
	$("#add-group-modal input[name=name]").val("");
	$("#add-group-modal input[name=capacity]").val("");
	$("#add-group-modal textarea[name=detail]").val("");
	$("#validation-group-name").html("");
	$("#validation-group-capacity").html("");
	$("#add-group-btn").prop("disabled", true);
	$("#add-group-modal .close").click();

	//ユーザ
	$("#add-user-modal input[name=id]").val("");
	$("#add-user-modal input[name=name]").val("");
	$("#add-user-modal input[name=score]").val("");
	$("#validation-user-name").html("");
	$("#validation-user-score").html("");
	$("#add-user-btn").prop("disabled", true);
	$("#add-user-modal .close").click();

	//招待コード
  $("#input-invitation-code-modal input[name=invitation_code]").val("");
	$("#input-invitation-code-btn").prop("disabled", true);
	$("#input-invitation-code-modal .close").click();
}

// 表に参加ユーザとグループがあるかどうかをチェックする
function checkTable(){
	var users = $(document).find(".user");
	var groups = $(document).find(".group");

	if(users.size() == 0){
		$("#users-field").addClass("no-element");
		$(".user-table-note").addClass("no-element");
	}else{
		$("#users-field").removeClass("no-element");
		$(".user-table-note").removeClass("no-element");
	}

	if(groups.size() == 0){
		$("#groups-field").addClass("no-element");
		$(".group-table-note").addClass("no-element");
	}else{
		$("#groups-field").removeClass("no-element");
		$(".group-table-note").removeClass("no-element");
	}
}

// 点数優先かじゃんけんかで、ユーザのスコアの表示・非表示を切り替える
function checkAssignSystem(){

	var select = $("select[name=assign_system]").val();
	var score_input = $("#add-user-modal input[name=score]");
	if(select=="1"){
		score_input.parent().removeClass("display-none");
		score_input.val("");
		score_input.prop("disabled", false);

		$("#users-field .th-score").removeClass("display-none");
		$("#users-field .td-score").removeClass("display-none");

		$("#allocation-method-field").removeClass("display-none");
	}else if(select=="2"){
		score_input.parent().addClass("display-none");
		score_input.val("0");
		score_input.prop("disabled", true);

		$("#users-field .th-score").addClass("display-none");
		$("#users-field .td-score").addClass("display-none");

		$("#allocation-method-field").addClass("display-none");
	}
}

// XXS対策
function escapeText(text){
	var val = "";
	for(var i=0; i<text.length; i++){
		var word = text.substring(i, i+1);
		if(word == "<") { val += "&lt"; }
		else if(word == ">") { val += "&gt"; }
		else if(word == "&") { val += "&amp"; }
		else if(word == '"') { val += "&quot"; }
		else if(word == '\n') { val += "<br>"; }
		else { val += word; }
	}
	return val;
}

// 引数セレクタの場所まで移動
function move(selecta){
	var p = selecta.eq(0).offset().top;
	$('html,body').animate({ scrollTop: p }, 'fast');
}

// プロジェクト保存ボタンを押したとき、適切な入力かを調べる
function checkBeforeSaveProject(){
	$(".validation-input").html("");
	$(".validation-group").html("");
	$(".validation-user").html("");

	var edit_form 	= $(document).find(".replace-input").size();
	var group_num 	= $('#groups-field .group').size();
	var user_num 	= $('#users-field .user').size();

	if(edit_form > 0){
		$(".replace-input").addClass("error-form");
		$(".validation-input").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>必要項目を入力してください。");
    	move($(".error-form"));
		return false;
	}

	var wish_limit = parseInt($("select[name=wish_limit]").val());
	if(wish_limit > group_num){
		$(".validation-wish").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>志望数はグループの数より小さくしてください。");
		move($(".validation-wish"));
		return false;
	}
	if(group_num < 2){
		$(".validation-group").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>グループは2つ以上追加してください。");
		move($(".validation-group"));
		return false;
	}else{
		$(".validation-group").html("");
	}
	// if(user_num < 2){
	// 	$("#validation-user").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>ユーザは2人以上追加してください。");
	// 	return false;
	// }else{
		$(".validation-user").html("");
	// }

	return true;
}

// 引数の招待コードが有効かどうかを返す ajax
function isValidInvitationCode(invitation_code){
	var valid_flg = true;

	var result = $.ajax({
        type: 'GET',
        url: '/isValidInvitationCode',
        data: "invitation_code="+invitation_code,
        dataType: "json",
        async: false // 同期的
    }).responseJSON;

	var isValid = result.isValid;
	valid_flg = isValid;

	return valid_flg; 
}

// 引数の招待コードが指すProjectの情報を返す ajax
function informationProject(invitation_code){
	var result = $.ajax({
        type: 'GET',
        url: '/informationProject',
        data: "invitation_code="+invitation_code,
        dataType: "json",
        async: false // 同期的
    }).responseJSON;

	return new Array(result.project_id, result.project_name, result.project_deadline);
}

function getInvitationCode(project_id, is_valid) {
	var result = $.ajax({
        type: 'GET',
        url: '/getInvitationCode',
        data: { project_id: project_id , is_valid: is_valid },
        dataType: "json",
        async: false // 同期的
    }).responseJSON;
    if(result.code != null){
    	return result.code;
    }
	return null;
}
