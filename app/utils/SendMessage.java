package utils;

import org.joda.time.DateTime;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Humin on 4/9/14.
 */
public class SendMessage {

    public String createSmsSendXml(Map<String, String> map)
    {
        StringPlus sp = new StringPlus();
        sp.append(String.format("<Group Login_Name=\"%s\" Login_Pwd=\"%s\" InterFaceID=\"%s\" OpKind=\"0\">", map.get("LOGIN_NAME"), map.get("PASSWORD"), StringPlus.trim(map.get("USER_NAME"))));
        sp.append(String.format("<E_Time>%s</E_Time>",""));
        sp.append("<Item>");
        sp.append("<Task>");
        sp.append(String.format("<Recive_Phone_Number>%s</Recive_Phone_Number>", map.get("MOBILE")));
        sp.append(String.format("<Content><![CDATA[%s]]></Content>", map.get("MESSAGE")));
        sp.append(String.format("<Search_ID>%s</Search_ID>", map.get("TASK_ID")));
        sp.append("</Task>");
        sp.append("</Item>");
        sp.append("</Group>");
        return sp.value();
    }

    public static String MD5(String encryptContent)
    {
        String result = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(encryptContent.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++)
            {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16) buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            result = buf.toString().toUpperCase();

        }
        catch (NoSuchAlgorithmException e)
        {

        }
        return result;
    }

    public void sendSms(String mobile, String message, String taskid)
    {
        try{
            Map<String, String> map=new HashMap<String, String>();
            map.put("LOGIN_NAME","bjtcyy");
            map.put("PASSWORD",MD5("bjtcyy1"));
            map.put("MOBILE",mobile);
            map.put("MESSAGE",message);
            map.put("TASK_ID",taskid);
            String _interface_xml = createSmsSendXml(map);

            System.out.println(_interface_xml);
            String result = Http.post("http://qdif.vcomcn.cn/Opration.aspx",_interface_xml);
            System.out.println("result:"+result);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
