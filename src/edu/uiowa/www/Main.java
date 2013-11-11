package edu.uiowa.www;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class Main {
 
    private static String JDBC_CONNECTION_URL;
    private static String JDBC_USERNAME;
    private static String JDBC_PASSWORD;
    private static Logger logger = Logger.getLogger(Main.class.getName());       
 
     
    public static void main(String[] args) throws IOException, SQLException {
    	Properties config = new Properties();
    	config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
    	JDBC_CONNECTION_URL=config.getProperty("url");
    	JDBC_USERNAME=config.getProperty("username");
    	JDBC_PASSWORD=config.getProperty("password");
    	Connection connection = null;
        try {
 
            CSVLoader loader = new CSVLoader();
            connection=getCon();
            loader.setConnection(connection);
            loader.loadCSV("C:\\test-data.csv", "student_year_2013", true);           
            loader.loadCSV("C:\\population-by-county.csv", "population_by_county", true);           
            String query1="select county_name,population from population_by_county";
            logger.log(Level.INFO,"query1:"+query1);
            ResultSet resultSet1 = connection.createStatement().executeQuery(query1);
            List<List<String>> data = new ArrayList<List<String>>();
            while(resultSet1.next()){
            	List<String> values = new ArrayList<String>();
            	values.add(resultSet1.getString("county_name"));
            	values.add(resultSet1.getLong("population")+"");
            	data.add(values);
            }
            for(List<String> countyInfo: data){
            	String county_name=countyInfo.get(0).replaceAll("County", "").trim();
            	String query2="select count(*) as total from student_year_2013 where home_county="+"\""+county_name+"\"";
            	logger.log(Level.INFO,"query2:"+query2);
            	ResultSet resultSet2 = connection.createStatement().executeQuery(query2);
            	resultSet2.next();
            	long numStudents = resultSet2.getLong("total");
            	countyInfo.add(numStudents+"");
            	countyInfo.add((numStudents*1000.00)/Long.valueOf(countyInfo.get(1))+"");
            }           
            
            analyzeData(data);
            
            
            
             
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
        	if (null != connection)
				connection.close();
			
        }
    }
    
    private static void analyzeData(List<List<String>> data){
    	List<String> countiesNotSendingStudents = new ArrayList<String>();
    	float maxPerCapita=0;
    	String maxPerCapitaCountyName=null;
    	System.out.println("County Name   |  Population | No. of Students Sent | Per Capital Students Sent");
    	for(List<String> values: data){
    	// index 0 holds county name
    	System.out.print(values.get(0)+" | ");
    	// index 1 holds population
    	System.out.print(values.get(1)+" | ");
    	//index 2 holds total students from this home county
    	if(Long.valueOf(values.get(2))<1.00){
    		countiesNotSendingStudents.add(values.get(0));	
    	}
    	
    	System.out.print(values.get(2)+" | ");
    	//index 3 holds students per capita for this county
    	System.out.println(values.get(3)+" ");
    	if(maxPerCapita<Float.valueOf(values.get(3))){
    		maxPerCapita = Float.valueOf(values.get(3));
    		maxPerCapitaCountyName = values.get(0);
    	}
    	}
    	System.out.println("\nTotal Number of Counties that did not send students to University of Iowa = "+countiesNotSendingStudents.size()); 
    	System.out.println("They are as follows:");
    	for(String countyName: countiesNotSendingStudents){
    		System.out.println(countyName);
    	}
    	
    	System.out.println("County that sent highest students per capita is:"+maxPerCapitaCountyName);
    	System.out.println("with per capita: "+maxPerCapita+" per 1000 population.");
    	
    }
    
    
 
    private static Connection getCon() {
        Connection connection = null;
        try {
            try {
            	
				Object o = Class.forName("com.mysql.jdbc.Driver").newInstance();				
			} catch (InstantiationException e) {
				logger.log(Level.SEVERE, "JDBC Instantiation Error");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, "JDBC Instantiation Error");
				e.printStackTrace();
			}
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/student",JDBC_USERNAME,JDBC_PASSWORD);
 
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        return connection;
    }
}