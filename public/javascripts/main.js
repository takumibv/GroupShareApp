$(document).ready(function(){

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
		if(name=="" || capacity=="" || detail=="" || !isValidGroup(name)){
			$("#add-group-btn").prop("disabled", true);
		}else{
			$("#add-group-btn").prop("disabled", false);
		}
		// グループが有効かどうかのチェック
		if(!isValidGroup(name)){
			$("#validation-group-name").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>このアカウント名は無効です。");
		}else{
			$("#validation-group-name").removeClass("ng").addClass("ok").html("<i class='fa fa-check-circle'></i>このアカウント名は有効です。");
		}
	});

	// グループ追加ボタン
	$("#add-group-btn").on("click", function(){
		$("#groups-field").append(
			  "<tr class='group'>"
			+ "<td><span class='name'>" + $("#add-group-modal input[name=name]").val() + "</span></td>"
			+ "<td><span class='capacity'>" + $("#add-group-modal input[name=capacity]").val() + "</span></td>"
			+ "<td><span class='detail'>" + $("#add-group-modal textarea[name=detail]").val() + "</span></td>"
			+ "<td><a class='delete'>削除</a></td>"
			+ "</tr>");

		$("#add-group-modal input[name=name]").val("");
		$("#add-group-modal input[name=capacity]").val("");
		$("#add-group-modal textarea[name=detail]").val("");

		$("#add-group-btn").prop("disabled", true);
		$("#add-group-modal .close").click();
	});

	// モーダル　参加ユーザ追加ボタンを有効、無効の切り替え
	$("#add-user-modal .form").keyup(function(){
		var name 		= $("#add-user-modal input[name=name]").val();
		var score 		= $("#add-user-modal input[name=score]").val();
		if(name=="" || score=="" || !isValidUser(name)){
			$("#add-user-btn").prop("disabled", true);
		}else{
			$("#add-user-btn").prop("disabled", false);
		}
		// ユーザが有効かどうかのチェック
		if(!isValidUser(name)){
			$("#validation-user-name").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>このアカウント名は無効です。");
		}else{
			$("#validation-user-name").removeClass("ng").addClass("ok").html("<i class='fa fa-check-circle'></i>このアカウント名は有効です。");
		}
	});

	// 参加ユーザ追加ボタン
	$("#add-user-btn").on("click", function(){
		var name 		= $("#add-user-modal input[name=name]").val();
		var score 		= $("#add-user-modal input[name=score]").val();
		if(isValidUser(name)){
			$("#users-field").append(
				  "<tr class='user'>"
				+ "<td><span class='name'>" + $("#add-user-modal input[name=name]").val() + "</span></td>"
				+ "<td><span class='score'>" + $("#add-user-modal input[name=score]").val() + "</span></td>"
				+ "<td><a class='delete'>削除</a></td>"
				+ "</tr>");

			$("#add-user-modal input[name=name]").val("");
			$("#add-user-modal input[name=score]").val("");

			$("#add-user-btn").prop("disabled", true);
			$("#add-user-modal .close").click();
		}else{
			return false;
		}
	});

	// 表の削除ボタン
	$(document).on("click", ".table .delete", function(){
		if(confirm("削除しますか？")){
			$(this).parent().parent().fadeOut().queue(function() {
				this.remove();
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
	$(document).on("keydown", ".group .replace-input, .user .replace-input", function(e){
		if(!event.shiftKey){
			if ( e.which == 13 ) {
				if($(this).parent().parent().attr("class")=="group" && $(this).attr("name")=="name"){
					if(isValidGroup($(this).val())){
						alert("無効なグループ名です。");
						$(this).replaceWith("<span class='"+ $(this).attr("name") +"'>"+ $(this).val() +"</span>");
					}
				}else{
					$(this).replaceWith("<span class='"+ $(this).attr("name") +"'>"+ $(this).val() +"</span>");
				}
			}
		}
	});

	$("#make-project").on("submit", function(){
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
				+"<input type='text' name='user-"+user_num+"[score]' value='"+ $(this).find(".capacity").html() +"'>");
			user_num++;
		});

		if(group_num < 2){
			$("#validation-group").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>グループは最低2つ追加してください。");
			return false;
		}else{
			$("#validation-group").html("");
		}
		$("input[name=group-num]").val(group_num);
		if(user_num < 2){
			$("#validation-user").removeClass("ok").addClass("ng").html("<i class='fa fa-exclamation-triangle'></i>ユーザは最低2人追加してください。");
			return false;
		}else{
			$("#validation-group").html("");
		}
		$("input[name=user-num]").val(user_num);
	});

});

// 引数のユーザ名が有効かどうかを返す ajax
function isValidUser(name){
	var valid_flg = true;
	$('#users-field .user .name').each(function(){
        console.log( $(this).html() );
        if(name == $(this).html()){
        	valid_flg = false;
        }
    });

    return valid_flg;
}
// 引数のグループ名が有効かどうかを返す
function isValidGroup(name){
	var valid_flg = true;
	$('#groups-field .group .name').each(function(){
        console.log( $(this).html() );
        if(name == $(this).html()){
        	valid_flg = false;
        }
    });
    return valid_flg;
}
