package jobs;

import models.Request;
import play.jobs.Every;
import play.jobs.Job;

import java.util.Date;
import java.util.List;

/**
 * Created by Humin on 4/17/14.
 */
@Every("1mn")
public class Bootstrap extends Job {

    public void doJob() {

        List<Request> requests = Request.find("status!=?",1).fetch();
        for(Request req: requests){
            if(req.stoptime.getTime()-new Date().getTime()<0){
                req.status = 1;
                req.save();
            }
        }

    }

}
