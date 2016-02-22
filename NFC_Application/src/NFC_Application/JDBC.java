package NFC_Application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {
  private Connection con = null; //Database objects
  //連接object
  private Statement stat = null;
  //執行,傳入之sql為完整字串
  private ResultSet rs = null;
  //結果集
  private PreparedStatement pst = null;
  //執行,傳入之sql為預儲之字申,需要傳入變數之位置
  //先利用?來做標示

  private String dropdbSQL = "checkout";
  private String selectSQL = "select * from checkout ";
  private static String createdbSQL = "CREATE TABLE nfc.checkout(" +
		    "    serial     VARCHAR(10) " +
		    "  , year    INTEGER " +
		    "  , month    INTEGER " +
		    "  , day    INTEGER " +
		    "  , hr    INTEGER " +
		    "  , min    INTEGER " +
		    "  , sec    INTEGER )" ;

  private String insertdbSQL = "insert into checkout('serial','year','month','day','hr','min','sec')";

  //private static String selectSQL = "select * from User where id='546' ";

  public JDBC(String dbname,String account,String password)
  {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      //註冊driver
      con = DriverManager.getConnection(
      "jdbc:mysql://140.116.39.225/"+dbname+"?useUnicode=true&characterEncoding=Big5",
      account,password);

    }
    catch(ClassNotFoundException e)
    {
      System.out.println("DriverClassNotFound :"+e.toString());
    }//有可能會產生sqlexception
    catch(SQLException x) {
      System.out.println("Exception :"+x.toString());
    }


  }
  //建立table的方式
  //可以看看Statement的使用方式
  public void createTable(String c)
  {
    try
    {
      stat = con.createStatement();
      stat.executeUpdate(c);
    }
    catch(SQLException e)
    {
      System.out.println("CreateDB Exception :" + e.toString());
    }
    finally
    {
      close();
    }
  }
  //新增資料
  //可以看看PrepareStatement的使用方式
  // private String insertdbSQL = "insert into User(id,day,passwd) "
	      // "select ifNULL(max(id),0)+1,?,? FROM User";
  public void insertTable(String c,String serial,int y,int m,int day,int hr,int min,int sec)
  {
    try
    {
      pst = con.prepareStatement(c);
      pst.setString(1, serial);
      pst.setInt(2, y);
      pst.setInt(3, m);
      pst.setInt(4, day);
      pst.setInt(5, hr);
      pst.setInt(6, min);
      pst.setInt(7, sec);
      pst.executeUpdate();
    }
    catch(SQLException e)
    {

      System.out.println("InsertDB Exception :" + e.toString());
    }
    finally
    {
      close();
    }
  }
  //查詢資料
  //可以看看回傳結果集及取得資料方式

  public String SelectTable(String  d,String  c)
  {
	  String b = "";
    try
    {
      stat = con.createStatement();
      rs = stat.executeQuery(d+"'"+c+"'");
      //System.out.println("ID\t\tName\t\tPASSWORD");
      while(rs.next())
      {
        System.out.println(rs.getString("serial")+"\t\t"+rs.getString("year")+"年"+rs.getString("month")+"月"+
        		rs.getString("day")+"日"+rs.getString("hr")+"時"+rs.getString("min")+"分"+rs.getString("sec")+"秒"
        		);
        b=b+rs.getString("year")+"年"+rs.getString("month")+"月"+
        		rs.getString("day")+"日"+rs.getString("hr")+"時"+rs.getString("min")+"分"+
        		rs.getString("sec")+"秒\n"

        		;
      }
    }
    catch(SQLException e)
    {
      System.out.println("DropDB Exception :" + e.toString());
    }
    finally
    {
      close();
    }
	return b;
  }

  public void checkdb(String c)
  {
    try
    {
      stat = con.createStatement();
      rs = stat.executeQuery(c);
      System.out.println("ID\t\tName\t\tPASSWORD");
      while(rs.next())
      {
        System.out.println(rs.getInt("id")+"\t\t"+
            rs.getString("name")+"\t\t"+rs.getString("passwd"));
      }
    }
    catch(SQLException e)
    {
      System.out.println("DropDB Exception :" + e.toString());
    }
    finally
    {
      close();
    }
  }
  //完整使用完資料庫後,記得要關閉所有Object
  //否則在等待Timeout時,可能會有Connection poor的狀況
  public void close()
  {
    try
    {
      if(rs!=null)
      {
        rs.close();
        rs = null;
      }
      if(stat!=null)
      {
        stat.close();
        stat = null;
      }
      if(pst!=null)
      {
        pst.close();
        pst = null;
      }
    }
    catch(SQLException e)
    {
      System.out.println("Close Exception :" + e.toString());
    }
  }


  public static void main(String[] args)
  {
    //測看看是否正常
    JDBC test = new JDBC("nfc","gigabyte","crew1274");
    //test.dropTable();
    test.createTable(createdbSQL);
    //test.insertTable("yku", "12356");
    //test.insertTable("yku2", "7890");
    //test.SelectTable("741DC5BE");

  }
}