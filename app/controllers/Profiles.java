package controllers;

import models.Audit;
import models.Profile;
import models.User;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

import java.net.URLDecoder;
import java.util.List;

@CRUD.For(Profile.class)
public class Profiles extends CRUD {

    public static void view(Long id){
        Profile profile = Profile.findById(id);
        String  username = session.get("username");
        User user = null;
        if(username!=null){

            user = User.getByUserName(username);

        }
        render(profile,user);
    }

    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        String result = params.get("result");
        if(result!=null && !"".equals(result))
            flash.success(URLDecoder.decode(result));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
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
