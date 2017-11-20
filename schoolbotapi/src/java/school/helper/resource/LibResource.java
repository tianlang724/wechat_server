package school.helper.resource;

import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;
import school.helper.storage.UserPrivacyInfo;
import school.helper.thread.ChoosenCourseThread;
import school.helper.thread.LibHistoryThread;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/lib")
public class LibResource {

    @Path("/history")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getAcquiredPoint(@QueryParam("usertoken") String usertoken) throws Exception{
//        UserPrivacyInfo privacyInfo =  LocalDataStorage.get(usertoken);
//        if (privacyInfo == null) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.FIND_NO_USER_PRIVACY);
//        String username = privacyInfo.getUsername();
//        String password = privacyInfo.getPassword();
//        WebContentManager webContentManager = new WebContentManager(username, password, true);
//        return webContentManager.getReadingHistoryFromLib();
//        return new MySqlOperator().getLibInfo(usertoken, MySqlOperator.TYPE_LIB_READING_HISTORY);
        UserPrivacyInfo privacyInfo = new MySqlOperator().getUserPrivacyFromDB(usertoken, MySqlOperator.TYPE_LIB_ACCOUNT);
        String username = privacyInfo.getUsername();
        String password = privacyInfo.getPassword();
        System.out.println(username + " " + password);
        new LibHistoryThread(new WebContentManager(username, password, true), usertoken);
    }
}
