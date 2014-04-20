package controllers;

import models.Config;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

/**
 * Created by Humin on 4/20/14.
 */
@CRUD.For(Config.class)
public class Configuration extends CRUD {

    public static void show(String id) throws Exception {
        if(id==null || "".equals(id)){
            Config c = Config.find("1=1").first();
            if(c==null) {
                c = new Config();
                c.save();
            }
            id = c.id.toString();
        }

        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);
        try {
            render(type, object);
        } catch (TemplateNotFoundException e) {
            render("CRUD/show.html", type, object);
        }
    }
}
