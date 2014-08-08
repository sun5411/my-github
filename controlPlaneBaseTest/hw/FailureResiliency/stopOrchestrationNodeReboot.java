/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.InstanceUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class stopOrchestrationNodeReboot extends TakeoverBaseClass {
    String vmUUID;
    InstanceUtil vm;
   
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
        super.addStartOrchestration("/tmp/SingleVM_HA.json");
        vmUUID=super.orchestrationInstances().get(0);
        Assert.assertTrue(vm.pingVM(vmUUID), "Error : Failed to ping VM");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_startOrchestration() throws InterruptedException{
        super.stopDeleteOrchestration();
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_NodeReboot() throws InterruptedException{
        super.rebootNodeHostingVM(util.getVMnode(vmUUID));
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
    }
}
