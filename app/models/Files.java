package models;

import javax.persistence.Entity;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Files extends Model {

    @CRUD.Hidden
	public String name;

    @CRUD.Hidden
	public String path;

    @Required
    public Blob image;

}
