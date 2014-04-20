package models;

import controllers.CRUD;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * Created by Humin on 4/20/14.
 */
@Entity
public class Config extends Model {

    @CRUD.Exclude
    public String name="系统配置";

    @Lob
    public String msg_import;

    @Lob
    public String msg_request_invite;

    @Lob
    public String msg_request_notification;

}
