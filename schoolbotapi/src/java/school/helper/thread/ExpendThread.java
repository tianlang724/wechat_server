package school.helper.thread;

import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;

public class ExpendThread {
    private Thread t;
    public ExpendThread(WebContentManager wcm, String usertoken) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String expend = "";
                try {
                    expend = wcm.getRecentlySpecificExpend();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(expend);
                new MySqlOperator().updateUserEcardExpend(usertoken, expend);
            }
        });
        t.start();
    }
}
