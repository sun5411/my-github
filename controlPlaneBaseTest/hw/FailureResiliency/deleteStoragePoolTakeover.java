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
public class deleteStoragePoolTakeover extends ControlPlaneBaseTest {
    NimbulaHAPropertiesReader haProp = NimbulaHAPropertiesReader.getInstance();
    Zfs zfs;
    String username = null;
    String password = null;
    String hostname = null;
            
    @BeforeClass
    public void deleteStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        this.hostname = haProp.getNimbulaZFS_HOSTNAME();
        this.username = haProp.getNimbulaZFS_USERNAME();
        this.password = haProp.getNimbulaZFS_PASSWORD();
        zfs = new Zfs(username,password,hostname,null,null);
        if (zfs.peerInCluster()){
            Assert.assertTrue(zfs.failback(), "Error : Storage failback failed!");
        }
        Assert.assertTrue(func.createStorageProperty(),"Error : Create Storage property failed!");
        Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
        Assert.assertTrue(func.createStoragePool(),"Error : Create Storagepool failed!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteStorageServerPool() throws InterruptedException{
       //Delete 10 storage pools
       Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagepool failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(zfs.takeover(), "Error : Storage takeover failed!");
        Assert.assertTrue(zfs.isAlive(), "Error : Storage isn't alive!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
