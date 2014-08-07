package com.oracle.nimbula.qa.ha.hw.vmResiliency;

import com.oracle.nimbula.qa.ha.IPpoolUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.OrchestrationUtil;
import com.oracle.nimbula.qa.ha.hw.FailureResiliency.TakeoverBaseClass;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class largeNumberVMsAttachedVolumeTakeover extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    IPpoolUtil ip;
    String ipPoolEntry;    
    List<String> uuids;
    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)    
    public void setup() throws InterruptedException{
        super.setup();
        
        List<String> orchestrationsList = new LinkedList<>();
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach1.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach2.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach3.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach4.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach5.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach6.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach7.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach9.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach9.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach10.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach11.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach12.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach13.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach14.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach15.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach16.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach17.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach18.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach19.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach20.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach21.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach22.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach23.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach24.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach25.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach26.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach27.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach28.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach29.json");
        orchestrationsList.add("/tmp/vmResiliency_VMWith10volAttach30.json");
        
        uuids = new LinkedList<>();
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();        
        instanceUtil = new InstanceUtil();        
        
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");  
        
        List<OrchestrationUtil> orchObj = super.addListOfOrchestrations(orchestrationsList);
        super.startListOfOrchestrations(orchObj);
        Thread.sleep(180000);
        
        Iterator<OrchestrationUtil> it = orchObj.iterator();
        while(it.hasNext()){
            OrchestrationUtil ou = it.next();
            uuids.addAll(ou.getListOfInstanceNames());
        }
        Iterator<String> uuidIt = this.uuids.iterator();
        while (uuidIt.hasNext()){            
            Assert.assertTrue(instanceUtil.checkVolumeSanity(uuidIt.next()),"Volume check failed");
        }
    }
    
    @Test(alwaysRun=true,threadPoolSize = 5, invocationCount = 5, timeOut=129600000)
    public void IOonAttachedVolumes() throws InterruptedException {
        Thread t = Thread.currentThread();
        long threadid = t.getId();
        Iterator<String> uuidIt = this.uuids.iterator();
        while (uuidIt.hasNext()){            
            Assert.assertTrue(instanceUtil.checkVolumeMultipleDDthreads(threadid, uuidIt.next()), "dd thread failed!");
        }
        
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(this.takeover(), "Error : Storage takeover failed!");        
    }
    
    @Test(alwaysRun=true,timeOut=129600000,dependsOnMethods = { "bb_StorageServerTakeover" })
    public void cc_testPingAndVolumeSanityAfterTakeover() throws InterruptedException{   
        Iterator<String> uuidIt = this.uuids.iterator();
        while (uuidIt.hasNext()){   
            String vmName = uuidIt.next();
            Assert.assertTrue(instanceUtil.pingVM(vmName), "Error : Failed to ping VM after new instance is created!");
            Assert.assertTrue(instanceUtil.checkVolumeSanity(vmName), "Error : Failed to accessed attached volume from instance, VM : " + vmName);
        }
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
