package controllers;

import models.*;
import play.Logger;

public class Security extends Secure.Security {

    static boolean authentify(String username, String password) {
        boolean result = true;
        if(User.getByUserName(username)==null){
            flash.error("用户不存在");
            flash.put("username", username);
            result = false;
        } else if(User.connect(username, password) == null){
            flash.error("用户名和密码不匹配");
            flash.put("username", username);
            result = false;
        }
        return result;
    }
    
    
    static void onDisconnected() {
        Application.index();
    }
    
    static void onAuthenticated() {
        Application.index();
    }
    
}

