package org.restcomm.connect.testsuite.http;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.mss.extension.SipStackTool;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.ShrinkWrapMaven;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.restcomm.connect.commons.Version;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author maria
 */

@RunWith(Arquillian.class)
public class OrganizationsEndpointTest extends EndpointTest {
    private final static Logger logger = Logger.getLogger(OrganizationsEndpointTest.class.getName());

    private static final String version = Version.getVersion();

    @ArquillianResource
    private Deployer deployer;
    @ArquillianResource
    URL deploymentUrl;

    private String superAdminUsername = "administrator@company.com";
    private String adminFriendlyName = "Default Administrator Account";
    private String adminAccountSid = "ACae6e420f425248d6a26948c17a9e2acf";
    private String superAdminAuthToken = "77f8c12cc7b8f8423e5c38b035249166";
    private String adminPassword = "RestComm";

    private String commonAuthToken = "77f8c12cc7b8f8423e5c38b035249166";

    static SipStackTool tool1;

    private final String org1 = "ORafbe225ad37541eba518a74248f0ac4c";
    private final String org2 = "ORafbe225ad37541eba518a74248f0ac4c";
    
    @BeforeClass
    public static void beforeClass() {
    }

    @Test
    public void testGetAccount() {
        // Get Account using admin email address and user email address
        JsonObject adminAccount = RestcommAccountsTool.getInstance().getAccount(deploymentUrl.toString(), superAdminUsername,
                superAdminAuthToken, superAdminUsername);
        assertTrue(adminAccount.get("sid").getAsString().equals(adminAccountSid));
    }

    /**
     * SuperAdmin is allowed to read any organization
     * this test will try to Read single organization and read list
     */
    @Test
    public void getOrganizationFromSuperAdminAccount(){
    	JsonObject organizationJsonObject = RestcommOrganizationsTool.getInstance().getOrganization(deploymentUrl.toString(), superAdminUsername, superAdminAuthToken, org1);
    	assertTrue(organizationJsonObject!=null);
    	logger.info("organization: "+organizationJsonObject);
    	
    	// only superadmin can read an org that does not affiliate with its account
    	organizationJsonObject = null;
    	organizationJsonObject = RestcommOrganizationsTool.getInstance().getOrganization(deploymentUrl.toString(), superAdminUsername, superAdminAuthToken, org1);
    	assertTrue(organizationJsonObject!=null);
    	
    	//only superadmin can read the whole list of organizations
    	JsonArray jsonArray = null;
    	jsonArray = RestcommOrganizationsTool.getInstance().getOrganizationList(deploymentUrl.toExternalForm(), superAdminUsername, superAdminAuthToken, null);
    	logger.info("organization list: "+jsonArray);
    	assertTrue(jsonArray!=null);
    	assertTrue(jsonArray.size() == 2);
    }

    /**
     * Admis can read only affliated organization
     * this test will try to Read single organization and read list
     */
    @Test
    public void getOrganizationFromAdministratorAccount(){}
    /**
     * Developers can not read organization
     * this test will try to Read single organization and read list
     */
    @Test
    public void getOrganizationFromDeveloperAccount(){}
    /**
     * getOrganizationListByStatus
     */
    @Test
    public void getOrganizationListByStatus(){}
    
    @Deployment(name = "OrganizationsEndpointTest", managed = true, testable = false)
    public static WebArchive createWebArchiveNoGw() {
        logger.info("Packaging Test App");
        logger.info("version");
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "restcomm.war");
        final WebArchive restcommArchive = ShrinkWrapMaven.resolver()
                .resolve("org.restcomm:restcomm-connect.application:war:" + version).withoutTransitivity()
                .asSingle(WebArchive.class);
        archive = archive.merge(restcommArchive);
        archive.delete("/WEB-INF/sip.xml");
        archive.delete("/WEB-INF/conf/restcomm.xml");
        archive.delete("/WEB-INF/data/hsql/restcomm.script");
        archive.addAsWebInfResource("sip.xml");
        archive.addAsWebInfResource("restcomm.xml", "conf/restcomm.xml");
        archive.addAsWebInfResource("restcomm.script_accounts_test", "data/hsql/restcomm.script");
        logger.info("Packaged Test App");
        return archive;
    }

}
