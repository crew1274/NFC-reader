package NFC_Application;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Program extends Frame implements ItemListener , ActionListener
{
	     static Menu menu;
	     static MenuItem menuitem1;
		 static MenuItem menuitem2;
		 static MenuItem menuitem3;
		 static MenuItem menuitem4;
		 static String dbname="nfc";
		 static Calendar calendar = new GregorianCalendar();  //建立一個 Calendar
		 static Frame frm;
		 static Label lab1=new Label();
		 static Label lab2=new Label();
		 static Label lab3=new Label();
		 static Label lab4=new Label();
		 static Label lab5=new Label();
		 static Label lab6=new Label();
		 static Label lab7=new Label();
		 static TextArea ta1=new TextArea();
		 static Checkbox ckb1=new Checkbox("");
		 static TextField txf1=new TextField("");
		 static Button btn=new Button("清除");
		 static Button btn1=new Button("簽到");
		 static String c="insert into checkout(serial,year,month,day,hr,min,sec) VALUES(?,?,?,?,?,?,?)";
		 static String d = "select * from checkout where serial =";

		 public static void main(String args[])
		  {
			JDBC db = new JDBC(dbname,"gigabyte","crew1274");
			SerialTest st = new SerialTest();
			st.initialize();
			Thread t=new Thread() {
				public void run() {

				}
			};
			t.start();
			txf1.requestFocus();
			Program frm = new Program();
			frm.setVisible(true);
			//frm.setLayout(null); // 取消版面配置
		    frm.setSize(600,600); // 設定視窗的像素
		    //frm.setBackground(new Color(255,102,204));   // 設定背景顏色
		    frm.setLocation(500,300); // 設定視窗的位置
		    frm.add(lab1);
		    frm.add(lab2);
		    frm.add(lab3);
		    frm.add(lab4);
		    frm.add(lab5);
		    frm.add(lab6);
		    frm.add(lab7);
		    frm.add(ta1);
		    frm.add(txf1);
		    frm.add(btn);
		    frm.add(btn1);
		    frm.add(ckb1);
		    frm.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){System.exit(0);}});
		    txf1.requestFocus();
		    lab1.setText("現在時間:");
		    lab2.setText("抓取時間中...");
		    lab3.setText("目前連接的資料庫:");
		    lab4.setText(dbname);
		    lab5.setText("讀取的卡號:");
		    lab6.setText("紀錄");
		    lab1.setLocation(10,50);
		    lab1.setSize(100,20);
		    lab2.setLocation(110,50);
		    lab2.setSize(500,20);
		    lab3.setLocation(10,100);
		    lab3.setSize(160,20);
		    lab4.setLocation(170,100);
		    lab4.setSize(150,20);
		    lab5.setLocation(10,150);
		    lab5.setSize(100,20);
		    lab6.setLocation(10,250);
		    lab6.setSize(100,30);
		    lab7.setLocation(10,280);
		    lab7.setSize(500,30);
		    ta1.setLocation(10,310);
		    ta1.setSize(500,200);
		    lab1.setFont(new Font("Serief",Font.BOLD,20));
		    lab2.setFont(new Font("Serief",Font.BOLD,20));
		    lab3.setFont(new Font("Serief",Font.BOLD,20));
		    lab4.setFont(new Font("Serief",Font.BOLD,20));
		    lab5.setFont(new Font("Serief",Font.BOLD,20));
		    lab6.setFont(new Font("Serief",Font.BOLD,20));
		    lab7.setFont(new Font("Serief",Font.BOLD,22));
		    ta1.setFont(new Font("Serief",Font.BOLD,20));
		    txf1.setFont(new Font("Serief",Font.BOLD,20));
		    btn.setFont(new Font("Serief",Font.BOLD,20));
		    btn1.setFont(new Font("Serief",Font.BOLD,20));
		    btn.setForeground(Color.red);
		    txf1.setBounds(120,150,200,30);
		    btn.setBounds(360,150,60,30);
		    btn1.setBounds(150,200,60,60);
		    menuitem1 = new MenuItem("one");
	        //menuitem1.addActionListener();
	        menuitem2 = new MenuItem("two");
	        //menuitem2.addActionListener();
	        menuitem3 = new MenuItem("three");
	        //menuitem3.addActionListener();
	        menuitem4 = new MenuItem("登出");
	        //menuitem4.addActionListener();
	        menu = new Menu("選單");
	        menu.setFont(new Font("Serief",Font.BOLD,20));
	        menu.add(menuitem1);
	        menu.add(menuitem2);
	        menu.add(menuitem3);
	        menu.addSeparator();
	        menu.add(menuitem4);
	        MenuBar menubar = new MenuBar();
	        menubar.add(menu);
	        frm.setMenuBar(menubar);
		    //ckb1.setBounds(160,270,10,10);
		    btn.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	txf1.requestFocus();
		        	txf1.setText("");
		                }});
		    btn1.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	if(txf1.getText().length()>10)
		        	{
		        		lab7.setText("卡號不符格式!");
		        		lab7.setForeground(Color.BLUE);
		        	}
		        	else{
		        	db.insertTable(c,txf1.getText(),calendar.get(Calendar.YEAR),(calendar.get(Calendar.MONTH)+1),
		        			calendar.get(Calendar.DATE),calendar.get(Calendar.HOUR_OF_DAY),
		        			calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)
		        			);
		        	lab7.setText("    上次簽到時間");
		        	ta1.setText(db.SelectTable(d,txf1.getText()));


		        	}
		                }});
		    while(true)
		    {
		    	 setTime();
		    	 lab2.setText(calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+
		    			 calendar.get(Calendar.DATE)+"日"+ calendar.get(Calendar.HOUR_OF_DAY)+"時"+
		    			 calendar.get(Calendar.MINUTE)+"分"+calendar.get(Calendar.SECOND)+"秒");

		  }

		  }
		 public void actionPerformed(ActionEvent e)
		 {


		 }



		 private static void setTime()  //設定系統時間
		 {
		     calendar.setTimeInMillis( System.currentTimeMillis() );
		 }
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			// TODO Auto-generated method stub

		}
		 }
