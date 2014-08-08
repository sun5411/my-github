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
public class deleteSeclistBsecnodeReboot extends TakeoverBaseClass {
    String vmUUID,new_vmUUID;
    InstanceUtil vm;
   
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
        Assert.assertTrue(func.addSeclist(),"Add secList failed");
    }

    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_updateSeclist() throws InterruptedException{      
        Assert.assertTrue(func.deleteSeclist(),"Add secList failed");
    }  
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_bsecNodeReboot() throws InterruptedException{
        List<String> nodeOfService = this.util.getNodesOfService("bsecsite");
        super.rebootNodeUUID(nodeOfService.get(0));
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
    }
}
