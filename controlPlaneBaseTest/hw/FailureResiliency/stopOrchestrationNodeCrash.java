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
public class stopOrchestrationNodeCrash extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    List<OrchestrationUtil> orchObj;
    String vmUUID,new_vmUUID;
    List<String> orchFiles = new LinkedList<>();
    
    @BeforeClass(alwaysRun = true, timeOut = 900000)
    public void setup() throws InterruptedException {
        super.setup();
        instanceUtil = new InstanceUtil();
        
        orchFiles.add("/tmp/SingleVM_HA.json");
   
        orchObj = super.addListOfOrchestrations(orchFiles);        
        Assert.assertNotNull(orchObj, "Failed to add orchestrations");
        
        super.startListOfOrchestrations(orchObj);
        vmUUID=super.orchestrationInstances().get(0);
        Assert.assertTrue(instanceUtil.pingVM(vmUUID), "Error : Failed to ping VM after new instance is created!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_stopOrchestration() throws InterruptedException{
        super.stopListOfOrchestrations(orchObj);
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_nodeReboot() throws InterruptedException{
        super.killNodeUUID(util.getVMnode(vmUUID));
        new_vmUUID=super.orchestrationInstances().get(0);
        Assert.assertFalse(vmUUID.equals(new_vmUUID), "Error : After deleted, VM should have different UUID.");
        Assert.assertTrue(instanceUtil.pingVM(new_vmUUID), "Error : Failed to ping VM after new instance is created!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        super.deleteListOfOrchestrations(orchObj);
    }
}
