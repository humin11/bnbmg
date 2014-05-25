package jobs;

import models.*;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Every;
import play.jobs.Job;
import utils.SendMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Humin on 4/17/14.
 */
@Every("1mn")
public class Bootstrap extends Job {

    public void doJob() {

        Logger.info("Start execute the bootstrap job...");

        List<Request> requests = Request.find("status!=?",1).fetch();
        List<Profile> profileList = new ArrayList<Profile>();
        List<Profile> tempProfiles = new ArrayList<Profile>();
        Config config = Config.find("1=1").first();
        String message="";
        for(Request req: requests){
            if(req.stoptime.getTime()-new Date().getTime()<0){
                req.status = 1;
                req.is_finished=true;
                req.save();

                if(req.profiles==null || req.profiles.size()==0){
                    for(Specification spec: req.specifications){
                        tempProfiles = Profile.em().createQuery("select p from Profile p join fetch p.materials m where m.id = :ids").setParameter("ids", spec.material.id).getResultList();;
                        for(Profile p : tempProfiles){
                            profileList.add(p);
                        }
                    }
                }else{
                    profileList = req.profiles;
                }
                SendMessage m = new SendMessage();
                message=req.name+"正在招标，欢迎参与";
                if(config.msg_request_invite!=null && !"".equals(config.msg_request_invite)){
                    message = config.msg_request_invite.replace("{request}", req.name);
                }
                for(Profile p: profileList){
                    if(SMSHistory.isSend(req.id, p.contact_phone, "0000003")) {
                        if (p.contact_phone != null && !"".equals(p.contact_phone)) {
                            m.sendSms(p.contact_phone, message, "0000003");
                            SMSHistory smsHistory = new SMSHistory();
                            smsHistory.req = req;
                            smsHistory.mobile=p.contact_phone;
                            smsHistory.type= "0000003";
                            smsHistory.save();
                        }
                        if (p.contact_email != null && !"".equals(p.contact_email))
                            m.sendMail(p.contact_email, "[" + Messages.get("application.name") + "]招标邀请", message);
                    }
                }
            }

            if(req.starttime.getTime()<=new Date().getTime() && req.stoptime.getTime()>=new Date().getTime()){
                req.status = 0;
                req.save();
            }
        }

    }

}
