package controllers;

import models.Files;
import models.Profile;
import models.User;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by Humin on 3/31/14.
 */
@CRUD.For(Files.class)
public class QA extends CRUD {

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
            Profile profile = Profile.find("user.id=?", user.id).first();
            String ids = "";
            if(profile.files.size()>0) {
                for (Files f : profile.files) {
                    if (!"".equals(ids))
                        ids += ",";
                    ids += f.id;
                }
                where += "id in (" + ids + ")";
            }else{
                where += "id<0";
            }
            request.args.put("where",where);
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
        Model object = type.findById(id);
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        String  username = session.get("username");
        if(username!=null){
            User user = User.getByUserName(username);
            Profile profile = Profile.find("user.id=?", user.id).first();
            profile.is_audit=0;
            profile.files.add((Files)object);
            profile.save();
        }
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
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        String  username = session.get("username");
        if(username!=null){
            User user = User.getByUserName(username);
            Profile profile = Profile.find("user.id=?", user.id).first();
            profile.is_audit=0;
            profile.files.add((Files)object);
            profile.save();
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

    public static void upload(){
        String[] files = params.getAll("files");
        String  username = session.get("username");
        Files file = null;
        if(username!=null){
            User user = User.getByUserName(username);
            Profile profile = Profile.find("user.id=?", user.id).first();
            profile.is_audit=0;
            for (String f: files){
                file = Files.findById(Long.valueOf(f));
                profile.files.add(file);
            }
            profile.save();
        }

        redirect("/admin/qa");
    }

}
