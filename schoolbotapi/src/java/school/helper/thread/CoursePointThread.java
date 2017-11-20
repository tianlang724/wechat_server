package school.helper.thread;

import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;

public class CoursePointThread {
    private Thread t;
    public CoursePointThread(WebContentManager wcm, String usertoken) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
//                String point = "{\"公共基础课\":\"5\",\"基础课\":\"8.5\",\"专业基础课\":\"2\",\"本学科选修课\":\"4\",\"跨学科选修课\":\"5\",\"实验选修课\":\"1\",\"必修环节\":\"3\"}";
                String point = "";
                try {
                    point = wcm.getAcquiredPoint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(point);
                new MySqlOperator().updateCoursePoint(usertoken, point);
            }
        });
        t.start();
    }
}
