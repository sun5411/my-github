/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.InstanceUtil;
import java.util.List;
import java.util.logging.Level;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class addSecIPlistBsecnodeReboot extends TakeoverBaseClass {
    String vmUUID,new_vmUUID;
   
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
    }

    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addSecIPlist() throws InterruptedException{     
        func.addSecIPList();
        if (!func.secIPListExists()){
            logger.log(Level.INFO, "SecIPlist not added when bSecNode was killed");
            Thread.sleep(30000);
            Assert.assertTrue(func.addSecIPList(),"Add secIPList failed");
        }        
        Assert.assertTrue(func.secIPListExists(),"SecIPList does not exist");        
    }  
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_bsecNodeReboot() throws InterruptedException{
        List<String> nodeOfService = this.util.getNodesOfService("bsecsite");
        super.rebootNodeUUID(nodeOfService.get(0));
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteSecIPList();
    }
}
