package school.helper.thread;

import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;

public class RechargeThread {
    private Thread t;
    public RechargeThread(WebContentManager wcm, String usertoken) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String recharge = "";
                try {
                    recharge = wcm.getRecentlyRecharge();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(recharge);
                new MySqlOperator().updateUserEcardRecharge(usertoken, recharge);
            }
        });
        t.start();
    }
}
