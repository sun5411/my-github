/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.vmResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.IPpoolUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.NAT_Util;
import com.oracle.nimbula.qa.ha.SeclistUtil;
import com.oracle.nimbula.qa.ha.system.healthcheck.ContentDiff;
import com.oracle.nimbula.qa.ha.system.healthcheck.RabbitMQHealthCheck;
import com.oracle.nimbula.qa.ha.system.healthcheck.ZookeeperHealthCheck;
import com.oracle.nimbula.test_framework.resource.types.SecList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;

/**
 *
 * @author ctyagi
 */
public class natNodeFailure {
    InstanceUtil vm;
    NAT_Util nat;
    
    //@Test(alwaysRun = true, timeOut = 129600000)
    public void natUnitTest(){
        nat = new NAT_Util();
        
        String natNode = nat.getNonNatNodeUUID();
        nat.addNATtoNode(natNode);
        
        natNode = nat.getNatNodeUUID();
        nat.removeNATfromNode(natNode);
    }
    
    //@Test(alwaysRun = true, timeOut = 129600000)
    public void seclistUnitTest(){
        SeclistUtil sec = new SeclistUtil();
        sec.addSeclist(1);        
        List<SecList> ls = sec.listSeclist();
        System.out.println(" >> " + ls.get(0).getName());
        
        List<String> names = sec.getCreatedSeclists();
        System.out.println(" >> " + names.get(0));
        
        sec.getSeclist(names.get(0));
        System.out.println(" >> " + sec.getSeclist(names.get(0)));
        
        System.out.println(" >> " + sec.seclistExists(names.get(0)));        
        System.out.println(" >> " + sec.seclistExists("blabla"));
        
        System.out.println(" >> " + sec.deleteSeclist(names.get(0)));
        System.out.println(" >> " + sec.deleteSeclist(names.get(0)));
    }
    
    //@Test(alwaysRun = true, timeOut = 129600000)
    public void storagePart() throws InterruptedException{
        FunctionalUtils func = new FunctionalUtils();
        func.deleteVolume();
        func.deleteStoragePool();
        func.deleteStorageServer();
        
        func.createVolume();
        
        func.createVolumes(3);
        
        func.deleteAllSystemVolumes();
    }
    
    //@Test(alwaysRun = true, timeOut = 129600000)
    public void reservationPart() throws InterruptedException{
        IPpoolUtil ip = new IPpoolUtil();
        
        ip.addIPpool();
        System.out.println ("1>> IP pool exists: " + ip.ipPoolExists());
        String id = ip.addIPPoolEntry();
        System.out.println ("1>> IP pool entry exists: " + ip.ipPoolEntryExists(id));
        String resName = ip.addIPPoolReservation();
        
        System.out.println ("Is res used: " + ip.isIPReservationUsed(resName));
        
        ip.deleteIPpoolReservation(resName);
        ip.deleteIPPoolEntry(id);        
        System.out.println ("2>> IP pool entry exists: " + ip.ipPoolEntryExists(id));
        ip.deleteIPpool();
        System.out.println ("2>> IP pool exists: " + ip.ipPoolExists());        
    }
    
    //@Test(alwaysRun = true, timeOut = 129600000)
    public void natNodeFailure() throws InterruptedException{
        FunctionalUtils func = new FunctionalUtils();
        vm = new InstanceUtil();
        
        //vm.launchNSimpleVMs(1);
        //vm.launchSimple();
        //vm.findAllSystemVMs();
        
        //List<String> vms = vm.getCreatedInstancesUUID();
        
        //vm.findAllSystemVMs();
        //List<String> vms = vm.getSystemInstancesUUID();
        
        //vm.pingVM(vms.get(0),vms.get(1));
        //vm.sshVM_IP(vms.get(0));
        //vm.sshVM_DNS(vms.get(0));
        //vm.getSeclist(vms.get(0));
        //vm.getDNSname(vms.get(0));
        //func.createVolume();
        //while (!func.isVolumeOnline()){
        //    Thread.sleep(600);
        //}
        //vm.launchVMwithStorage();
        List<String> uuid = vm.getVMsWithStorageAttachment();
        vm.getStorageAttachmentNames(uuid.get(0));
        vm.getStorageAttachment(uuid.get(0));
        
        //vm.deleteAllVMs();
        
    }
    
    //@Test(alwaysRun = true, timeOut = 129600000)
    public void zookeeperPart(){
        ZookeeperHealthCheck zoo = new ZookeeperHealthCheck();
        zoo.populateZookeeperContentPriorFailure();
        
        // before failure test code
        Map<String,String> primSec = zoo.getPrimarySecondaryNATfwPriorFailure();
        Iterator<String> it = primSec.keySet().iterator();
        while (it.hasNext()){
            String elem = it.next();
            if (elem.equals("primary")){
                System.out.println("Primary NAT (prior to failure)= " + primSec.get(elem));
            } else if (elem.equals("secondary")){
                System.out.println("Secondary NAT (prior to failure)= " + primSec.get(elem));
            }             
        }
        
        zoo.populateZookeeperContentAfterFailure();
        // after failure test code
        primSec = zoo.getPrimarySecondaryNATfwAfterFailure();
        it = primSec.keySet().iterator();
        while (it.hasNext()){
            String elem = it.next();
            if (elem.equals("primary")){
                System.out.println("Primary NAT (after failure)= " + primSec.get(elem));
            } else if (elem.equals("secondary")){
                System.out.println("Secondary NAT (after failure)= " + primSec.get(elem));
            }             
        }
        
        ContentDiff cnt = zoo.analyzeNodesDataAfterFailure();
        System.out.println("\n>> " + cnt.getContentChnage() + " <<< \n");
    }
    
    @Test(alwaysRun = true, timeOut = 129600000)
    public void rabbitMQpart(){
        
        RabbitMQHealthCheck rabbit = new RabbitMQHealthCheck();
        
        rabbit.populateRabbitMQ_NDMS_ContentPriorFailure();
        rabbit.populateRabbitMQ_STATUS_ContentPriorFailure();
        
        // do some failure
        
        rabbit.populateRabbitMQ_NDMS_ContentAfterFailure();
        rabbit.populateRabbitMQ_STATUS_ContentAfterFailure();
        
        // compare the data
        ContentDiff cnt = rabbit.analyzeRabbitMQ_NDMS_DataAfterFailure();
        System.out.println("\n>>NDMS data:\n\n " + cnt.getContentChnage() + " <<< \n");
        
        cnt = rabbit.analyzeRabbitMQ_STATUS_DataAfterFailure();
        System.out.println("\n>>STATUS data:\n\n " + cnt.getContentChnage() + " <<< \n");
    }
}
