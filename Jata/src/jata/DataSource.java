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

public class DataSource {
	
    private String name;
    private String url;
    private String user;
    private String password;
    
    
    private String fullPath;
    
    
    

    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return fullPath;
	}
	public void setUrl(String url) {
		this.url = url;
		fullPath = makeFullPath(url);
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
    
    

	public DataSource() {
		
	}
	
	public DataSource(String name) {
		this.name = name;
	}
	
	

	final static String APPLICATION_P = "/resources/application.properties";

	
	static Map<String, DataSource> dataSourceMap = null;
	static DataSource get(String name) {
		loadDataSources();
		if (!dataSourceMap.containsKey(name)) {
			dataSourceMap.put(name, new DataSource(name));
		}
		return dataSourceMap.get(name);
	}
    static Collection<DataSource> allDataSources() {
        loadDataSources();
        return dataSourceMap.values();
    }
    static void loadDataSources() {
    	if (dataSourceMap != null)
    		return;    	
        dataSourceMap = new HashMap();
        Map<Pattern, DataSourceUpdateCall> pMap = new HashMap();
        pMap.put(Pattern.compile("JataDB\\.([a-zA-Z]+)\\.url\\s*=\\s*(.+)"), (name, value) -> get(name).setUrl(value));		
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
	

	
    final static String ACCESS_DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
    final static String SQLITE_DRIVER = "org.sqlite.JDBC";

    final static String ACCESS_URL = "jdbc:ucanaccess://?;newDatabaseVersion=V2010";
    final static String SQLITE_URL = "jdbc:sqlite:?";

    final static String ACCESS_EXT = "mdb,accdb";
    final static String SQLITE_EXT = "db,sdb,sqlite,db3,s3db,sqlite3,sl3,db2,s2db,sqlite2,sl2";
  
    
    static Map<String, String> extMap = new HashMap();
    
    
    static {
    	addExtMap(ACCESS_EXT, ACCESS_URL);
    	addExtMap(SQLITE_EXT, SQLITE_URL);
    }

    static void addExtMap(String str, String url) {
    	Arrays.asList(str.split(",")).forEach(ext -> extMap.put(ext, url));    	
    }
    
    
    static String makeFullPath(String dbPath) {
        dbPath = dbPath.trim();
        int index = dbPath.lastIndexOf(".");
        if (index == -1) {
            return dbPath;
        } else {
            String ext = dbPath.substring(index+1).toLowerCase(); 
            String url = extMap.containsKey(ext) ? extMap.get(ext) : SQLITE_URL;            
            return url.replace("?", dbPath);
        }           
    }	
	
	
    
    
    

}
