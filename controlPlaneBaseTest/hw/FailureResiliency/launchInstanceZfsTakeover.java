/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.IPpoolUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.hw.FailureResiliency.TakeoverBaseClass;
import com.oracle.nimbula.test_framework.resource.types.Instance;
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
public class launchInstanceZfsTakeover extends TakeoverBaseClass {
    InstanceUtil vm;    
    IPpoolUtil ip;
    String ipPoolEntry;
    String vmUUID;
    String ipResName;

    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
        ip = new IPpoolUtil();
        
        Assert.assertTrue(ip.addIPpool(),"Failed to add ippool");
        ipPoolEntry = ip.addIPPoolEntry();
        ipResName = ip.addIPPoolReservation();
        Assert.assertNotNull(ipResName, "Unable to add ip reservation"); 
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_launchInstance() throws InterruptedException{
        Assert.assertTrue(vm.launchVMwithIPreservation(ipResName),"Error : VM create failed!");
        vmUUID = vm.getCreatedInstancesUUID().get(0);
        while(!vm.isVMup(vmUUID)){
            Thread.sleep(1000);
        }
        Assert.assertTrue(vm.pingVM(vmUUID), "Error : Failed to ping VM with private IP");
        Assert.assertTrue(vm.pingVM_publicIP(vmUUID, 10), "Error : Failed to ping VM with public IP.");
        Assert.assertTrue(vm.sshVM_IP(vmUUID), "Error : Failed to ssh VM");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_zfsTakeover() throws InterruptedException{
        Assert.assertTrue(super.takeover(), "Error : Storage takeover failed!");
    }  

    @AfterClass(alwaysRun = true)
    public void tearDown() throws InterruptedException {
        vm.deleteVM(vmUUID);
        Thread.sleep(20000);
        ip.deleteIPpoolReservation(ipResName);
        ip.deleteIPPoolEntry(ipPoolEntry);
        ip.deleteIPpool();
    }
}
