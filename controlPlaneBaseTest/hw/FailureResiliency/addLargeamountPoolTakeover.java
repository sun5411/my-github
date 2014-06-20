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
public class addLargeamountPoolTakeover extends TakeoverBaseClass {
               
    @BeforeClass
    public void addStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();        
        Assert.assertTrue(func.createStorageProperty(),"Error : Create Storage property failed!");
        Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
	Assert.assertNotNull(super.add_N_Shares(1000),"Shares could not be added");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addStorageServerLargeamountPool() throws InterruptedException{
       //Added 1000 stPools
       
       Assert.assertTrue(func.createNStoragePools(1000),"Error : Create Storagepool failed!");                     
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(super.takeover(),"Takeover failed");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
        Assert.assertTrue(this.destroyProject(),"Unable to delete project from the ZFS appliance");
    }
}
