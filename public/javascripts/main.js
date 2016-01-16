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
		console.log("name:"+(name=="")+" "+isValidUser(name));
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
			$("#invitated-project-header").after(
				"<tr><td class='column-1'><a href='project?id=" + project[0] + "'>" + project[1] + "</a></td>"
				+ "<td class='column-2'>" + project[2] + "</td>"
				 + "<td class='column-3'><a class='register-btn btn' href='register?id=" + project[0] + "'>登録する</a></td></tr>");
		}else{
			alert("無効な招待コードです.");
		}

		resetModal();
	});

/*
/****
 * プロジェクト作成ページ
 ****/
	$("#add-group").on("click",function(){
		return false;
	});

	// モーダル　グループ追加ボタンを有効、無効の切り替え
	$("#add-group-modal .form").keyup(function(){
		var name 		= $("#add-group-modal input[name=name]").val();
		var capacity 	= $("#add-group-modal input[name=capacity]").val();
		var detail 		= $("#add-group-modal textarea[name=detail]").val();
		if(name=="" || capacity=="" || detail=="" || !isValidGroup(name) || !isFinite(capacity)){
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
		if(!isFinite(capacity)){
			$("#validation-group-capacity").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>数値を入力してください。");
		}else{
			$("#validation-group-capacity").removeClass("ng").addClass("ok").html("");
		}
	});

	// グループ追加ボタン
	$("#add-group-btn").on("click", function(){
		var name 		= $("#add-group-modal input[name=name]").val();
		var capacity 	= parseInt($("#add-group-modal input[name=capacity]").val());
		var detail 		= $("#add-group-modal textarea[name=detail]").val();
		$("#groups-field").append(
			  "<tr class='group'>"
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
		var name 		= $("#add-user-modal input[name=name]").val();
		var score 		= $("#add-user-modal input[name=score]").val();
		if(name=="" || score=="" || !isValidUser(name) || !isFinite(score)){
			$("#add-user-btn").prop("disabled", true);
		}else{
			$("#add-user-btn").prop("disabled", false);
		}
		// ユーザが有効かどうかのチェック
		if(!isValidUser(name)){
			$("#validation-user-name").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>このアカウント名は存在しないか、既に追加しています。");
		}else{
			$("#validation-user-name").removeClass("ng").addClass("ok").html("<i class='fa fa-check-circle'></i>このアカウント名は有効です。");
		}
		// 得点が数字かどうかのチェック
		if(!isFinite(score)){
			$("#validation-user-score").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>数値を入力してください。");
		}else{
			$("#validation-user-score").removeClass("ng").addClass("ok").html("");
		}
	});

	// 参加ユーザ追加ボタン
	$("#add-user-btn").on("click", function(){
		var name 		= $("#add-user-modal input[name=name]").val();
		var score 		= parseInt($("#add-user-modal input[name=score]").val());
		if(isValidUser(name)){
			$("#users-field").append(
				  "<tr class='user'>"
				+ "<td><span class='name'>" + name + "</span></td>"
				+ "<td><span class='score'>" + score + "</span></td>"
				+ "<td><a class='delete'>削除</a></td>"
				+ "</tr>");

			resetModal();
			checkTable();
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
						$(this).replaceWith("<span class='"+ $(this).attr("name") +"'>"+ $(this).val() +"</span>");
					}else{
						alert("無効なグループ名です。");
					}
				}else if($(this).attr("name")=="score" || $(this).attr("name")=="capacity"){
					if($(this).val()!="" && isFinite($(this).val())){
						$(this).replaceWith("<span class='"+ $(this).attr("name") +"'>"+ parseInt($(this).val()) +"</span>");
					}else{
						alert("数値でありません。");
					}
				}else{
					$(this).replaceWith("<span class='"+ $(this).attr("name") +"'>"+ $(this).val() +"</span>");
				}
			}
		}
	});

	// プロジェクトを保存ボタン
	$("#make-project").on("submit", function(){
		$(window).off('beforeunload');
		var edit_form = $(document).find(".replace-input").size();
		if(edit_form > 0){
			$(".replace-input").addClass("error-form");
			return false;
		}

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
				  "<input type='text' name='user-"+user_num+"[name]' value='"+ $(this).find(".name").html() +"'>"
				+ "<input type='text' name='user-"+user_num+"[score]' value='"+ $(this).find(".score").html() +"'>");
			user_num++;
		});

		if(group_num < 2){
			$("#validation-group").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>グループは2つ以上追加してください。");
			return false;
		}else{
			$("#validation-group").html("");
		}
		$("input[name=group-num]").val(group_num);
		// if(user_num < 2){
		// 	$("#validation-user").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>ユーザは2人以上追加してください。");
		// 	return false;
		// }else{
			$("#validation-group").html("");
		// }
		$("input[name=user-num]").val(user_num);
	});

});

// 引数のユーザ名が有効かどうかを返す ajax
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
	valid_flg = isExists; // 存在するユーザ名ならば、有効

	// リストに既に存在する名前であるかどうか
	$('#users-field .user .name').each(function(){
        if(name == $(this).html()){
        	valid_flg = false;
        }
    });

	return valid_flg; 
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
	}else{
		$("#users-field").removeClass("no-element");
	}

	if(groups.size() == 0){
		$("#groups-field").addClass("no-element");
	}else{
		$("#groups-field").removeClass("no-element");
	}
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
