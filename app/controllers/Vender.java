package controllers;

import java.util.List;

import models.ApplicationRole;
import models.Files;
import models.Material;
import models.Profile;
import models.User;
import play.mvc.Controller;

public class Vender extends Controller {

	public static void register(){
	
		List<Material> materialList = Material.findAll();
		render(materialList);
	}
	
	
	public static void save(){
		String username = params.get("username");
		String password = params.get("password");
		String[] materials = params.getAll("material");
		String registration_number = params.get("registration_number");
		String registration_assets = params.get("registration_assets");
		String registration_address = params.get("registration_address");
		String bank_name = params.get("bank_name");
		String account_name = params.get("account_name");
		String tfn = params.get("tfn");
		String legal_person = params.get("legal_person");
		String factory_name = params.get("factory_name");
		String factory_address = params.get("factory_address");
		String first_supply = params.get("first_supply");
		String business_model = params.get("business_model");
		String contact_name = params.get("contact_name");
		String contact_job = params.get("contact_job");
		String contact_phone = params.get("contact_phone");
		String sales_name = params.get("sales_name");
		String sales_job = params.get("sales_job");
		String sales_phone = params.get("sales_phone");
		String[] files = params.getAll("files");
		
		User user = null;
		Material material = null;
		Files file = null;
		if(username!=null && password!=null && !"".equals(username) && !"".equals(password)){
			user = User.find("username=?", username).first();
			if(user == null){
				user = new User(username, password, ApplicationRole.getByName("user"));
				user.save();
			}
			
			Profile profile = Profile.find("user.id=?", user.id).first();
			if(profile == null){
				profile = new Profile();
				profile.user = user;
			}
			
			if(materials!=null){
				for(String material_id: materials){
					material = Material.find("id=?", Long.getLong(material_id)).first();
					if(material!=null)
						profile.materials.add(material);
				}
			}
			profile.registration_number = registration_number;
			profile.registration_assets = registration_assets;
			profile.registration_address = registration_address;
			profile.bank_name = bank_name;
			profile.account_name = account_name;
			profile.tfn = tfn;
			profile.legal_person = legal_person;
			profile.factory_name = factory_name;
			profile.factory_address = factory_address;
			profile.first_supply = first_supply;
			profile.business_model = business_model;
			profile.contact_name = contact_name;
			profile.contact_job = contact_job;
			profile.contact_phone = contact_phone;
			profile.sales_name = sales_name;
			profile.sales_job = sales_job;
			profile.sales_phone = sales_phone;
			
			if(files!=null){
				for(String f: files){
					file = Files.findById(Long.getLong(f));
					if(file!=null){
						profile.files.add(file);
					}
				}
			
			}
			
			profile.save();
		}
		
		
		
		renderText("");
	}
	
}
