/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author ctyagi
 */
public class snapshotInstanceZfsTakeover extends TakeoverBaseClass {
    @BeforeClass(alwaysRun = true, timeOut = 129600000)
    public void setup() throws InterruptedException {
        func.launchSmallVM();
        while (!func.isVMup()){
            Thread.sleep(10000);
        }
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_snapshotVM() throws InterruptedException{
        Assert.assertTrue(func.createSnapshot(),"Snapshot creation failed");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_zfsTakeover() throws InterruptedException{
        Assert.assertTrue(super.takeover(), "Error : Storage takeover failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteVM();
    }
    
}
