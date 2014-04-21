package controllers;

import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

import com.csvreader.CsvReader;
import models.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import play.db.jpa.Blob;
import play.i18n.Messages;
import play.libs.MimeTypes;
import play.mvc.Controller;
import utils.SendMessage;

public class Vender extends Controller {

    public static void editSpec(Long id){
        Specification spec = null;
        if(id!=null)
            spec = Specification.findById(id);
        List<Material> materials = Material.findAll();
        String  username = session.get("username");
        if(username!=null){

            User user = User.getByUserName(username);
            if(user.role.name.equalsIgnoreCase("operator") ) {
                materials = user.materials;
            }
        }
        render(spec, materials);
    }

    public static void newSpec(){
        List<Material> materials = Material.findAll();
        String  username = session.get("username");
        if(username!=null){

            User user = User.getByUserName(username);
            if(user.role.name.equalsIgnoreCase("operator") ) {
                materials = user.materials;
            }
        }
        render(materials);
    }

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
        List<Specification> specifications = Specification.em().createQuery("select r from Specification r join fetch r.properties s where s.id="+Long.valueOf(id), Specification.class).getResultList();

        for(Specification spec: specifications){
            spec.properties.remove(p);
            spec.save();
        }
        if(p!=null)
            p.delete();
        renderText(id);
    }

    public static void removeSpec(){
        String id = params.get("id");
        Specification spec = Specification.findById(Long.valueOf(id));
        List<Request> requests = Request.em().createQuery("select r from Request r join fetch r.specifications s where s.id="+Long.valueOf(id), Request.class).getResultList();

        for(Request req: requests){
            req.specifications.remove(spec);
            req.save();
        }
        if(spec!=null)
            spec.delete();
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
        Config config = Config.find("1=1").first();
        String message="";
        SendMessage m = new SendMessage();
        message="您已中标"+toubiao.request.name+"项目，请按时发货";
        if(config.msg_request_invite!=null && !"".equals(config.msg_request_invite)){
            message = config.msg_request_notification.replace("{request}", toubiao.request.name);
        }
        Profile p = toubiao.profile;
        if(p.contact_phone!=null && !"".equals(p.contact_phone))
             m.sendSms(p.contact_phone,message,"0000003");
        if(p.contact_email!=null && !"".equals(p.contact_email))
             m.sendMail(p.contact_email, "["+ Messages.get("application.name")+"]中标通知", message);

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
            Specification spec = null;
            Material material = null;
            try{
                FileInputStream fileInputStream = new FileInputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
                HSSFSheet worksheet = workbook.getSheetAt(0);
                HSSFRow row = null;
                HSSFCell cell = null;
                Map<Integer, String> headerMap = new HashMap<Integer, String>();
                Prop prop = null;
                if(worksheet.getLastRowNum()>0){
                    row = worksheet.getRow(0);
                    for(int i=0; i< row.getLastCellNum(); i++){
                        headerMap.put(Integer.valueOf(i), row.getCell(i).getStringCellValue());
                    }
                }
                for(int i=1; i<worksheet.getLastRowNum();i++){
                    row = worksheet.getRow(i);
                    cell = row.getCell(0);
                    String materialName = cell.getStringCellValue();

                    cell = row.getCell(1);
                    String specification = cell.getStringCellValue();

                    cell = row.getCell(2);
                    Double number = cell.getNumericCellValue();

                    cell = row.getCell(3);
                    String unit = cell.getStringCellValue();

                    cell = row.getCell(4);
                    String company = cell.getStringCellValue();

                    cell = row.getCell(5);
                    Date date = cell.getDateCellValue();

                    cell = row.getCell(6);
                    String description = cell.getStringCellValue();

                    spec = new Specification();

                    for(int x=7; x< row.getLastCellNum(); x++){
                        cell = row.getCell(x);
                        String value = "";
                        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                            value = String.valueOf(cell.getNumericCellValue());
                        }else {
                            value = cell.getStringCellValue();
                        }
                        prop = new Prop();
                        prop.name = headerMap.get(x);
                        prop.value = value;
                        prop.save();
                        spec.properties.add(prop);
                    }

                    spec.name = specification;
                    spec.specification = specification;
                    if (number != null && !"".equals(number))
                        spec.amount = number;
                    spec.unit = unit;
                    spec.company = company;
                    if (materialName != null && !"".equals(materialName)) {
                        material = Material.find("name=?", materialName.trim()).first();
                        spec.material = material;
                    }
                    spec.arrival_time = date;
                    spec.description = description;
                    spec.save();
                    specifications.add(spec);
                }

                renderJSON(specifications);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void imp(File file){
        String result="";
        String profiles="";
        int imported=0;
        int missed=0;

        if (file != null) {

            Config config = Config.find("1=1").first();

            FileInputStream fileInputStream = null;
            HSSFWorkbook workbook = null;
            try {
                fileInputStream = new FileInputStream(file);
                workbook = new HSSFWorkbook(fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Profile profile = null;
                User user = null;
                String value="";
                Material material = null;

                SendMessage m = new SendMessage();
                for(int x=0;x<workbook.getNumberOfSheets();x++){

                    HSSFSheet worksheet = workbook.getSheetAt(x);
                    HSSFRow row = null;
                    HSSFCell cell = null;
                    DecimalFormat df = new DecimalFormat("#");
                    df.setMaximumFractionDigits(0);

                    for(int i=1; i<worksheet.getLastRowNum();i++) {
                        String unit = "万元";
                        String business = "0";
                        try {
                        row = worksheet.getRow(i);
                        if(row.getLastCellNum()<22){
                            continue;
                        }
                        cell = row.getCell(0);
                        value = cell.getStringCellValue();
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

                        cell = row.getCell(1);
                        value = cell.getStringCellValue();
                        profile.name = value;

                        cell = row.getCell(2);
                        value = cell.getStringCellValue();
                        material = Material.find("name=?",value).first();
                        if(material == null){
                            material = new Material();
                            material.name = value;
                            material.save();

                        }
                        if(!profile.materials.contains(material))
                            profile.materials.add(material);

                        cell = row.getCell(3);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }

                        cell = row.getCell(4);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }

                        cell = row.getCell(5);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.registration_number = value;

                        cell = row.getCell(6);
                        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                            df.setMaximumFractionDigits(2);
                            value = String.valueOf(df.format(cell.getNumericCellValue()));
                        }else {
                            value = cell.getStringCellValue();
                        }
                        if(value.contains("美元")){
                            value = value.replace("（美元）","").trim();
                            unit = "万美元";
                        }
                        profile.registration_assets = value;
                        profile.registration_assets_unit=unit;


                        df.setMaximumFractionDigits(0);
                        cell = row.getCell(7);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.registration_address = value;

                        cell = row.getCell(8);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.bank_name = value;

                        cell = row.getCell(9);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.account_name = value;

                        cell = row.getCell(10);
                        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                            value = String.valueOf(cell.getNumericCellValue());
                        }else {
                            value = cell.getStringCellValue();
                        }
                        profile.tfn = value;

                        cell = row.getCell(11);
                        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                            value = String.valueOf(cell.getNumericCellValue());
                        }else {
                            value = cell.getStringCellValue();
                        }
                        profile.factory_name = value;

                        cell = row.getCell(12);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.factory_address = value;

                        cell = row.getCell(13);
                        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                            value = String.valueOf(df.format(cell.getNumericCellValue()));
                        }else {
                            value = cell.getStringCellValue();
                        }
                        profile.first_supply = value.replaceAll("年","");

                        cell = row.getCell(14);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.legal_person = value;

                        cell = row.getCell(15);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.contact_name = value;

                        cell = row.getCell(16);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.contact_job = value;


                        cell = row.getCell(17);
                        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                            value = String.valueOf(df.format(cell.getNumericCellValue()));
                        }else {
                            value = cell.getStringCellValue();
                        }
                        profile.contact_phone = value;

                        cell = row.getCell(18);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.sales_name = value;

                        cell = row.getCell(19);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
                        profile.sales_job = value;

                        cell = row.getCell(20);
                        if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                            value = String.valueOf(df.format(cell.getNumericCellValue()));
                        }else {
                            value = cell.getStringCellValue();
                        }
                        profile.sales_phone = value;

                        cell = row.getCell(21);
                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                value = String.valueOf(cell.getNumericCellValue());
                            }else {
                                value = cell.getStringCellValue();
                            }
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
                            imported+=1;

                            if(profile.contact_phone!=null) {
                                String message="您的信息已导入,用户名:"+user.username+",密码:"+user.password+"，请登录比价平台上传资质文件";
                                if (config.msg_import != null && !"".equals(config.msg_import)) {
                                    message = config.msg_import.replace("{username}", user.username).replace("{passowrd}", user.password);
                                }
                                m.sendSms(profile.contact_phone, message, "0000001");
                                if(profile.contact_email!=null && !"".equals(profile.contact_phone)){
                                    m.sendMail(profile.contact_email, "["+Messages.get("application.name")+"]信息导入",message);
                                }
                            }
                        } catch (Exception e) {
                            missed+=1;
                            if(!"".equals(profiles)){
                                profiles+=","+profile.name;
                            }else{
                                profiles+=profile.name;
                            }
                            e.printStackTrace();
                        }

                    }



                }



            result="成功导入"+imported+"条记录，丢失"+missed+"条记录。";
            if(!"".equals(profiles)){
                result+="丢失导入的供应商为:"+profiles;
            }
        }
        redirect("/admin/profiles?result="+ URLEncoder.encode(result));
    }

}
