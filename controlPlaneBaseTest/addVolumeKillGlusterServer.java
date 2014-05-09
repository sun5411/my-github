package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class addVolumeKillGlusterServer extends ControlPlaneBaseTest {
    FunctionalUtils func;
    
    @BeforeClass
    public void addVolume_setup() throws InterruptedException{
        super.setup();
        func = new FunctionalUtils();
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void addVolumes() throws InterruptedException{
        func.createVolumes(2);
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volumes are not online");
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_GlusterServerFailure() throws InterruptedException{                     
        super.killNDService("gluster_server");
    }  
    
    @AfterClass
    public void tearDown() {
        func.deleteCreatedVolumes();
        func.deleteStoragePool();
        func.deleteStorageServer();
    }
}
