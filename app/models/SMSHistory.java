package models;

import jobs.Bootstrap;
import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class SMSHistory extends Model {

	public Request req;

    public String mobile;

    public String type;


    public static Boolean isSend(Long id, String mobile, String type){
        Boolean result = false;

        if(SMSHistory.find("req.id=? and mobile=? and type=?", id, mobile, type).first()!=null){
            result = true;
        }

        return result;
    }

}
