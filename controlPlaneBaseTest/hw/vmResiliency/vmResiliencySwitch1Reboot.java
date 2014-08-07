package com.oracle.nimbula.qa.ha.hw.vmResiliency;

import com.oracle.nimbula.qa.ha.IPpoolUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.OrchestrationUtil;
import com.oracle.nimbula.qa.ha.hw.FailureResiliency.TakeoverBaseClass;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.net.UnknownHostException;

/**
 *
 * @author Sun Ning
 */
public class vmResiliencySwitch1Reboot extends TakeoverBaseClass {
    InstanceUtil instanceUtil;
    List<OrchestrationUtil> orchObj;
    IPpoolUtil ip;    
    String ipPoolEntry;        
    List<String> orchFiles;    
    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException{
        super.setup();
        ip = new IPpoolUtil();
        ip.addIPpool();
        ipPoolEntry = ip.addIPPoolEntry();        
        instanceUtil = new InstanceUtil();
        
        orchFiles = new LinkedList<>();
        Assert.assertTrue(func.createStorageProperty(), "Error : Create Storage Property failed!");
        Assert.assertTrue(func.createStorageServer(), "Error : Add storage server failed!");
        Assert.assertTrue(func.createStoragePool(), "Error : Create storage pool failed!");  
        orchFiles.add("/tmp/vmResiliency_gatewayFailover1.json");
        orchFiles.add("/tmp/vmResiliency_gatewayFailover2.json");
        orchFiles.add("/tmp/vmResiliency_gatewayFailover3.json");
        
        orchObj = super.addListOfOrchestrations(orchFiles);
        Assert.assertTrue(0 == super.startListOfOrchestrations(orchObj), "Orchestrations could not be started");
        Thread.sleep(180000);
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void aa_testVMsSSH(){
        Assert.assertTrue(sshIntoVM(), "Failed to ssh into the VM prior to switch failover");
    }
    
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_switchFailover() throws InterruptedException{
        Assert.assertTrue(super.switchFailover(), "Failed to perform switch failover");
    }  
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void cc_testVMsSSH(){
        Assert.assertTrue(sshIntoVM(), "Failed to ssh into the VM after switch failover");
    }
    
    private boolean sshIntoVM(){
        Iterator<OrchestrationUtil> orchIt = orchObj.iterator();
        while (orchIt.hasNext()){
            OrchestrationUtil ou = orchIt.next();
            logger.log(Level.INFO, "Checking instances for orchestration : {0}", ou.getListOfInstanceNames());
            List<String> instances = ou.getListOfInstanceNames();
            Iterator<String> it = instances.iterator();
            while(it.hasNext()){
                String vm = it.next();
                if (!instanceUtil.sshVM_IP(vm)){
                    return false;
                }
            }
        }
        return true;
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        super.stopListOfOrchestrations(orchObj);
        super.deleteListOfOrchestrations(orchObj);
        ip.deleteIPPoolEntry(ipPoolEntry);
        ip.deleteIPpool();        
        Assert.assertTrue(func.deleteStoragePool(),"Error : Delete Storage pool failed!");
        Assert.assertTrue(func.deleteStorageServer(),"Error : Delete Storage server failed!");
    }
}
