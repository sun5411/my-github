package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import java.net.UnknownHostException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Sun Ning
 */
public class addStorageVolumeRestart_iscsi extends TakeoverBaseClass {
            
    @BeforeClass
    public void addStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        Assert.assertTrue(func.createStorageProperty(),"Error : Create Storage property failed!");
        Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!"); 
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addVolumes() throws InterruptedException{
        Assert.assertTrue(func.createVolumes(30, true), "Error : Create Volumes failed!");
        while (!func.areVolumesOnline()){
            logger.info("Not all volumes are online yet");
            Thread.sleep(30000);
        }

    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerRestart() throws InterruptedException{
        Thread.sleep(60000);
        Assert.assertTrue(super.rebootZFSmaster(), "Error : ZFS storage master reboot failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteCreatedVolumes(),"Error : Delete Volumes failed!");
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
