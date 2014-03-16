package controllers;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.Request;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@CRUD.For(Request.class)
@With(Deadbolt.class)
@Restrictions({@Restrict("superadmin"), @Restrict("admin"), @Restrict("operator")})
public class Requests extends CRUD {

}
