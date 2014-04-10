package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * Created by Humin on 3/25/14.
 */
@Entity
public class Audit extends Model {

    public String name;

    @ManyToOne
    public User user;

    @OneToOne
    public Profile profile;

    public int is_audit;

    public String message;

    public Date create_at= new Date();

}
