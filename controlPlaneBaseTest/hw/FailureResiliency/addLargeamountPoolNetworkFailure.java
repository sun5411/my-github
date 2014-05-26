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
public class addLargeamountPoolNetworkFailure extends ControlPlaneBaseTest {
    NimbulaHAPropertiesReader haProp = NimbulaHAPropertiesReader.getInstance();
    Zfs zfs;
    String username = null;
    String password = null;
    String hostname = null;
            
    @BeforeClass
    public void addStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        this.hostname = haProp.getNimbulaZFS_HOSTNAME();
        this.username = haProp.getNimbulaZFS_USERNAME();
        this.password = haProp.getNimbulaZFS_PASSWORD();
        zfs = new Zfs(username,password,hostname,null,null);
        if (zfs.peerInCluster()){
            //Assert.assertTrue(zfs.failback(), "Error : Storage failback failed!");
        }
        Assert.assertTrue(func.createStorageProperty(),"Error : Create Storage property failed!");
        Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addStorageServerLargeamountPool() throws InterruptedException{
       //Added 1000 stPools
       Assert.assertTrue(func.createStoragePool(),"Error : Create Storagepool failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        //Exec network failure about 5 mins
        //zfs.networkFailure();
        Assert.assertTrue(zfs.isAlive(), "Error : Storage isn't alive!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteStorageServer();
    }
}
