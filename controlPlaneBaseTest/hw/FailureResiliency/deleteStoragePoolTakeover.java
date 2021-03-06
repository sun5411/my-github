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
public class deleteStoragePoolTakeover extends TakeoverBaseClass {
                
    @BeforeClass
    public void deleteStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();        
        Assert.assertTrue(func.createStorageProperty(),"Error : Create Storage property failed!");
        Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
        Assert.assertNotNull(super.add_N_Shares(10),"Shares could not be added");
        Assert.assertTrue(func.createNStoragePools(10),"Error : Create Storagepool failed!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteStorageServerPool() throws InterruptedException{
       Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagepool failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(super.takeover(), "Error : Storage takeover failed!");        
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
        Assert.assertTrue(this.destroyProject(),"Unable to delete project from the ZFS appliance");
    }
}
