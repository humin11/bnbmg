package controllers;

import models.User;
import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import play.mvc.Controller;
import play.mvc.With;

@With(Deadbolt.class)
@CRUD.For(User.class)
@Restrictions({@Restrict("superadmin"), @Restrict("admin")})
public class Users extends CRUD {
	
}