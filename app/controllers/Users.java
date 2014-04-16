package controllers;

import models.User;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import play.mvc.Controller;
import play.mvc.With;

@With(Deadbolt.class)
@CRUD.For(User.class)
public class Users extends CRUD {


    public static  void toPassword(){

        render("Users/password.html");
    }

    public static void changePassword(){
        String password = params.get("password");
        String username = session.get("username");
        if(username !=null) {
            User user = User.getByUserName(username);
            user.password = password;
            user.save();
        }

        redirect("/");
    }

}