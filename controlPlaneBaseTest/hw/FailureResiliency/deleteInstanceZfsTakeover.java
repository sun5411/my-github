/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import java.util.logging.Level;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author sning
 */
public class deleteInstanceZfsTakeover extends TakeoverBaseClass {
    InstanceUtil vm;
    
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        super.setup();
        vm = new InstanceUtil();
        func.launchSmallVM();
        int count = 0;
        while (!func.isVMup()){
            Thread.sleep(10000);   
            count++;
            if (count >= 10){
                break;
            }
        }
        Assert.assertTrue(func.isVMup(),"VM is down");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteInstance() throws InterruptedException{
        func.deleteVM(); 
        if (func.isVMup()){
            logger.log(Level.INFO, "Error : VM failed to be deleted!");            
            func.deleteVM();    
            Thread.sleep(10000);
        }
        Assert.assertFalse(func.isVMup(),"Error : VM have not been deleted!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_zfsTakeover() throws InterruptedException{
        Assert.assertTrue(super.takeover(), "Error : Storage takeover failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
    }
    
}
