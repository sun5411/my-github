package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.OrchestrationUtil;
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
public class deleteOrchestrationNodeReboot extends TakeoverBaseClass {

    InstanceUtil instanceUtil;
    List<OrchestrationUtil> orchObj;
    String vmUUID;
    List<String> orchFiles = new LinkedList<>();

    @BeforeClass(alwaysRun = true, timeOut = 900000)
    public void setup() throws InterruptedException {
        super.setup();
        instanceUtil = new InstanceUtil();

        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");

        orchFiles.add("/tmp/SingleVM_HA.json");

        orchObj = super.addListOfOrchestrations(orchFiles);
        Assert.assertNotNull(orchObj, "Failed to add orchestrations");
        Assert.assertTrue(0 == super.startListOfOrchestrations(orchObj), "Orchestration could not be started");

        super.startListOfOrchestrations(orchObj);
        vmUUID = super.orchestrationInstances().get(0);
        Assert.assertTrue(instanceUtil.pingVM(vmUUID), "Error : Failed to ping VM after new instance is created!");
        super.stopListOfOrchestrations(orchObj);
    }

    @Test(alwaysRun = true, timeOut = 129600000)
    public void aa_deleteOrchestration() throws InterruptedException {
        super.deleteListOfOrchestrations(orchObj);
    }

    @Test(alwaysRun = true, timeOut = 129600000)
    public void bb_nodeReboot() throws InterruptedException {
        super.rebootNodeHostingVM(util.getVMnode(vmUUID));
    }
    
    @Test(alwaysRun = true, timeOut = 129600000, dependsOnMethods={"aa_deleteOrchestration","bb_nodeReboot"})
    public void cc_sanityTest() throws InterruptedException {
        vmUUID = super.orchestrationInstances().get(0);
        Assert.assertTrue(instanceUtil.pingVM(vmUUID), "Error : Failed to ping VM after new instance is created!");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        super.deleteListOfOrchestrations(orchObj);
    }
}
