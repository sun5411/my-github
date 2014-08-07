/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class startOrchestrationNodeReboot extends TakeoverBaseClass {
    String vmUUID,new_vmUUID;
    InstanceUtil vm;
   
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_startOrchestration() throws InterruptedException{
        super.addStartOrchestration("/tmp/SingleVM_HA.json");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_NodeReboot() throws InterruptedException{
        Thread.sleep(20000);
        vmUUID=super.orchestrationInstances().get(0);
        super.rebootNodeHostingVM(util.getVMnode(vmUUID));
        new_vmUUID=super.orchestrationInstances().get(0);
        Assert.assertFalse(vmUUID.equals(new_vmUUID), "Error : After deleted, VM should have different UUID.");
        Assert.assertTrue(vm.pingVM(new_vmUUID), "Error : Failed to ping VM");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        super.stopDeleteOrchestration();
    }
    
}
