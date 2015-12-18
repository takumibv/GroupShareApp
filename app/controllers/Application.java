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
    	mypage();
    }

    // 登録を保存する
    public static void saveRegistration(){
    	mypage();
    }
}
