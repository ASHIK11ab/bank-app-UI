package model.account;

public class SavingsAccountBean extends RegularAccountBean {
    public static float intrestRate;
    private static int minimumBalance = 1000;
//    public static int dailyLimit;

    @Override
    public int initiateTransaction(float amount) { return 200; }
    
    // Getters
    public static float getIntrestRate()  {
    	return intrestRate;
    }
    
    public static int getMinimumBalance() {
    	return minimumBalance;
    }
//    
//    public int getDailyLimit() {
//    	return dailyLimit;
//    }
    
    // Setters
    public static void setMinimumBalance(int minBalance) {
    	SavingsAccountBean.minimumBalance = minBalance;
    }
}