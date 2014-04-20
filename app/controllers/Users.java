package controllers;

import models.User;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

@With(Deadbolt.class)
@CRUD.For(User.class)
public class Users extends CRUD {


    public static  void toPassword(){

        render("Users/password.html");
    }

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
        if(where!=null && !"".equalsIgnoreCase(where) && !"where".equalsIgnoreCase(where)){
            where +=" and ";

        }

        where += " (role.name!='superadmin' and role.name!='user') ";
        request.args.put("where", where);

        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
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