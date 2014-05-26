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
public class addLargeamountVolumeNetworkFailure extends ControlPlaneBaseTest {
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
            Assert.assertTrue(zfs.failback(), "Error : Storage failback failed!");
        }
        Assert.assertTrue(func.createStorageProperty(),"Error : Create Storage property failed!");
        Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
        Assert.assertTrue(func.createStoragePool(),"Error : Create Storagepool failed!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addVolumes() throws InterruptedException{
        Assert.assertTrue(func.createVolumes(100, true), "Error : Create Volumes failed!");
        while (!func.areVolumesOnline()){
            logger.info("Not all volumes are online yet");
            Thread.sleep(30000);
        }
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        //Network Failure about 5 mins
        Assert.assertTrue(zfs.isAlive(), "Error : Storage isn't alive!");
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteCreatedVolumes();
        func.deleteStoragePool();
        func.deleteStorageServer();
    }
}
