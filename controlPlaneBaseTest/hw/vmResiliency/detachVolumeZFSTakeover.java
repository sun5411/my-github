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
public class detachVolumeZFSTakeover extends TakeoverBaseClass {
    private InstanceUtil vm;    
    private IPpoolUtil ip;
    private String ipPoolEntry;    
    private final int volNum = 30;
    private List<String> uuids;
    private List<String> volumeNames;
    private List<String> ipReservations;
    private List<String> storageAttachments;
       
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
        ip = new IPpoolUtil();
        ipReservations = new LinkedList<>();
        uuids = new LinkedList<>();
        volumeNames = new LinkedList<>();
        storageAttachments = new LinkedList<>();
        
        Assert.assertTrue(ip.addIPpool(),"Failed to add ippool");
        ipPoolEntry = ip.addIPPoolEntry();        
        for ( int i = 0 ; i < volNum; i++){
            String ipResName = ip.addIPPoolReservation();
            Assert.assertNotNull(ipResName, "Unable to add ip reservation");
            ipReservations.add(ipResName);
        }
        
        Assert.assertTrue(func.createVolumes(volNum),"Error : Volume create failed!");
        while (!func.areVolumesOnline()){
            logger.severe("Volumes are not online yet");
            Thread.sleep(10000);
        }
        Assert.assertTrue(vm.launchNSimpleVMsWithIPreservations(volNum,ipReservations),"Error : VMs create failed!");
        uuids = vm.getCreatedInstancesUUID();
        volumeNames = func.getCreatedVolumeNames();
        for ( int i = 0 ; i < volNum; i++ ){            
            while(!vm.isVMup(uuids.get(i))){
                Thread.sleep(10000);
            }
        }
        
        for ( int i = 0 ; i < volNum; i++ ){            
            Assert.assertTrue(vm.addStorageAttachment(uuids.get(i), volumeNames.get(i)), "Storage volume could not be attached to the VM");
        }
        for ( int i = 0; i < volNum; i++){
            int count = 0;            
            String storageAtt = vm.getStorageAttachmentName(uuids.get(i)).get(0);
            while(!vm.isStorageAttached(storageAtt)){
                logger.severe("Storage is not attached yet");
                Thread.sleep(30000);
                count = count + 1;
                if ( count > 15 ) {
                    break;
                }
            }            
            this.storageAttachments.add(storageAtt);
        }
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_detachVolumes() throws InterruptedException{
        Iterator<String> it = this.storageAttachments.iterator();
        while(it.hasNext()){
            Assert.assertTrue(vm.deleteStorageAttachment(it.next()), "Error : Storage dettachment failed !");
        }        
        for ( int i = 0; i < volNum; i++){            
            Assert.assertTrue(vm.pingVM(uuids.get(i)), "Error : Failed to ping VM");
            Assert.assertTrue(vm.checkVolumeSanity(uuids.get(i)), "Error : Failed to accessed attached volume from instance, VM : " + uuids.get(i));
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
