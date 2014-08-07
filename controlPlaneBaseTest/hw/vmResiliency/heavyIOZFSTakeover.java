package com.oracle.nimbula.qa.ha.hw.vmResiliency;

import com.oracle.nimbula.qa.ha.IPpoolUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.hw.FailureResiliency.TakeoverBaseClass;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class heavyIOZFSTakeover extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    IPpoolUtil ip;
    String ipPoolEntry;
    String vmName;
    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();        
        instanceUtil = new InstanceUtil();
        
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");  
        
        super.addStartOrchestration("/tmp/vmResiliency_writenMountedVolumeFailover.json");
        
        vmName = super.orch.getListOfInstanceNames().get(0);
        Assert.assertTrue(instanceUtil.checkVolumeSanity(vmName),"Volume check failed");
    }
    
    @Test(alwaysRun=true,threadPoolSize = 5, invocationCount = 5, timeOut=129600000)
    public void HighIO() throws InterruptedException {
        Thread t = Thread.currentThread();
        long threadid = t.getId();
        Assert.assertTrue(instanceUtil.checkVolumeMultipleDDthreads(threadid, vmName), "dd thread failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(this.takeover(), "Error : Storage takeover failed!");        
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {        
        super.stopDeleteOrchestration();
        ip.deleteIPPoolEntry(ipPoolEntry);
        ip.deleteIPpool();        
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
