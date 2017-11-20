package school.helper.thread;

import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;

public class LibHistoryThread {
    private Thread t;
    public LibHistoryThread(WebContentManager wcm, String usertoken) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String history = "";
                try {
                    history = wcm.getReadingHistoryFromLib();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(history);
                new MySqlOperator().updateLibReadingHistory(usertoken, history);
            }
        });
        t.start();
    }
}
