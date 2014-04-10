package jobs;

import models.ApplicationRole;
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class InitData extends Job
{
    @Override
    public void doJob() throws Exception
    {
        if (ApplicationRole.getByName("superadmin") == null)
        {
            new ApplicationRole("superadmin").save();
        }
        if (ApplicationRole.getByName("system") == null)
        {
            new ApplicationRole("system").save();
        }
        if (ApplicationRole.getByName("admin") == null)
        {
            new ApplicationRole("admin").save();
        }
        if (ApplicationRole.getByName("operator") == null)
        {
            new ApplicationRole("operator").save();
        }
        if (ApplicationRole.getByName("user") == null)
        {
            new ApplicationRole("user").save();
        }

        if (User.getByUserName("system") == null)
        {
            User user = new User("system",
                                 "Passw0rd",
                                 ApplicationRole.getByName("system"));
            user.save();
        }

    }
}
