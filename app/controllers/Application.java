package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.*;
import utils.SendMessage;

@With(Deadbolt.class)
public class Application extends Controller {

	@Restrictions({@Restrict("superadmin"), @Restrict("admin"), @Restrict("operator"), @Restrict("system"), @Restrict("user")})
    public static void index() {
        List<Profile> profiles = Profile.find("is_audit=?",0).fetch();
        List<Request> requests = Request.find("status!=?",1).fetch();
        List<Request> allRequests = Request.findAll();
        String username = session.get("username");
        List<Toubiao> toubiaos = null;
        Profile profile = null;
        if(username !=null) {
            User user = User.getByUserName(username);
            profile = Profile.find("user.id=?", user.id).first();
            if(profile!=null)
                toubiaos = Toubiao.find("profile.id=?", profile.id).fetch();
        }
        render(profiles, profile, requests, allRequests, toubiaos);
    }


    public static void test(String id){
        SendMessage m = new SendMessage();
        m.sendSms("13401011713","This is a test message","123456");
        renderText("");
    }
}