package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import controllers.CRUD.Exclude;
import play.db.jpa.Model;

@Entity
public class Profile extends Model {

	@Exclude
	@OneToOne
	public User user;
	
	public String name;

	@ManyToMany
	public List<Material> materials;
	
	public String registration_number;
	
	public String registration_assets;

    public String registration_assets_unit;
	
	public String registration_address;
	
	public String bank_name;
	
	public String account_name;
	
	public String tfn;
	
	public String legal_person;
	
	public String factory_name;
	
	public String factory_address;
	
	public String first_supply;
	
	public String business_model;
	
	public String contact_name;
	
	public String contact_job;
	
	public String contact_phone;

    public String contact_email;
	
	public String sales_name;
	
	public String sales_job;
	
	public String sales_phone;
	
	@ManyToMany
	public List<Files> files = new ArrayList<Files>();

    @Exclude
    @ManyToMany
    public List<Request> requests = new ArrayList<Request>();
	
	public int is_audit=0;
}
