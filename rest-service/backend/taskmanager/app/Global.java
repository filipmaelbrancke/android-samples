import java.util.List;
import java.util.Map;

import models.Task;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Yaml;

import com.avaje.ebean.Ebean;


public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("Application has started. Loading initial data.");
		InitialData.insert(app);
	}
	
	static class InitialData {
		
		public static void insert(Application app) {
			
			if (Ebean.find(Task.class).findRowCount() == 0) {
				
				@SuppressWarnings("unchecked")
				Map<String, List<Object>> all = (Map<String, List<Object>>)Yaml.load("initial-data.yml");
				
				// Insert tasks
				Ebean.save(all.get("tasks"));
				
			}
			
		}
	}

}
