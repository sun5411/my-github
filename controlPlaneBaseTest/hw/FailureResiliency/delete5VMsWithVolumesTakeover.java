package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.InstanceUtil;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.oracle.nimbula.qa.ha.IPpoolUtil;

/**
 *
 * @author Sun Ning
 */
public class delete5VMsWithVolumesTakeover extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    IPpoolUtil ip;
    String ipPoolEntry;
    String resName;
    
    @BeforeClass(alwaysRun = true, timeOut = 900000)
    public void setup() throws InterruptedException{
        super.setup();
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();
        instanceUtil = new InstanceUtil();
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");        
        Assert.assertTrue(0 == orch.addOrchestration("/tmp/hwFailure_Launch5VMswithVolumes.json"),"Add Orchestration failed!");
        Assert.assertTrue(0 == orch.startOrchestration(),"Orchestration start failed!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addOrchVolumes() throws InterruptedException{
        Assert.assertTrue(0 == orch.stopOrchestration(),"Orchestration stop failed!");
        Assert.assertTrue(0 == orch.deleteOrchestration(),"Orchestration deleted failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Thread.sleep(20000);
        Assert.assertTrue(this.takeover(), "Error : Storage takeover failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        ip.deleteIPPoolEntry(ipPoolEntry);
        ip.deleteIPpool();
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
