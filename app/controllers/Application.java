package controllers;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import play.*;
import play.mvc.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import models.*;
import utils.SendMessage;

@With(Deadbolt.class)
public class Application extends Controller {

	@Restrictions({@Restrict("superadmin"), @Restrict("admin"), @Restrict("operator"), @Restrict("system"), @Restrict("user")})
    public static void index() {
        List<Profile> profiles = Profile.find("is_audit=?",0).fetch();
        List<Request> requests = Request.find("status!=?",1).fetch();
        List<Request> allRequests = Request.findAll();
        String username = session.get("username");
        List<Toubiao> toubiaos = null;
        Profile profile = null;
        User user = null;

        if(username !=null) {
            user = User.getByUserName(username);
            profile = Profile.find("user.id=?", user.id).first();
            if(profile!=null)
                toubiaos = Toubiao.find("profile.id=?", profile.id).fetch();
        }
        render(profiles, profile, requests, allRequests, toubiaos, user);
    }


    public static void test(String id){
        try {
            FileInputStream fileInputStream = new FileInputStream("/Users/Humin/Desktop/Book2.xls");
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheetAt(0);
            HSSFRow row1 = worksheet.getRow(0);
            System.out.println(row1.getLastCellNum());
            HSSFCell cellA1 = row1.getCell(0);
            String a1Val = cellA1.getStringCellValue();
            HSSFCell cellB1 = row1.getCell(1);
            String b1Val = cellB1.getStringCellValue();
            HSSFCell cellC1 = row1.getCell(2);
            String c1Val = cellC1.getStringCellValue();
            HSSFCell cellD1 = row1.getCell(3);
            String d1Val = cellD1.getStringCellValue();

            System.out.println("A1: " + a1Val);
            System.out.println("B1: " + b1Val);
            System.out.println("C1: " + c1Val);
            System.out.println("D1: " + d1Val);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        renderText("");
    }
}