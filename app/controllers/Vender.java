package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.csvreader.CsvReader;
import models.*;
import play.db.jpa.Blob;
import play.i18n.Messages;
import play.libs.MimeTypes;
import play.mvc.Controller;
import utils.SendMessage;

public class Vender extends Controller {

	public static void register(){
	
		List<Material> materialList = Material.findAll();
		render(materialList);
	}

    public static void checkUsername(String username){
        Boolean result= true;
        User user = User.find("username=?",username).first();
        if(user!=null){
            result = false;
        }

        renderJSON(result);
    }

    public static void checkCompany(String name){
        Boolean result= true;
        Profile profile = Profile.find("name=?",name).first();
        if(profile!=null){
            result = false;
        }

        renderJSON(result);
    }

    public static void postProperty(){
        String name = params.get("name");
        String value = params.get("value");
        Prop p = new Prop();
        p.name = name;
        p.value = value;
        p.save();
        renderText(p.id.toString());
    }

    public static void removeProperty(){
        String id = params.get("id");
        Prop p = Prop.findById(Long.valueOf(id));

        if(p!=null)
            p.delete();
        renderText(id);
    }


    public static void toImp(){
        render();
    }

    public static void baojia(Long id){
        Request req = Request.findById(id);
        render(req);
    }

    public static void baojia2(Long id, Long tbid){
        Request req = Request.findById(id);
        Toubiao toubiao = Toubiao.findById(tbid);
        render(req,toubiao);
    }

    public static void zbAgain(){
        String id = params.get("toubiaoid");
        String againComments = params.get("againComments");
        Toubiao toubiao = Toubiao.findById(Long.valueOf(id));
        toubiao.again = true;
        toubiao.againComments = againComments;
        toubiao.save();

        Request req = toubiao.request;
        req.status=2;
        req.save();
        redirect("/vender/tblist?id=" + id);
    }

    public static void tbList(Long id){
        Request req = Request.findById(id);
        List<Toubiao> toubiaos = Toubiao.find("request.id",req.id).fetch();
        render(req, toubiaos);
    }

    public static void zhongbiao(Long id){
        Toubiao toubiao = Toubiao.findById(id);
        toubiao.status="1";
        toubiao.save();
        Request request = toubiao.request;
        request.status=1;
        request.save();
        List<Toubiao> toubiaos  = Toubiao.find("request.id=? and id!=?", request.id, id).fetch();
        for(Toubiao tb: toubiaos){
            tb.status="2";
            tb.save();
        }
        SendMessage m = new SendMessage();
        m.sendSms(toubiao.profile.contact_phone,"您已中标"+toubiao.request.name+"项目，请按时发货","000002");
        redirect("/admin/requests");
    }

    public static void toubiao(){
        String username = session.get("username");
        if(username !=null){
            User user = User.getByUserName(username);
            Profile profile = Profile.find("user.id=?",user.id).first();

            String payStyle = params.get("payStyle");
            String invoice = params.get("invoice");
            String comments = params.get("comments");
            String requestS = params.get("request");

            Request request = Request.findById(Long.valueOf(requestS));
            Toubiao toubiao = null;
            toubiao = Toubiao.find("request.id=? and profile.id=?", request.id, profile.id).first();
            if(toubiao==null) {
                toubiao = new Toubiao();
            }
            toubiao.profile = profile;
            toubiao.user = user;
            toubiao.request = request;
            toubiao.payStyle = payStyle;
            toubiao.invoice = invoice;
            toubiao.comments = comments;
            toubiao.again=false;
            toubiao.save();

            String price = "0";
            String price2 = "0";
            String price3 = "0";
            Baojia baojia = null;
            for(Specification spec: request.specifications){
                price = params.get("price"+spec.id);
                price2 = params.get("price"+spec.id+"-2");
                price3 = params.get("price"+spec.id+"-3");
                baojia = Baojia.find("toubiao.id=? and specification.id=?", toubiao.id, spec.id).first();
                if(baojia==null){
                    baojia = new Baojia();
                    baojia.toubiao = toubiao;
                    baojia.specification = spec;
                }
                if(price !=null && !"".equals(price) && !"0".equals(price))
                    baojia.price = Double.valueOf(price);
                if(price2!=null && !"".equals(price2) && !"0".equals(price2))
                    baojia.secondPrice = Double.valueOf(price2);
                if(price3!=null && !"".equals(price3) && !"0".equals(price3))
                    baojia.thirdPrice = Double.valueOf(price3);
                baojia.save();

                if(!toubiao.baojias.contains(baojia)) {

                    toubiao.baojias.add(baojia);
                    toubiao.save();
                }

            }

        }
        redirect("/");
    }

    public static void show(){
        String username = session.get("username");
        if(username !=null){
            User user = User.getByUserName(username);
            Profile profile = Profile.find("user.id=?",user.id).first();

            render(profile);
        }
    }

    public static void remove(){
        Long id = Long.valueOf(params.get("id"));
        Files files = Files.findById(id);
        if(files.image.getFile().exists()){
            files.image.getFile().delete();
        }
        files.delete();
        renderText("");
    }


    public static void getBusinessModel(String modelid){
        Map<String, String> modelMap = new HashMap<String, String>();
        modelMap.put("1", Messages.get("model.ziying"));
        modelMap.put("2", Messages.get("model.jingxiao"));
        modelMap.put("3", Messages.get("model.guakao"));
        modelMap.put("0", Messages.get("model.qita"));
        renderText(modelMap.get(modelid));
    }

    public static void upload(File[] files) throws FileNotFoundException {
        Files f = null;
        for(File file: files){
            f = new Files();
            f.name = file.getName();
            f.path = "";
            f.image = new Blob();
            f.image.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));
            f.save();
            renderText(f.id);
        }
    }
	
	public static void save() throws Throwable {
		String username = params.get("username");
        String name = params.get("name");
		String password = params.get("password");
		String[] materials = params.getAll("material");
		String registration_number = params.get("registration_number");
		String registration_assets = params.get("registration_assets");
        String registration_assets_unit = params.get("registration_assets_unit");
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
        String contact_email = params.get("contact_email");
		String sales_name = params.get("sales_name");
		String sales_job = params.get("sales_job");
		String sales_phone = params.get("sales_phone");
		String[] files = params.getAll("files");
		
		User user = null;
		Material m = null;
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
                    m = Material.find("id=?", Long.valueOf(material_id.trim())).first();
                    if(m!=null)
                        profile.materials.add(m);
				}
			}
            profile.name = name;
			profile.registration_number = registration_number;
			profile.registration_assets = registration_assets;
			profile.registration_address = registration_address;
            profile.registration_assets_unit = registration_assets_unit;
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
            profile.contact_email = contact_email;
			profile.sales_name = sales_name;
			profile.sales_job = sales_job;
			profile.sales_phone = sales_phone;
			
			if(files!=null){
				for(String f: files){
                    file = Files.find("id=?", Long.valueOf(f)).first();
					if(file!=null){
						profile.files.add(file);
					}
				}
			
			}
			
			profile.save();
		}


        session.put("username", username);

        Secure.redirectToOriginalURL();
	}

    public static String getRandomPwd(){
        Random rd = new Random();
        String n="";
        int getNum;
        do {
            getNum = Math.abs(rd.nextInt())%10 + 48;//产生数字0-9的随机数
            //getNum = Math.abs(rd.nextInt())%26 + 97;//产生字母a-z的随机数
            char num1 = (char)getNum;
            String dn = Character.toString(num1);
            n += dn;
        } while (n.length()<8);
        return n;
    }

    public static void impSpec(File file){
        if(file!=null){
            List<Specification> specifications = new ArrayList<Specification>();
            try{
                FileReader input = new FileReader(file);
                CsvReader reader = new CsvReader(input);
                reader.readHeaders();
                while(reader.readRecord()){
                    String materialName = reader.getValues()[0];
                    String specification = reader.getValues()[1];
                    String number = reader.getValues()[2];
                    String unit = reader.getValues()[3];
                    String company = reader.getValues()[4];
                    String date = reader.getValues()[5];
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void imp(File file){
        if (file != null) {
            try {
                FileReader input = new FileReader(file);
                CsvReader reader = new CsvReader(input);
                reader.readHeaders();
                Profile profile = null;
                User user = null;
                String value="";
                Material material = null;
                String unit = "万元";
                String business = "0";
                SendMessage m = new SendMessage();
                while (reader.readRecord()) {
                    if (reader.getValues().length < 22) {
                        continue;
                    }
                    value = reader.getValues()[0];
                    user = User.find("username=?", value).first();
                    if(user == null){
                        user = new User(value, getRandomPwd(),ApplicationRole.getByName("user"));
                        user.save();
                    }

                    profile = Profile.find("user.id=?", user.id).first();
                    if(profile == null){
                        profile = new Profile();
                        profile.user=user;
                    }
                    value = reader.getValues()[1];
                    profile.name=value;
                    value = reader.getValues()[2];
                    material = Material.find("name=?",value).first();
                    if(material == null){
                        material = new Material();
                        material.name = value;
                        material.save();

                    }

                    if(!profile.materials.contains(material))
                        profile.materials.add(material);
                    value = reader.getValues()[3];

                    value = reader.getValues()[4];
                    //profile.business_model = value;
                    value = reader.getValues()[5];
                    profile.registration_number = value;
                    value = reader.getValues()[6];
                    if(value.contains("（美元）")){
                        value = value.replace("（美元）","").trim();
                        unit = "万美元";
                    }
                    profile.registration_assets = value;
                    profile.registration_assets_unit=unit;
                    value = reader.getValues()[7];
                    profile.registration_address = value;
                    value = reader.getValues()[8];
                    profile.bank_name = value;
                    value = reader.getValues()[9];
                    profile.account_name = value;
                    value = reader.getValues()[10];
                    profile.tfn = value;
                    value = reader.getValues()[11];
                    profile.factory_name = value;
                    value = reader.getValues()[12];
                    profile.factory_address = value;
                    value = reader.getValues()[13];
                    profile.first_supply = value;
                    value = reader.getValues()[14];
                    profile.legal_person = value;
                    value = reader.getValues()[15];
                    profile.contact_name = value;
                    value = reader.getValues()[16];
                    profile.contact_job = value;
                    value = reader.getValues()[17];
                    profile.contact_phone = value;
                    value = reader.getValues()[18];
                    profile.sales_name = value;
                    value = reader.getValues()[19];
                    profile.sales_job = value;
                    value = reader.getValues()[20];
                    profile.sales_phone = value;
                    value = reader.getValues()[21];
                    if(value.equals("自营")){
                        business = "1";
                    } else if(value.equals("经销")){
                        business = "2";
                    } else if(value.equals("挂靠")){
                        business = "3";
                    } else {
                        business = "0";
                    }
                    profile.business_model = business;
                    profile.save();

                    m.sendSms(profile.contact_phone,"您的信息已导入,用户名:"+user.username+",密码:"+user.password+"，请登录比价平台上传资质文件","000001");
                }
            } catch (Exception e) {

            }

        }
        redirect("/admin/profiles");
    }

}
