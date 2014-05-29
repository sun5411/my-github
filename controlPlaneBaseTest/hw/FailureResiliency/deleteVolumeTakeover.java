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
public class deleteVolumeTakeover extends TakeoverBaseClass {
           
    @BeforeClass
    public void deleteStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        Assert.assertTrue(func.createVolumes(10, true), "Error : Create Volumes failed!");
        while (!func.areVolumesOnline()){
            logger.info("Not all volumes are online yet");
            Thread.sleep(30000);
        }
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteVolumes() throws InterruptedException{
        Assert.assertTrue(func.deleteCreatedVolumes(),"Error : Delete Volumes failed!");
        Assert.assertFalse(func.areVolumesOnline(), "Error : Delete Volume failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(this.takeover(), "Error : Storage takeover failed!");        
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
