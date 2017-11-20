package school.helper.resource;

import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;
import school.helper.storage.UserPrivacyInfo;
import school.helper.thread.BalanceThread;
import school.helper.thread.ExpendThread;
import school.helper.thread.RechargeThread;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/ecard")
public class BalanceResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getEcardBalance(@QueryParam("usertoken") String usertoken) throws Exception {
//        UserPrivacyInfo privacyInfo =  LocalDataStorage.get(usertoken);
//        if (privacyInfo == null) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.FIND_NO_USER_PRIVACY);
//        String username = privacyInfo.getUsername();
//        String password = privacyInfo.getPassword();
//        WebContentManager webContentManager = new WebContentManager(username, password, false);
//        String balanceNode = webContentManager.getEcardBalance();
//        return balanceNode;
        UserPrivacyInfo privacyInfo = new MySqlOperator().getUserPrivacyFromDB(usertoken, MySqlOperator.TYPE_ECARD_ACCOUNT);
        String username = privacyInfo.getUsername();
        String password = privacyInfo.getPassword();
        System.out.println(username + " " + password);
        new BalanceThread(new WebContentManager(username, password, false), usertoken);
//        return new MySqlOperator().getUserEcardInfo(usertoken, MySqlOperator.TYPE_ECARD_BALANCE);
    }

    @Path("/expend")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getEcardExpendDetail(@QueryParam("usertoken") String usertoken) throws Exception {
//        UserPrivacyInfo privacyInfo =  LocalDataStorage.get(usertoken);
//        if (privacyInfo == null) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.FIND_NO_USER_PRIVACY);
//        String username = privacyInfo.getUsername();
//        String password = privacyInfo.getPassword();
//        WebContentManager webContentManager = new WebContentManager(username, password, false);
//        String expendDetail = webContentManager.getRecentlySpecificExpend();
//        return expendDetail;
//        return new MySqlOperator().getUserEcardInfo(usertoken, MySqlOperator.TYPE_ECARD_EXPEND);
        UserPrivacyInfo privacyInfo = new MySqlOperator().getUserPrivacyFromDB(usertoken, MySqlOperator.TYPE_ECARD_ACCOUNT);
        String username = privacyInfo.getUsername();
        String password = privacyInfo.getPassword();
        System.out.println(username + " " + password);
        new ExpendThread(new WebContentManager(username, password, false), usertoken);
    }

    @Path("/recharge")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getEcardRechargeDetail(@QueryParam("usertoken") String usertoken) throws Exception{
//        UserPrivacyInfo privacyInfo =  LocalDataStorage.get(usertoken);
//        if (privacyInfo == null) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.FIND_NO_USER_PRIVACY);
//        String username = privacyInfo.getUsername();
//        String password = privacyInfo.getPassword();
//        WebContentManager webContentManager = new WebContentManager(username, password, false);
//        String rechargeDetail = webContentManager.getRecentlyRecharge();
//        return rechargeDetail;
//        return new MySqlOperator().getUserEcardInfo(usertoken, MySqlOperator.TYPE_ECARD_RECHARGE);
        UserPrivacyInfo privacyInfo = new MySqlOperator().getUserPrivacyFromDB(usertoken, MySqlOperator.TYPE_ECARD_ACCOUNT);
        String username = privacyInfo.getUsername();
        String password = privacyInfo.getPassword();
        System.out.println(username + " " + password);
        new RechargeThread(new WebContentManager(username, password, false), usertoken);
    }

}
