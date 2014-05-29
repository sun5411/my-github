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
public class addLargeamountVolumeNetworkFailure extends TakeoverBaseClass {   
            
    @BeforeClass
    public void addStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();                
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
    public void bb_StorageNetworkFailure() throws InterruptedException{
        Thread.sleep(120000);
        //Network Failure about 5 mins        
        Assert.assertTrue(this.networkFailure(), "Failed to inject network failure.");
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteCreatedVolumes(),"Error : Delete Volumes failed!");
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
