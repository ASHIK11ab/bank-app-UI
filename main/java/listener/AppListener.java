package listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import util.Factory;


public class AppListener implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent sce)  { 
    	try {    		
    		Factory.init();
    		System.out.println("factory initialized");
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
	
}
