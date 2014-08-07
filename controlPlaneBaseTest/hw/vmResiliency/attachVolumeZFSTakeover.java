/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.vmResiliency;

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
public class attachVolumeZFSTakeover extends TakeoverBaseClass {
    InstanceUtil vm;    
    IPpoolUtil ip;
    String ipPoolEntry;
    String vmUUID;
    String volumeName;
    int volNum = 30;
    List<String> uuids = new LinkedList<>();
    List<String> volumeNames = new LinkedList<>();
    List<String> ipReservations = new LinkedList<>();

    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
        ip = new IPpoolUtil();
        
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
                Thread.sleep(1000);
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
        for ( int i = 0; i < volNum; i++){
            int count = 0;
            vmUUID = uuids.get(i);
            while(!vm.isStorageAttached(vm.getStorageAttachmentName(vmUUID).get(0))){
                logger.severe("Storage is not attached yet");
                Thread.sleep(10000);
                count = count + 1;
                if ( count > 15 ){ break;}
            }                     
        }
        for (int i = 0 ; i < volNum ; i++){
            vmUUID = uuids.get(i);
            Assert.assertTrue(vm.pingVM(vmUUID), "Error : Failed to ping VM");
            Assert.assertTrue(vm.checkVolumeSanity(vmUUID), "Error : Failed to accessed attached volume from instance, VM : " + vmUUID);
        }
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_StorageServerTakeover() throws InterruptedException{
        Assert.assertTrue(super.takeover(), "Error : Storage takeover failed!");
        Thread.sleep(30000);
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
                    Thread.sleep(10000);
                    ins = vm.getVM(uud);
                    if(null == ins){
                        break;
                    }
                }
            }
        }        
        it = this.ipReservations.iterator();
        while(it.hasNext()){
            String ipRes = it.next();
            this.ip.deleteIPpoolReservation(ipRes);
        }            
        func.deleteCreatedVolumes();
        Thread.sleep(10000);
        func.deleteStoragePool();
        func.deleteStorageServer();
    }        
}
