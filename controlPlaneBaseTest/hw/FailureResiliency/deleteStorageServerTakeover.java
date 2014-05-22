package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import com.oracle.nimbula.qa.ha.hardware.Zfs;
import java.net.UnknownHostException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import com.oracle.nimbula.qa.ha.common.NimbulaHAPropertiesReader;

/**
 *
 * @author Sun Ning
 */
public class deleteStorageServerTakeover extends ControlPlaneBaseTest {
    NimbulaHAPropertiesReader haProp = NimbulaHAPropertiesReader.getInstance();
    Zfs zfs;
    String username = null;
    String password = null;
    String hostname = null;
            
    @BeforeClass
    public void deleteStServer_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        this.hostname = haProp.getNimbulaZFS_HOSTNAME();
        this.username = haProp.getNimbulaZFS_USERNAME();
        this.password = haProp.getNimbulaZFS_PASSWORD();
        zfs = new Zfs(username,password,hostname,null,null);
        if (zfs.peerInCluster()){
            Assert.assertTrue(zfs.failback(), "Error : Failbake failed!");
        }
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteStorageServer() throws InterruptedException{
       Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(zfs.takeover(), "Error : Storage takeover failed!");
        Assert.assertTrue(zfs.isAlive(), "Error : Storage isn't alive!");
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteStorageServer();
    }
}
