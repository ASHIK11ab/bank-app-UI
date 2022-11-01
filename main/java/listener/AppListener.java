package listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import runnables.AutoDebitRDRunnable;
import runnables.DepositIntrestCreditRunnable;
import util.Factory;


public class AppListener implements ServletContextListener {
	private Thread depositIntrestCreditThread;
	private Thread autoDebitRDThread;
	
	public void contextInitialized(ServletContextEvent sce)  { 
    	try {    		    		
    		Factory.init();
    		System.out.println("factory initialized");
    		
    		depositIntrestCreditThread = new Thread(new DepositIntrestCreditRunnable());
    		depositIntrestCreditThread.start();
    		System.out.println("\nDeposit intrest credit thread started in context");
    		
    		autoDebitRDThread = new Thread(new AutoDebitRDRunnable());
    		autoDebitRDThread.start();
    		System.out.println("\nAuto Debit RD thread started in context");
    		
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
	
	
	public void contextDestroyed(ServletContextEvent sce) {
		depositIntrestCreditThread.interrupt();
		autoDebitRDThread.interrupt();
		System.out.println("context destroyed");
	}
	
}
