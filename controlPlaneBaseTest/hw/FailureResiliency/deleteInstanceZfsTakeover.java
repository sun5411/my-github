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
 * @author ctyagi
 */
public class deleteInstanceZfsTakeover extends TakeoverBaseClass {
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        func.launchSmallVM();
        while (!func.isVMup()){
            Thread.sleep(10000);
        }
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteInstance() throws InterruptedException{
        func.deleteVM(); 
        if (func.isVMup()){
            logger.log(Level.INFO, "VM failed to delete when bImage was killed");            
            func.deleteVM();    
            Thread.sleep(30000);
        }
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_zfsTakeover() throws InterruptedException{
        Assert.assertTrue(super.takeover(), "Error : Storage takeover failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Assert.assertFalse(func.isVMup(),"VM was deleted");
    }
    
}
