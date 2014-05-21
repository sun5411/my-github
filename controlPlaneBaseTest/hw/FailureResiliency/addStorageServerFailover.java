package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import com.oracle.nimbula.qa.ha.hardware.Zfs;
import java.net.UnknownHostException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Sun Ning
 */
public class addStorageServerFailover extends ControlPlaneBaseTest {
    Zfs zfs;
    String username = "root";
    String password = "welcome1";
    String hostname = "slce10sn01-nas";
            
    @BeforeClass
    public void addStServer_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        zfs = new Zfs(username,password,hostname,null,null);
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addStorageServer() throws InterruptedException{
       Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        zfs.takeover();
        zfs.isAlive();
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        zfs.failback();
        func.deleteStorageServer();
    }
}
