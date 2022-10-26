package model.account;

public class CurrentAccountBean extends RegularAccountBean {
    private static int minimumBalance = 10000;

    @Override
    public int initiateTransaction(float amount) { return 200; }
    
    // Getters
    public static int getMinimumBalance() {
    	return minimumBalance;
    }
//    
//    public int getDailyLimit() {
//    	return dailyLimit;
//    }
    
    // Setters
    public static void setMinimumBalance(int minBalance) {
    	CurrentAccountBean.minimumBalance = minBalance;
    }
}