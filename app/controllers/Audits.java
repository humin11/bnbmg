package controllers;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.Audit;
import models.Profile;
import models.User;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;

import java.util.List;

/**
 * Created by Humin on 3/25/14.
 */
@With(Deadbolt.class)
@CRUD.For(Audit.class)
public class Audits extends CRUD {

    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        String where = "";
        if(request.args.containsKey("where")){
            where = (String)request.args.get("where");
        }


        String  username = session.get("username");
        if(username!=null){
            User user = User.getByUserName(username);
            Profile profile = Profile.find("user.id=?",user.id).first();
            if(profile!=null){
                if(where!=null && !"".equalsIgnoreCase(where) && !"where".equalsIgnoreCase(where)){
                    where +=" and ";
                }
                where+= " profile.id="+profile.id+" ";
                request.args.put("where", where);
            }

        }

        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }

}
