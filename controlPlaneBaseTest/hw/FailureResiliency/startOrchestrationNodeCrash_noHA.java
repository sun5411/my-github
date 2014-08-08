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
public class startOrchestrationNodeCrash_noHA extends TakeoverBaseClass {
    String vmUUID,new_vmUUID;
    InstanceUtil vm;
   
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_startOrchestration() throws InterruptedException{
        super.addStartOrchestration("/tmp/Simple_OPlan.json");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_NodeCrash() throws InterruptedException{
        Thread.sleep(20000);
        vmUUID=super.orchestrationInstances().get(0);
        super.killNodeUUID(util.getVMnode(vmUUID));
        Assert.assertFalse(vm.pingVM(new_vmUUID), "Error : should can not ping the VM!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        super.stopDeleteOrchestration();
    }
    
}
