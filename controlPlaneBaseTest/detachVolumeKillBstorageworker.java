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
public class detachVolumeKillBstorageworker extends ControlPlaneBaseTest {
    HAUtil util;
    FunctionalUtils func;
    InstanceUtil vm;
    String vmUUID;
    String volumeName;

    @BeforeClass
    public void detachVolumes_setup() throws InterruptedException {
        super.setup();
        func = new FunctionalUtils();
        vm = new InstanceUtil();
        func.createVolumes(20);
        //Thread.sleep(30000);
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volume is not online");
        vm.launchNSimpleVMs(20);
        for ( int i = 0 ; i < 20; i++ ){
            vmUUID = vm.getCreatedInstancesUUID().get(i);
            Assert.assertTrue(vm.isVMup(vmUUID),"VM is not up yet");
        }

        for ( int i = 0 ; i < 20; i++ ){
            vmUUID = vm.getCreatedInstancesUUID().get(i);
            volumeName = func.getCreatedVolumeNames().get(i);
            Assert.assertTrue(vm.addStorageAttachment(vmUUID, volumeName), "Storage volume could not be attached to the VM");
        }
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void detachVolumes() throws InterruptedException{
        for ( int i = 0 ; i < 20; i++ ){
            volumeName = func.getCreatedVolumeNames().get(i);
            Assert.assertTrue(vm.deleteStorageAttachment(volumeName), volumeName + " Error : Volume detach failed");
        }
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_bStorageworkerFailure() throws InterruptedException{                     
       super.killNDService("bstorageworker_nfs_1");               
    }  
    

    @AfterClass
    public void tearDown() {
        vm.deleteAllCreatedVMs();
        func.deleteCreatedVolumes();
    }        
}
