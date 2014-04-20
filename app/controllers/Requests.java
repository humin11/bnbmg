package controllers;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controllers.CRUD.ObjectType;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.Material;
import models.Profile;
import models.Request;
import models.User;
import play.data.binding.Binder;
import play.data.validation.*;
import play.data.validation.Error;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Controller;
import play.mvc.With;
import utils.SendMessage;

@CRUD.For(Request.class)
@With(Deadbolt.class)
public class Requests extends CRUD {

    public static void view(Long id){
        Request object = Request.findById(id);
        List<Profile> profiles = Profile.findAll();
        List<Material> materials = Material.findAll();
        String  username = session.get("username");
        User user = null;
        if(username!=null){

            user = User.getByUserName(username);

            if(user.role.name.equalsIgnoreCase("operator") ) {
                materials = user.materials;
            }
        }
        render(object,user, profiles, materials, user);
    }

    public static void show(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);
        List<Profile> profiles = Profile.findAll();
        List<Material> materials = Material.findAll();
        String  username = session.get("username");
        User user = null;
        if(username!=null){

            user = User.getByUserName(username);
            if(user.role.name.equalsIgnoreCase("operator") ) {
                materials = user.materials;
            }
        }
        try {
            render(type, object, profiles,materials,user);
        } catch (TemplateNotFoundException e) {
            render("CRUD/show.html", type, object, profiles,materials);
        }
    }

    public static void blank() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        List<Profile> profiles = Profile.findAll();
        List<Material> materials = Material.findAll();
        String  username = session.get("username");
        if(username!=null){

            User user = User.getByUserName(username);
            if(user.role.name.equalsIgnoreCase("operator") ) {
                materials = user.materials;
            }
        }
        try {
            render(type, object,profiles,materials);
        } catch (TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object,profiles,materials);
        }
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


        String  username = session.get("username");
        if(username!=null){
            if(where!=null && !"".equalsIgnoreCase(where) && !"where".equalsIgnoreCase(where)){
                where +=" and ";
            }
            User user = User.getByUserName(username);
            if(user.role.name.equalsIgnoreCase("operator") ) {
                where += " user.id=" + user.id + " ";
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
	
	
	public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Request object = (Request)type.findById(id);
        String username=session.get("username");
        User user = User.getByUserName(username);
        if(user!=null){
        	object.user = user;
        }
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        List<Profile> profiles = Profile.findAll();
        List<Material> materials = Material.findAll();
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));

            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object,profiles,materials);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object,profiles,materials);
            }
        }
        object._save();

        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }


    public static void create() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        List<Profile> profiles = Profile.findAll();
        List<Material> materials = Material.findAll();
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
//            Map<String, List<Error>> errorMap = validation.errorsMap();
//            for(List<Error> errorList: errorMap.values()){
//                for(Error error: errorList){
//                    System.out.println(error.getKey()+"---"+error.message());
//                }
//            }
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object,profiles,materials);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object,profiles,materials);
            }
        }
        object._save();
        Request req = (Request)object;
        List<Profile> profileList = null;
        if(req.profiles==null || req.profiles.size()==0){
            profileList = Profile.findAll();
        }else{
            profileList = req.profiles;
        }
        SendMessage m = new SendMessage();
        for(Profile p: profileList){
            m.sendSms(p.contact_phone,req.name+"正在招标，欢迎参与","0000003");
        }
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }
	
}
