package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by Humin on 4/7/14.
 */
@Entity
public class Specification extends Model {

    public String name;

    @CRUD.Exclude
    @Required
    @ManyToOne
    public Material material;

    @Required
    public String specification;

    public double thickness;

    public double width;

    public double weight;

    public double others;

    @Required
    public double amount;

    @Required
    public String unit;

    @Required
    public Date arrival_time;

    @Lob
    @CRUD.Exclude
    @Required
    public String description;

    public Blob detail_spec;

}
