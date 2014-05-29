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
public class deleteStorageServerTakeover extends TakeoverBaseClass {
    @BeforeClass
    public void deleteStServer_setup() throws InterruptedException, UnknownHostException{
        super.setup();        
        Assert.assertTrue(func.createStorageServer(),"Failed to create storage server");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteStorageServer() throws InterruptedException{
       Assert.assertTrue(func.deleteStorageServer(),"Error : Create Storageserver failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(this.takeover(), "Error : Storage takeover failed!");
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
