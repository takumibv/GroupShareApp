package controllers;

import play.*;
import play.mvc.*;

import models.*;

import javax.persistence.*;
import java.util.*;


public class Application extends Controller {

    public static void index() {
        render();
    }
}
