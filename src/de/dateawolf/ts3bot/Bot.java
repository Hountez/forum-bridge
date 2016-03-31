package de.dateawolf.ts3bot;

import de.stefan1200.jts3serverquery.JTS3ServerQuery;
import de.stefan1200.jts3serverquery.TS3ServerQueryException;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Bot {
	static JTS3ServerQuery query;
	boolean debug = false;
	
	public static void date()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String uhrzeit = sdf.format(new Date());
		System.out.print(uhrzeit);
	}
	
	public static void main(String[] args) {
		
		String url1 = "jdbc:mysql://date-a-wolf.de/wcf";
		String url2 = "jdbc:mysql://date-a-wolf.de/TS3_3.0.11.2";
		String user = "";
		String password = "";
		int Rows = 0;
		Connection con = null;
		Connection con2 = null;
		
		
		// Herstellen der Verbindungen
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			con = DriverManager.getConnection(url1, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			con2 = DriverManager.getConnection(url2, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Auswählen der nicht eingetragenen User
		String Users = "SELECT userID FROM wcf3_user WHERE activationCode = 0 AND eingetragen = 0";
		String AnzahlR= "SELECT COUNT(*) FROM wcf3_user WHERE activationCode = 0 AND eingetragen = 0";
		Statement stmtRows = null;
		Statement stmtSelect = null;
		ResultSet rsRow = null;
		ResultSet rs = null;
		String userID = "";
		try {
			stmtSelect = con.createStatement();
			stmtRows = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			rsRow = stmtSelect.executeQuery(AnzahlR);
			rs = stmtRows.executeQuery(Users);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			while(rsRow.next()){
				try {
				
				Rows=rsRow.getInt(1);
				System.out.println(Rows);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		ResultSetMetaData rsMetaData = null;
		try {
			rsMetaData = rs.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int columnCount = 0;

		
		try {
			columnCount = rsMetaData.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String[] UserIDS = new String[Rows];
		
		try {
			while(rs.next()) {
			  for( int i = 1 ; i<=Rows ; i++ ) {
			    userID =(rs.getString(1)) ;
			    
			    UserIDS[i-1]=userID;
			    System.out.println(" userID: " + UserIDS[Rows-1]);
			    rs.next();
			  } 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		date();
		System.out.println(" userID: " + userID);
		System.out.println(Arrays.toString(UserIDS));
		
		
		String[] TSUUIDS = new String[Rows];
		
		//Suchen der Passenden ID im Teamspeak Server
		for (int c = 0; c<=Rows-1; c++){
			String TSUID = "SELECT userOption30 FROM wcf3_user_option_value WHERE userID = " + UserIDS[c] +"";
			Statement stmtSelect2 = null;
			ResultSet rs2 = null;
			String UUID = "";
			try {
				stmtSelect2 = con.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				rs2 = stmtSelect2.executeQuery(TSUID);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			ResultSetMetaData rsMetaData2 = null;
			try {
				rsMetaData2 = rs2.getMetaData();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			int columnCount2 = 0;
			try {
				columnCount2 = rsMetaData2.getColumnCount();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				while(rs2.next()) {
					for( int i = 1 ; i<=Rows ; i++ ) {
						UUID =(rs2.getString(1) ) ; 
						TSUUIDS[c]=UUID;
						rs.next();
					} 
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			date();
			System.out.println(" UUID: " + UUID);
			System.out.println(Arrays.toString(TSUUIDS));
		}
		
		
		//Suchen der Passenden UserID auf dem Server
		String[] TSIDS = new String[Rows];
		for (int c = 0; c<=Rows-1; c++){
		String TS3ID = "SELECT client_id FROM clients WHERE client_unique_id = '" + TSUUIDS[c] +"'";
		Statement stmtSelect3 = null;
		ResultSet rs3 = null;
		String TSID = "";
		try {
			stmtSelect3 = con2.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			rs3 = stmtSelect3.executeQuery(TS3ID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ResultSetMetaData rsMetaData3 = null;
		try {
			rsMetaData3 = rs3.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int columnCount3 = 0;
		try {
			columnCount3 = rsMetaData3.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			while(rs3.next()) {
			  for( int i = 1 ; i<=Rows ; i++ ) {
			    TSID =(rs3.getString(1) ) ; 
			    TSIDS[c]=TSID;
			    rs.next();
			  } 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		date();
		System.out.println(" TSID: " + TSID);
		System.out.println(Arrays.toString(TSIDS));
		}
		
		//Eintragen via Telnet
		query = new JTS3ServerQuery();
		try
		{
			// Connect to TS3 Server, set your server data here
			try {
				query.connectTS3Query("date-a-wolf.de", 10011);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Login with an server query account. If needed, uncomment next line!
			query.loginTS3("serveradmin", "");
			
			
			// Select virtual Server
			query.selectVirtualServer(3);
			
			query.doCommand("clientupdate client_nickname=Forum");
			//TODO sgid ändern
			for (int c = 0; c<=Rows-1; c++){
				query.doCommand("servergroupaddclient sgid=26 cldbid="+TSIDS[c]+"");
			}

		}
		catch (TS3ServerQueryException sqe)
		{
			date();
			System.err.println(" An error occurred while connecting to the TS3 server, stopping now! More details below.");
			
			if (sqe.getFailedPermissionID() >= 0)
			{
				HashMap<String, String> permInfo = null;
				try
				{
					// This needs the permission b_serverinstance_permission_list
					permInfo = query.getPermissionInfo(sqe.getFailedPermissionID());
					date();
					System.err.println(" Missing permission!");
				}
				catch (Exception e)
				{
					// Ignore this exception to make sure, that a missing b_serverinstance_permission_list don't quit this program. 
				}
			}
			
		}
		
		//Hinzufügen des Users zur Gruppe "Registriert" !!!NICHT MÖGLICH SERVER NEUSTART BENÖTIGT!!!
		/*String Insert = "INSERT INTO group_server_to_client (group_id, server_id, id1, id2) VALUES (56, 3, " + TSID + ", 0)";
		Statement stmtSelect4 = null;
		try {
			stmtSelect4 = con2.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			int rs4 = stmtSelect4.executeUpdate(Insert);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
		
		//Updaten des Statuses eingetragen im Forum
		for (int c = 0; c<=Rows-1; c++){
			if(UserIDS[c]==""||UserIDS[c]==null || TSUUIDS[c]==""||TSUUIDS[c]==null || TSIDS[c]==""||TSIDS[c]==null){
				date();
				System.out.println(" Array leer, beende und gehe weiter!");
			} else {
				String Update = "UPDATE wcf3_user SET eingetragen = 1 WHERE userID = " + UserIDS[c] +"";
				Statement stmtSelect5 = null;
				try {
					stmtSelect5 = con.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					int rs5 = stmtSelect5.executeUpdate(Update);
				} catch (SQLException e) {
					e.printStackTrace();
				}		
		
				//Test ausgabe und Trennen der Verbindungen
				date();
				System.out.println(" Alles OK");
			}
		}
		try {
			con.close();
			con2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
