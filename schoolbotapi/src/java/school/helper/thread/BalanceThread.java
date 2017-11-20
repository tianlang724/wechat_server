package school.helper.thread;

import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;

public class BalanceThread {
    private Thread t;
    public BalanceThread(WebContentManager wcm, String usertoken) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String balance = "";
                try {
                    balance = wcm.getEcardBalance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(balance);
                new MySqlOperator().updateUserEcardBalance(usertoken, balance);
            }
        });
        t.start();
    }
}
