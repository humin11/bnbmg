package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by Humin on 4/7/14.
 */
@Entity
public class Baojia extends Model {

    @OneToOne
    public Toubiao toubiao;

    @OneToOne
    public Specification specification;

    public Double price;

    public Double secondPrice;

    public Double thirdPrice;

}
