/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Sun Ning
 */
public class detachVolumeKillRabbitMQ extends ControlPlaneBaseTest {
    FunctionalUtils func;
    InstanceUtil vm;
    String vmUUID;
    String volumeName;
    int volNum = 20;
    List <String> uuids = new LinkedList<>();
    List<String> volumeNames = new LinkedList<>();

    @BeforeClass
    public void detachVolumes_setup() throws InterruptedException {
        super.setup();
        func = new FunctionalUtils();
        vm = new InstanceUtil();
        Assert.assertTrue(func.createVolumes(volNum),"Error : Volume create failed!");
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volumes are not online!");
        Assert.assertTrue(vm.launchNSimpleVMs(volNum),"Error : VMs create failed!");
        uuids = vm.getCreatedInstancesUUID();
        volumeNames = func.getCreatedVolumeNames();
        for ( int i = 0 ; i < volNum; i++ ){
            vmUUID = uuids.get(i);
            while(!vm.isVMup(vmUUID)){
                Thread.sleep(3000);
            }
        }

        for ( int i = 0 ; i < volNum; i++ ){
            vmUUID = uuids.get(i);
            volumeName = volumeNames.get(i);
            Assert.assertTrue(vm.addStorageAttachment(vmUUID, volumeName), "Storage volume could not be attached to the VM !");
        }
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void detachVolumes() throws InterruptedException{
        for ( int i = 0 ; i < volNum; i++ ){
            //volumeName = volumeNames.get(i);
            //Assert.assertTrue(vm.deleteStorageAttachment(volumeName), volumeName + " Error : Volume detach failed");
            vm.deleteAllCreatedVMs();
            Thread.sleep(20000);
        }
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_RabbitMQFailure() throws InterruptedException{
       super.killAllNDServices("rabbitmq_1");
    }  
    

    @AfterClass
    public void tearDown() throws InterruptedException {
        func.deleteCreatedVolumes();
        func.deleteStoragePool();
        func.deleteStorageServer();
    }        
}
