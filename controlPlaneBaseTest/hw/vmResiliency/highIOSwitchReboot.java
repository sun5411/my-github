package com.oracle.nimbula.qa.ha.hw.vmResiliency;

import com.oracle.nimbula.qa.ha.IPpoolUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.OrchestrationUtil;
import com.oracle.nimbula.qa.ha.common.NimbulaHAPropertiesReader;
import com.oracle.nimbula.qa.ha.hw.FailureResiliency.TakeoverBaseClass;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.oracle.nimbula.qa.ha.hardware.IBSwitch;
import java.net.UnknownHostException;

/**
 *
 * @author Sun Ning
 */
public class highIOSwitchReboot extends TakeoverBaseClass {
    InstanceUtil instanceUtil;    
    IPpoolUtil ip;
    String ipPoolEntry;
    String vmUUID;    
    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException{
        super.setup();        
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();        
        instanceUtil = new InstanceUtil();
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");  
        super.addStartOrchestration("/tmp/vmResiliency_writenMountedVolumeFailover.json");
        
        List<String> instances = super.orch.getListOfInstanceNames();
        vmUUID = instances.get(0);        
        Assert.assertTrue(instanceUtil.checkVolumeSanity(vmUUID), "Faild to check volume sanity");
    }
    
    @Test(alwaysRun=true,threadPoolSize = 5, invocationCount = 5, timeOut=129600000)
    public void aa_testHighIO() throws InterruptedException {
        Thread t = Thread.currentThread();
        long threadid = t.getId();
        Assert.assertTrue(instanceUtil.checkVolumeMultipleDDthreads(threadid, vmUUID), "dd thread failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_switchFailover() throws InterruptedException{
        Assert.assertTrue(super.switchFailover(), "Failed to perform switch failover");        
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
