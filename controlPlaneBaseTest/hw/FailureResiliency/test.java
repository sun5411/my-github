/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author ctyagi
 */
public class test extends TakeoverBaseClass{
    
    @BeforeClass(alwaysRun = true, timeOut = 900000)
    public void setup() throws InterruptedException{
        super.setup();
    }
    
    @Test(alwaysRun = true, timeOut = 900000)
    public void testTakeover() throws InterruptedException{
        super.takeover();
        super.takeover();
    }
}
