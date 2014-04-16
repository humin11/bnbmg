package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * Created by Humin on 4/16/14.
 */
@Entity
public class Prop extends Model {

    public String name;

    public String value;
}
