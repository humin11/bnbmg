package utils;

public class StringPlus
{
   StringBuilder _sb=null;
  
   public StringPlus()
   {
	   _sb=new StringBuilder();
   }
   
   public String append(String message)
   {
	   return _sb.append(message+"\n").toString();
   }
   
   public String insert(String message)
   {
	   _sb.delete(0, _sb.length());
	   return _sb.append(message+"\n").toString();
   }
   
   public String value()
   {
	   return _sb.toString();
   }
   
   /*
    * ����ַ�ͷβ�ַ�
    * @param content:�ַ�����
    * @param replace:Ҫ�����ַ�
    */
   public static String trim(String content,String replace)
   {
	   return content.replaceAll("^"+replace+"*|"+replace+"*$", "");
   }
   
   public static String trim(String content)
   {
	   if(content==null)
		  return "";
	   else 
		  return content;
   }
}
