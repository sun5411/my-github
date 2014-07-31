/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.IPpoolUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
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
public class attachVolumeKillBstoragemanager extends ControlPlaneBaseTest {
    InstanceUtil vm;    
    IPpoolUtil ip;
    String ipPoolEntry;
    String vmUUID;
    String volumeName;
    int volNum = 20;
    List<String> uuids = new LinkedList<>();
    List<String> volumeNames = new LinkedList<>();
    List<String> ipReservations;

    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
        
        ip = new IPpoolUtil();        
        //Create IP Pool for VM instances' IP-reservations
        Assert.assertTrue(ip.addIPpool(),"Failed to add ippool");
        ipPoolEntry = ip.addIPPoolEntry();
        ipReservations = new LinkedList<>();
        for ( int i = 0 ; i < volNum; i++){
            String ipResName = ip.addIPPoolReservation();
            Assert.assertNotNull(ipResName, "Unable to add ip reservation");
            ipReservations.add(ipResName);
        }
        
        Assert.assertTrue(func.createVolumes(volNum),"Error : Volume create failed!");
        while (!func.areVolumesOnline()){
            logger.severe("Volumes are not online yet");
            Thread.sleep(30000);
        }
        Assert.assertTrue(vm.launchNSimpleVMsWithIPreservations(volNum,ipReservations),"Error : VMs create failed!");
        uuids = vm.getCreatedInstancesUUID();
        volumeNames = func.getCreatedVolumeNames();
        for ( int i = 0 ; i < volNum; i++ ){
            vmUUID = uuids.get(i);
            while(!vm.isVMup(vmUUID)){
                Thread.sleep(3000);
            }
        }
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_attachVolumes() throws InterruptedException{
        for ( int i = 0 ; i < volNum; i++ ){
            vmUUID = uuids.get(i);
            volumeName = volumeNames.get(i);
            Assert.assertTrue(vm.addStorageAttachment(vmUUID, volumeName), "Storage volume could not be attached to the VM");
        }              
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void bb_attachVolumesAndValidateStorage() throws InterruptedException{
        for ( int i = 0 ; i < volNum; i++ ){
            vmUUID = uuids.get(i);
            volumeName = volumeNames.get(i);
            Assert.assertTrue(vm.addStorageAttachment(vmUUID, volumeName), "Storage volume could not be attached to the VM");
        }       
        int count = 0;
        while(!vm.isStorageAttached(vm.getStorageAttachmentName(vmUUID).get(0))){
            logger.severe("Storage is not attached yet");
            Thread.sleep(30000);
            count = count + 1;
            if ( count > 15 ){ break;}
        }
        for ( int i = 0 ; i < volNum; i++ ){
            Assert.assertTrue(vm.isStorageAttached(vm.getStorageAttachmentName(vmUUID).get(0)), "Storage volume's state is not \"attached\"");
            Assert.assertTrue(vm.checkVolumeSanity(vmUUID),"Failed to check volume sanity");
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
        vm.deleteAllCreatedVMs();
        Iterator<String> it = this.uuids.iterator();
        while(it.hasNext()){
            String uud = it.next();
            Instance ins = vm.getVM(uud);
            if ( null != ins ){
                while(ins.getState().equalsIgnoreCase("stopping")){
                    Thread.sleep(30000);
                }
            }
        }        
        it = this.ipReservations.iterator();
        while(it.hasNext()){
            String ipRes = it.next();
            this.ip.deleteIPpoolReservation(ipRes);
        }            
        func.deleteCreatedVolumes();
        Thread.sleep(30000);
        func.deleteStoragePool();
        Thread.sleep(30000);
        func.deleteStorageServer();
    }        
}
