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
public class addLargeamountPoolNetworkFailure extends TakeoverBaseClass {    
            
    @BeforeClass(alwaysRun = true, timeOut = 900000)
    public void addStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        Assert.assertNotNull(super.add_N_Shares(1000),"Shares could not be added");
        Assert.assertTrue(func.createStorageProperty(),"Error : Create Storage property failed!");
        Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addStorageServerLargeamountPool() throws InterruptedException{
       
       Assert.assertTrue(func.createNStoragePools(1000),"Error : Create Storage pools failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(this.networkFailure(5),"Failed to inject network failure on the master node");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
        Assert.assertTrue(this.destroyProject(),"Unable to delete project from the ZFS appliance");
    }
}
