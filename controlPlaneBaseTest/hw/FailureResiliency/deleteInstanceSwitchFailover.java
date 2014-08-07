/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import java.util.logging.Level;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class deleteInstanceSwitchFailover extends TakeoverBaseClass {
    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        func.launchSmallVM();
        int count = 0;
        while (!func.isVMup()){
            Thread.sleep(10000);
            count++;
            if (count >= 10){
                break;
            }
        }
        Assert.assertTrue(func.isVMup(), "VM is down");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteInstance() throws InterruptedException{
        func.deleteVM(); 
        if (func.isVMup()){            
            logger.log(Level.INFO, "VM failed to delete 1st time when rebooting the switch to which the BserviceManager's host was connected to");            
            Thread.sleep(60000);
            func.deleteVM();                
        }
        Assert.assertFalse(func.isVMup(), "VM is still up");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_switchTakeover() throws InterruptedException{
        // TODO: reboot the switch to which the BserviceManager's host is connected to
        Assert.assertTrue(super.switchFailover(), "Failed to perform switch failover");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
    }
    
}
