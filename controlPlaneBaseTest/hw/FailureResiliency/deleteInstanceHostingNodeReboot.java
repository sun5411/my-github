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
public class deleteInstanceHostingNodeReboot extends TakeoverBaseClass {

    String vmUUID, new_vmUUID;
    InstanceUtil vm;

    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
        super.addStartOrchestration("/tmp/SingleVM_HA.json");
        vmUUID = super.orchestrationInstances().get(0);
    }

    @Test(alwaysRun = true, timeOut = 129600000)
    public void aa_deleteVM() throws InterruptedException {
        Assert.assertTrue(vm.deleteVM(vmUUID), "Error : VM delete failed");
    }

    @Test(alwaysRun = true, timeOut = 129600000)
    public void bb_hostingNodeReboot() throws InterruptedException {
        super.rebootNodeHostingVM(util.getVMnode(vmUUID));
        new_vmUUID = super.orchestrationInstances().get(0);
        Assert.assertFalse(vmUUID.equals(new_vmUUID), "Error : After deleted, VM should have different UUID.");
        Assert.assertTrue(vm.pingVM(new_vmUUID), "Error : Failed to ping new VM.");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        super.stopDeleteOrchestration();
    }

}
