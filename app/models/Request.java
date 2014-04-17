package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import controllers.CRUD;
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Request extends Model {

    @CRUD.Exclude
	@Required
	public String name;
	
	@Required
	@ManyToOne
    @CRUD.Exclude
	public User user;

    //Zhaobiao or Yaobiao
    @CRUD.Exclude
	@Required
	public String style;

    @CRUD.Exclude
    @ManyToMany
    public List<Profile> profiles = new ArrayList<Profile>();
	
	@Required
    public Date starttime;
	
	@Required
    public Date stoptime;

//    @CRUD.Exclude
//	@ManyToMany
//	public List<Files> files = new ArrayList<Files>();

    // 0 - Toubiaoing
    // 1 - Already Toubiao
    // 2 - Toubiao Again
    @CRUD.Exclude
    public int status = 0;

    @CRUD.Exclude
    public Boolean is_finished=false;

    @CRUD.Exclude
    @ManyToOne
    public Profile zhongbiao;

    @CRUD.Exclude
    public int zbCount;

    @OneToMany
    @CRUD.Exclude
    public List<Specification> specifications = new ArrayList<Specification>();

    @Lob
    @CRUD.Exclude
    public String description;

    public Blob image;

}
