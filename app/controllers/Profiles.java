package controllers;

import models.Audit;
import models.Profile;
import models.User;

@CRUD.For(Profile.class)
public class Profiles extends CRUD {

    public static void view(Long id){
        Profile profile = Profile.findById(id);
        render(profile);
    }

    public static void audit(Long id){
        Profile profile = Profile.findById(id);
        String username = session.get("username");
        String is_audit = request.params.get("is_audit");
        String message = request.params.get("message");
        Audit audit = null;
        if(username !=null){
            User user = User.getByUserName(username);
            audit = new Audit();
            audit.user=user;
            audit.profile = profile;
            if(is_audit!=null){
                audit.is_audit = Integer.valueOf(is_audit).intValue();
            }
            audit.message = message;
            audit.save();

            profile.is_audit=audit.is_audit;
            profile.save();
            renderText("success");
        }
    }

}
