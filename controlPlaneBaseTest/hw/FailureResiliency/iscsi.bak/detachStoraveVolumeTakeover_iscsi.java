/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import com.oracle.nimbula.test_framework.resource.types.StorageAttachment;
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
public class detachVolumeKillBstoragemanager extends ControlPlaneBaseTest {

    InstanceUtil instanceUtil;
    String vmUUID;
    String volumeName;
    int volNum = 30;
    List<String> uuids = new LinkedList<>();
    List<String> volumeNames = new LinkedList<>();

    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        instanceUtil = new InstanceUtil();
        Assert.assertTrue(func.createVolumes(volNum), "Error : Volume create failed!");
        while (!func.areVolumesOnline()){
            logger.severe("Volumes are not online yet");
            Thread.sleep(30000);
        }
        Assert.assertTrue(instanceUtil.launchNSimpleVMs(volNum), "Error : VMs create failed!");
        uuids = instanceUtil.getCreatedInstancesUUID();
        volumeNames = func.getCreatedVolumeNames();
        for (int i = 0; i < volNum; i++) {
            vmUUID = uuids.get(i);
            while (!instanceUtil.isVMup(vmUUID)) {
                Thread.sleep(3000);
            }
        }

        for (int i = 0; i < volNum; i++) {
            vmUUID = uuids.get(i);
            volumeName = volumeNames.get(i);
            Assert.assertTrue(instanceUtil.addStorageAttachment(vmUUID, volumeName), "Storage volume could not be attached to the VM !");
            while (!instanceUtil.isStorageAttached(instanceUtil.getStorageAttachmentName(vmUUID).get(0))){
                logger.severe("Storage volume is not attached yet. Sleeping 3 seconds...");
                Thread.sleep(3000);
            }
        }       
    }

    @Test(alwaysRun = true, timeOut = 129600000)
    public void aa_detachVolumes() throws InterruptedException {        
        for (int i = 0; i < volNum; i++) {
            String stName = instanceUtil.getStorageAttachment(uuids.get(i)).get(0).getStorageAttachmentName();  
            Assert.assertNotNull(stName, "Storage attachment is null");
            Assert.assertTrue(instanceUtil.deleteStorageAttachment(stName), "Error : Storage dettachment failed !");
        }        
    }
    
    @Test(alwaysRun = true, timeOut = 129600000)
    public void bb_detachVolumesAndValidate() throws InterruptedException {        
        for (int i = 0; i < volNum; i++) {
            String stName = instanceUtil.getStorageAttachment(uuids.get(i)).get(0).getStorageAttachmentName();  
            Assert.assertNotNull(stName, "Storage attachment is null");
            Assert.assertTrue(instanceUtil.deleteStorageAttachment(stName), "Error : Storage dettachment failed !");
        }
        for ( int i = 0 ; i < volNum; i++ ){
            Assert.assertTrue(instanceUtil.isStorageDetached(instanceUtil.getStorageAttachmentName(vmUUID).get(0)), "Storage volume's state is not \"detached\"");
            Assert.assertFalse(instanceUtil.checkVolumeSanity(vmUUID),"Attached volume is usable");
        }
    }

    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_bStoragemanagerFailure() throws InterruptedException{                  
        super.killNDService("bstoragemanager_1");                
    } 
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void cc_bStoragemanagerFailure() throws InterruptedException{          
        Thread.sleep(1000);        
        super.killNDService("bstoragemanager_2");                
    } 
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void dd_bStoragemanagerFailure() throws InterruptedException{          
        Thread.sleep(2000);
        super.killNDService("bstoragemanager_3");
    } 
    
    @AfterClass(alwaysRun = true)
    public void tearDown() throws InterruptedException {
        instanceUtil.deleteAllCreatedVMs();
        Iterator<String> it = this.uuids.iterator();
        while(it.hasNext()){
            String uud = it.next();
            while(instanceUtil.getVM(uud).getState().equalsIgnoreCase("stopping")){
                Thread.sleep(30000);
            }
        }
        func.deleteCreatedVolumes();
        Thread.sleep(30000);
        func.deleteStoragePool();
        Thread.sleep(30000);
        func.deleteStorageServer();
    }
}
