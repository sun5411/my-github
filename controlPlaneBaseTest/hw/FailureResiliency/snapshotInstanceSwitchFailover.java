/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author sning
 */
public class snapshotInstanceSwitchFailover extends TakeoverBaseClass {
    
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
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_snapshotVM() throws InterruptedException{
        Assert.assertTrue(func.createSnapshot(),"Snapshot creation failed");
        Assert.assertTrue(func.isVMup(),"VM is up");
        Assert.assertTrue(func.lastCreatedSnapshotExists(),"Error : Snapshot doesn't existed, snapshot failed!");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_switchFailover() throws InterruptedException{
        // TODO: reboot the switch to which the BserviceManager's host is connected to
        Assert.assertTrue(super.takeover(), "Error : Storage takeover failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteVM();
    }
    
}
