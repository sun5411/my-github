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
public class addOrchestrationStorageVolume extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    List<OrchestrationUtil> orchObj;
    IPpoolUtil ip;
    String ipPoolEntry;
    String resName;
    
    @BeforeClass
    public void addStServerPool_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();
        resName = ip.addIPPoolReservation();
        instanceUtil = new InstanceUtil();
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addOrchVolumes() throws InterruptedException{
        List<String> orchFiles = new LinkedList<>();
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM1.json");
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM2.json");
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM3.json");
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM4.json");
        orchFiles.add("/tmp/hwFailure_OplanVolumeAttachVM5.json");
        
        orchObj = super.addStartListOfOrchestrations(orchFiles);
        
        Iterator<OrchestrationUtil> orchIt = orchObj.iterator();
        while (orchIt.hasNext()){
            OrchestrationUtil ou = orchIt.next();
            logger.log(Level.INFO, "Checking instances for orchestration : {0}", ou.getListOfInstanceNames());
            List<String> instances = ou.getListOfInstanceNames();
            Iterator<String> it = instances.iterator();
            while(it.hasNext()){
                String vm = it.next();
                System.out.println("######## pingVM && checkVolumeSanity, VM :" + vm);
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
        super.stopDeleteListOfOrchestrations(orchObj);
        ip.deleteIPPoolEntry(ipPoolEntry);
        ip.deleteIPpool();
        ip.deleteIPpoolReservation(resName);
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
