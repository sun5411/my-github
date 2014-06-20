package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.OrchestrationUtil;
import com.oracle.nimbula.test_framework.resource.types.Orchestration;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.oracle.nimbula.qa.ha.IPpoolUtil;

/**
 *
 * @author Sun Ning
 */
public class addOrchestrationStorageVolumeTakeover extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    List<OrchestrationUtil> orchObj;
    IPpoolUtil ip;
    String ipPoolEntry;
    String resName;
    List<String> orchFiles = new LinkedList<>();
    
    @BeforeClass
    public void addOrch_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();
        instanceUtil = new InstanceUtil();
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");
        
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM1.json");
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM2.json");
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM3.json");
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM4.json");
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM5.json");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addOrchVolumes() throws InterruptedException{
        orchObj = super.addListOfOrchestrations(orchFiles);
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Thread.sleep(20000);
        Assert.assertTrue(this.takeover(), "Error : Storage takeover failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        super.deleteListOfOrchestrations(orchObj);
        ip.deleteIPPoolEntry(ipPoolEntry);
        ip.deleteIPpool();
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
