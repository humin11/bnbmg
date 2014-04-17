package controllers;

import com.google.gson.Gson;
import models.Specification;
import play.data.binding.Binder;
import play.data.validation.*;
import play.data.validation.Error;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * Created by Humin on 4/7/14.
 */
@CRUD.For(Specification.class)
public class Specs extends CRUD {

    public static void create() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            Map<String, List<play.data.validation.Error>> errorMap = validation.errorsMap();
            for(List<Error> errorList: errorMap.values()){
                for(Error e: errorList){
                    System.out.println(e.getKey()+"-------"+e.message());
                }
            }

            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        Gson gson = new Gson();

        renderText(gson.toJson(object));
//        System.out.println(gson.toJson(object));
//        renderJSON(object);
    }

}
