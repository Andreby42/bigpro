package com.thc.platform.modules.sms.service;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.gexin.fastjson.JSON;
//
//import lombok.Data;

public class DBService {
	
//	private static final Logger logger = LoggerFactory.getLogger(DBService.class);
//
//	public static final String DB_DRIVER_MYSQL = "com.mysql.jdbc.Driver";
//	
//	private static final String URL = "jdbc:mysql://10.100.20.79:4001/thc_global_platform_extend?characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&allowMultiQueries=true&serverTimezone=GMT%2b8";
//	private static final String USER_NAME = "selectonly";
//	private static final String PWD = "35Nz2jp6pYRwcW7n";
//		
//	private static Connection getConn(String dbUrl, String username, String pwd) throws ClassNotFoundException, SQLException {
//		Class.forName(DB_DRIVER_MYSQL);
//		return DriverManager.getConnection(dbUrl, username, pwd);
//	}
//	
//	public static SmsRecordOut queryData(String tenantId, String mobile) {
//		// 处理SQL
//		String excuteSql = "SELECT mobiles, content FROM sms_send_record WHERE tenant_id = " 
//				+ tenantId + " AND mobiles = '" + mobile + "' ORDER BY create_time DESC limit 1";
//		
//		try(Connection conn = getConn(URL, USER_NAME, PWD);
//				Statement stmt = conn.createStatement();
//				ResultSet rs = stmt.executeQuery(excuteSql);) {
//			
//			SmsRecordOut out = new SmsRecordOut();
//	        while (rs.next()) {
//	            String mobiles = rs.getString("mobiles");
//	            String content = rs.getString("content");
//	            out.setMobile(mobiles);
//	            out.setContent(content);
//	        }
//	        
//	        return out;
//		} catch (Exception e) {
//			logger.error("数据库查询报错", e);
//			throw new RuntimeException(e);
//		}
//	}
//
//	@Data
//	public static class SmsRecordOut {
//		private String mobile;
//		private String content;
//		
//	}
//	
//	public static void main(String[] args) {
//		SmsRecordOut out = queryData("1009", "13969665349");
//		System.out.println(JSON.toJSONString(out));
//	}
	
}
