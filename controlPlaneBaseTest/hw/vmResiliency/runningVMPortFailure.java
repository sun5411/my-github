package com.oracle.nimbula.qa.ha.hw.vmResiliency;

import com.oracle.nimbula.qa.ha.IPpoolUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.hw.FailureResiliency.TakeoverBaseClass;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class runningVMPortFailure extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    IPpoolUtil ip;
    String ipPoolEntry;
    String vm1,vm2;
    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException{
        super.setup();
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();        
        instanceUtil = new InstanceUtil();
       
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!"); 
        super.addStartOrchestration("/tmp/vmResiliency_2VMsPortFailover.json");
        
        List<String> instances = super.orch.getListOfInstanceNames();
        vm1 = instances.get(0);
        vm2 = instances.get(1);
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void aa_pingVM_1_publicIP() throws InterruptedException{
        Assert.assertTrue(this.instanceUtil.pingVM_publicIP(vm1,100), "Ping VM1 public IP 100 times failed");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_pingVM_2_publicIP() throws InterruptedException{
        Assert.assertTrue(this.instanceUtil.pingVM_publicIP(vm2,100), "Ping VM2 public IP 100 times failed");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void cc_pingFromVMtoVM() throws InterruptedException{
        Assert.assertTrue(this.instanceUtil.pingVM_publicIP(vm1, vm2, 100), "Ping VM1 to VM2 100 times failed");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void dd_vm1HostBondPortFailover() throws InterruptedException{        
        String hostingNode = this.util.getVMnode(vm1);
        Assert.assertTrue(this.util.bondPortFailover(hostingNode), "Failed to perform bond port failover for vm1's hosting node");
    } 
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void ee_vm1HostBondPortFailover() throws InterruptedException{      
        String hostingNode = this.util.getVMnode(vm2);
        Assert.assertTrue(this.util.bondPortFailover(hostingNode), "Failed to perform bond port failover for vm2's hosting node");
    } 
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {        
        super.stopDeleteOrchestration();
        ip.deleteIPPoolEntry(ipPoolEntry);
        ip.deleteIPpool();
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storagespool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storageserver failed!");
    }
}
