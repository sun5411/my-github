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
public class addVolumeTakeover extends ControlPlaneBaseTest {
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
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addVolumes() throws InterruptedException{
        Assert.assertTrue(func.createVolumes(10, true), "Error : Create Volumes failed!");
        while (!func.areVolumesOnline()){
            logger.info("Not all volumes are online yet");
            Thread.sleep(3000);
        }

    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(zfs.takeover(), "Error : Storage takeover failed!");
        while(!zfs.peerInCluster()){
            logger.info("Storage server isn't alive yet");
            Thread.sleep(3000);
        }
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteCreatedVolumes(), "Error : Delete storage volumes failed!");
        Assert.assertTrue(func.deleteStoragePool(), "Error : Delete storage pool failed!");
        Assert.assertTrue(func.deleteStorageServer(), "Error : Delete storage server failed!");
    }
}
