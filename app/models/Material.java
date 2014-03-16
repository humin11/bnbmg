package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Material extends Model {

	public String name;
	
	public String specification;
	
	public double thickness;
	
	public double width;
	
	public double weight;
	
	public String others;
	
}
