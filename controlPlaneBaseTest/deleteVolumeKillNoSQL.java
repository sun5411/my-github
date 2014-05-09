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
public class deleteVolumeKillNoSQL extends ControlPlaneBaseTest {
    HAUtil util;
    FunctionalUtils func;
    
    @BeforeClass
    public void deleteVolume_setup() throws InterruptedException{
        super.setup();
        func = new FunctionalUtils();
        util = new HAUtil();
        Assert.assertTrue(func.createVolumes(2), "Error : Create volumes failed!");
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volumes are not online!");
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void deleteVolume() throws InterruptedException{
        Assert.assertTrue(func.deleteCreatedVolumes(), "Error : Delete volume failed!");
        Assert.assertFalse(func.areVolumesOnline(), "Error : Volumes are not online!");
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_NosqlFailure() throws InterruptedException{                     
        util.killNOSQLReplica();
    }  
    
    @AfterClass
    public void tearDown() {
        func.deleteCreatedVolumes();
        func.deleteStoragePool();
        func.deleteStorageServer();
    }
}
