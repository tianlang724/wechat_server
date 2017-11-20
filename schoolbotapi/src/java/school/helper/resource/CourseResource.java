package school.helper.resource;
import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;
import school.helper.storage.UserPrivacyInfo;
import school.helper.thread.ChoosenCourseThread;
import school.helper.thread.CoursePointThread;
import school.helper.thread.ExpendThread;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/course")
public class CourseResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getCourseInfo(@QueryParam("usertoken") String usertoken) throws Exception{
//        UserPrivacyInfo privacyInfo =  LocalDataStorage.get(usertoken);
//        if (privacyInfo == null) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.FIND_NO_USER_PRIVACY);
//        String username = privacyInfo.getUsername();
//        String password = privacyInfo.getPassword();
//        WebContentManager webContentManager = new WebContentManager(username, password, false);
//        return webContentManager.getAllCourse();
//        return new MySqlOperator().getCourseInfo(usertoken, MySqlOperator.TYPE_COURSE_CHOOSEN);
        UserPrivacyInfo privacyInfo = new MySqlOperator().getUserPrivacyFromDB(usertoken, MySqlOperator.TYPE_ECARD_ACCOUNT);
        String username = privacyInfo.getUsername();
        String password = privacyInfo.getPassword();
        System.out.println(username + " " + password);
        new ChoosenCourseThread(new WebContentManager(username, password, false), usertoken);
    }

    @Path("/point")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getAcquiredPoint(@QueryParam("usertoken") String usertoken) throws Exception{
//        UserPrivacyInfo privacyInfo =  LocalDataStorage.get(usertoken);
//        if (privacyInfo == null) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.FIND_NO_USER_PRIVACY);
//        String username = privacyInfo.getUsername();
//        String password = privacyInfo.getPassword();
//        WebContentManager webContentManager = new WebContentManager(username, password, false);
//        return webContentManager.getAcquiredPoint();
//        return new MySqlOperator().getCourseInfo(usertoken, MySqlOperator.TYPE_COURSE_POINT);
        UserPrivacyInfo privacyInfo = new MySqlOperator().getUserPrivacyFromDB(usertoken, MySqlOperator.TYPE_ECARD_ACCOUNT);
        String username = privacyInfo.getUsername();
        String password = privacyInfo.getPassword();
        System.out.println(username + " " + password);
        new CoursePointThread(new WebContentManager(username, password, false), usertoken);
    }
}