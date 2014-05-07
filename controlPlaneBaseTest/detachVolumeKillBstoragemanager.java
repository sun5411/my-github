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
import com.oracle.colt.result.Result;
import com.oracle.nimbula.test_framework.helpers.NimbulaHelper;
import com.oracle.nimbula.qa.ha.common.HAConstantDef;
/**
 *
 * @author Sun Ning
 */
public class detachVolumeKillBstoragemanager extends ControlPlaneBaseTest {
    HAUtil util;
    FunctionalUtils func;
    InstanceUtil vm;
    String vmUUID;
    String volumeName;
    NimbulaHelper nimhelper;
    int volNum = 20;

    @BeforeClass
    public void detachVolumes_setup() throws InterruptedException {
        super.setup();
        func = new FunctionalUtils();
        vm = new InstanceUtil();
        nimhelper = new NimbulaHelper(HAConstantDef.ROOT_USER,HAConstantDef.ROOT_PASSWORD);
        Assert.assertTrue(func.createVolumes(volNum),"Error : Volume create failed!");
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volume is not online!");
        Assert.assertTrue(vm.launchNSimpleVMs(volNum),"Error : VMs create failed!");
        Thread.sleep(120000);
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volume is not online");
        vm.launchNSimpleVMs(volNum);
        for ( int i = 0 ; i < volNum; i++ ){
            vmUUID = vm.getCreatedInstancesUUID().get(i);
            Assert.assertTrue(vm.isVMup(vmUUID),"VM is not up yet");
        }

        for ( int i = 0 ; i < volNum; i++ ){
            vmUUID = vm.getCreatedInstancesUUID().get(i);
            volumeName = func.getCreatedVolumeNames().get(i);
            Assert.assertTrue(vm.addStorageAttachment(vmUUID, volumeName), "Storage volume could not be attached to the VM");
        }
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void detachVolumes() throws InterruptedException{
        for ( int i = 0 ; i < volNum; i++ ){
            volumeName = func.getCreatedVolumeNames().get(i);
            Assert.assertTrue(vm.deleteStorageAttachment(volumeName), volumeName + " Error : Volume detach failed");
        }
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_bStoragemanagerFailure() throws InterruptedException{                     
       super.killNDService("bstoragemanager_1");               
    }  
    

    @AfterClass
    public void tearDown() {
        vm.deleteAllCreatedVMs();
        func.deleteCreatedVolumes();
        func.deleteStoragePool();
        Result res = nimhelper.deleteProperty("storage", HAConstantDef.STORAGE_PROP, true);
        if ( 0 != res.getExitValue() ){
            System.out.println("Error : Delete property failed !");
        }
        func.deleteStorageServer();
    }        
}
