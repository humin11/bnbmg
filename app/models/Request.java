package models;

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Request extends Model {

	public String name;
	
	@ManyToOne
	public Material material;
	
	public String style;
	
	public Date starttime;
	
	public Date stoptime;
	
	public String specification;
	
	public double thickness;
	
	public double width;
	
	public double weight;
	
	public double others;
	
	public double amount;
	
	public String unit;
	
	public Date arrival_time;
	
	@Lob
	public String description;
	
	@ManyToMany
	public List<Files> files;
}
