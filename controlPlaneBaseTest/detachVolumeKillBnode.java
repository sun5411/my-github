/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
/**
 *
 * @author Sun Ning
 */
public class detachVolumeKillBnode extends ControlPlaneBaseTest {
    HAUtil util;
    FunctionalUtils func;
    InstanceUtil vm;
    String vmUUID;
    String volumeName;
    String hostingNodeUUID;
    String hostingNodeIP;

    @BeforeClass
    public void detachVolumes_setup() throws InterruptedException {
        super.setup();
        func = new FunctionalUtils();
        vm = new InstanceUtil();
        util = new HAUtil();
        Assert.assertTrue(func.createVolume(),"Error : Volume create failed!");
        Assert.assertTrue(func.isVolumeOnline(), "Error : Volume is not online!");
        Assert.assertTrue(vm.launchSimple(),"Error : VM create failed!");
        volumeName = func.getCreatedVolumeNames().get(0);
        vmUUID = vm.getCreatedInstancesUUID().get(0);
        while(!vm.isVMup(vmUUID)){
            Thread.sleep(3000);
        }
        hostingNodeUUID = util.getVMnode(vmUUID);                                                                                                                                                  
        hostingNodeIP = util.getNodeIP(hostingNodeUUID);
        Assert.assertTrue(vm.addStorageAttachment(vmUUID, volumeName), "Storage volume could not be attached to the VM !");
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void detachVolumes() throws InterruptedException{
        vm.deleteVM(vmUUID);
        Thread.sleep(5000);
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_BnodeFailure() throws InterruptedException{
       System.out.println(hostingNodeIP + " service bnode will be killed ! ######");
       super.killNDServiceOnNode("bnode", hostingNodeIP);               
    }  
    

    @AfterClass
    public void tearDown() {
        func.deleteVolume();
        func.deleteStoragePool();
        func.deleteStorageServer();
    }        
}
