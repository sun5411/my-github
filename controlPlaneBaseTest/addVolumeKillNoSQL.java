package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class addVolumeKillNoSQL extends ControlPlaneBaseTest {
    HAUtil util;
    FunctionalUtils func;
    
    @BeforeClass
    public void addVolume_setup() throws InterruptedException{
        super.setup();
        util = new HAUtil();
        func = new FunctionalUtils();
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void addVolume() throws InterruptedException{
        System.out.println("###### Will create volume ...");
        func.createVolumes(2);
        System.out.println("###### Will check volumes ....");
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volume is not online");
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_NosqlFailure() throws InterruptedException{                     
        util.killNOSQLReplica();
        //Thread.sleep(60000);
    }  
    
    @AfterClass
    public void tearDown() {        
        func.deleteCreatedVolumes();
    }
}
