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
public class storageServerRestart_iscsi extends TakeoverBaseClass {
    @BeforeClass
    public void addStServer_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        Assert.assertTrue(func.createStorageProperty(),"Error : Create Storage property failed!");
        Assert.assertTrue(func.createStorageServer(),"Error : Create Storageserver failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerRestart() throws InterruptedException{
        Assert.assertTrue(super.rebootZFSmaster(), "Error : ZFS storage master reboot failed!");
        Thread.sleep(180000);
        Assert.assertTrue(func.isStorageServerOnline(),"Error : Storage Server must be online!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
