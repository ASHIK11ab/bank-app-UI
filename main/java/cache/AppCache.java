package cache;

import model.*;

public class AppCache {
    private static Bank bank;


    public static void init() {
        bank = null;
    }


    // cache methods
    public static void cacheBank(Bank bank) {
        AppCache.bank = bank;
    }
    
    public static void cacheBranch(Branch branch) {
        AppCache.bank.addBranch(branch);
    }
    
    public static void cacheIntegratedBank(IntegratedBank integratedBank) {
        bank.addIntegratedBank(integratedBank);
    }
    
    // Getters
    public static Bank getBank() {
    	return bank;
    }
    
    public static Branch getBranch(int branchId) {
        return bank.getBranch(branchId);
    }

    public static IntegratedBank getIntegratedBank(int bankId) {
        return bank.getIntegratedBank(bankId);
    }
}