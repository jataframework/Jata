package jata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataSource {
	
	private boolean mongodb;
    private String name;
    private String url;
    private String user;
    private String password;
    private String driver;

  
    
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public boolean isMongodb() {
		return mongodb;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
		makeFullPath();
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
    

	public DataSource(boolean mongo) {
		this.mongodb = mongo;		
	}
	
	public DataSource(String name, boolean mongo) {
		this(mongo);
		this.name = name;		
	}
	
	
	
    void makeFullPath() {
    	if (mongodb) {
    		this.url = "mongodb://"+url;
    	}
        url = url.trim();
        int index = url.lastIndexOf(".");
        if (index != -1) {
            String ext = url.substring(index+1).toLowerCase(); 
            String path = extMap.containsKey(ext) ? extMap.get(ext) : "?";            
            this.url = path.replace("?", url);
            this.driver = driverMap.get(ext);
            if (driver != null) {					// load the class here...
            	try {
					Class.forName(driver);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }           
    }	
	
	

	final static String APPLICATION_P = "/resources/application.properties";

	
	static Map<String, DataSource> dataSourceMap = null;
	public static DataSource get(String name) {
		loadDataSources();
		return dataSourceMap.get(name);
	}
	static DataSource get(String name, boolean mongo) {
		loadDataSources();
		if (!dataSourceMap.containsKey(name)) {
			dataSourceMap.put(name, new DataSource(name, mongo));
		}
		return dataSourceMap.get(name);
	}	
    public static Collection<DataSource> allDataSources() {
        loadDataSources();
        return dataSourceMap.values();
    }
    public static List<DataSource> allSqlDataSources() {
    	loadDataSources();
    	return dataSourceMap.values().stream().filter(ds->!ds.isMongodb()).collect(Collectors.toList());
    }
    public static List<DataSource> allMongoDataSources() {
    	loadDataSources();
    	return dataSourceMap.values().stream().filter(ds->ds.isMongodb()).collect(Collectors.toList());
    }
    static void loadDataSources() {
    	if (dataSourceMap != null)
    		return;    	
        dataSourceMap = new HashMap();
        Map<Pattern, DataSourceUpdateCall> pMap = new HashMap();
        pMap.put(Pattern.compile("jdbc\\.([a-zA-Z]+)\\.url\\s*=\\s*(.+)"), (name, value) -> get(name, false).setUrl(value));
        pMap.put(Pattern.compile("mongodb\\.([a-zA-Z]+)\\.url\\s*=\\s*(.+)"), (name, value) -> get(name, true).setUrl(value));
        try {
            List<String> dataSourceLines = Files.readAllLines(Paths.get(Jata.jarFolder()+APPLICATION_P));           
            for (String line : dataSourceLines) {
                for (Entry<Pattern, DataSourceUpdateCall> entry : pMap.entrySet()) {
                    Matcher m = entry.getKey().matcher(line);
                    if (m.find()) {
                        entry.getValue().update(m.group(1), m.group(2));
                        break;
                    }
                }
            }
        } catch (IOException e) {
                System.out.println("No application.properties found.");
        }	
    }	
	

	

  
    
    static Map<String, String> extMap = new HashMap();
    static Map<String, String> driverMap = new HashMap();
    static {
    	addExtMap("mdb,accdb", "jdbc:ucanaccess://?;newDatabaseVersion=V2010");
    	addExtMap("db,sdb,sqlite,db3,s3db,sqlite3,sl3,db2,s2db,sqlite2,sl2", "jdbc:sqlite:?");
    	addDriverMap("mdb,accdb", "net.ucanaccess.jdbc.UcanaccessDriver");
    	addDriverMap("db,sdb,sqlite,db3,s3db,sqlite3,sl3,db2,s2db,sqlite2,sl2", "org.sqlite.JDBC");
    }

    static void addExtMap(String str, String url) {
    	Arrays.asList(str.split(",")).forEach(ext -> extMap.put(ext, url));    	
    }
    static void addDriverMap(String str, String driver) {
    	Arrays.asList(str.split(",")).forEach(ext -> driverMap.put(ext, driver));    	
    }
    
    

	
	
    
    
    

}
