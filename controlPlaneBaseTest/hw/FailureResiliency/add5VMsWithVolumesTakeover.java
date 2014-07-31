package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.OrchestrationUtil;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.oracle.nimbula.qa.ha.IPpoolUtil;
import java.util.logging.Level;

/**
 *
 * @author Sun Ning
 */
public class add5VMsWithVolumesTakeover extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    List<OrchestrationUtil> orchObj;
    IPpoolUtil ip;
    String ipPoolEntry;
    String resName;
    List<String> orchFiles;
    
    @BeforeClass(alwaysRun = true, timeOut = 900000)
    public void add_setup() throws InterruptedException{
        super.setup();
        orchFiles = new LinkedList<>();
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();
        instanceUtil = new InstanceUtil();
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");
        orchFiles.add("/tmp/hwFailure_Launch5VMswithVolumes.json");
        orchObj = super.addListOfOrchestrations(orchFiles);
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addOrchVolumes() throws InterruptedException{
        Assert.assertTrue(0 == super.startListOfOrchestrations(orchObj), "Orchestration could not be started");
        Iterator<OrchestrationUtil> orchIt = orchObj.iterator();
        while (orchIt.hasNext()){
            OrchestrationUtil ou = orchIt.next();
            logger.log(Level.INFO, "Checking instances for orchestration : {0}", ou.getListOfInstanceNames());
            List<String> instances = ou.getListOfInstanceNames();
            Iterator<String> it = instances.iterator();
            while(it.hasNext()){
                String vm = it.next();
                Assert.assertTrue(instanceUtil.pingVM(vm), "Error : Failed to ping VM after new instance is created!");
                Assert.assertTrue(instanceUtil.checkVolumeSanity(vm), "Error : Failed to accessed attached volume from instance, VM : " + vm);
            }
        }
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Thread.sleep(20000);
        Assert.assertTrue(this.takeover(), "Error : Storage takeover failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        super.stopListOfOrchestrations(orchObj);
        super.deleteListOfOrchestrations(orchObj);
        ip.deleteIPPoolEntry(ipPoolEntry);
        ip.deleteIPpool();
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
