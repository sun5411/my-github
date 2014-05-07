package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import com.oracle.colt.result.Result;
import com.oracle.nimbula.test_framework.helpers.NimbulaHelper;
import com.oracle.nimbula.qa.ha.common.HAConstantDef;

/**
 *
 * @author Sun Ning
 */
public class deleteVolumeKillGlusterServer extends ControlPlaneBaseTest {
    FunctionalUtils func;
    NimbulaHelper nimhelper;
    
    @BeforeClass
    public void deleteVolume_setup() throws InterruptedException{
        super.setup();
        func = new FunctionalUtils();
        nimhelper = new NimbulaHelper(HAConstantDef.ROOT_USER,HAConstantDef.ROOT_PASSWORD);
        Assert.assertTrue(func.createVolumes(2), "Error : Create volumes failed!");
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volume is not online!");
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void deleteVolume() throws InterruptedException{
        Assert.assertTrue(func.deleteCreatedVolumes(), "Error : Delete volume failed!");
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_GlusterServerFailure() throws InterruptedException{                     
        super.killNDService("gluster_server");
    }
    
    @AfterClass
    public void tearDown() {        
        func.deleteCreatedVolumes();
        func.deleteStoragePool();
        Result res = nimhelper.deleteProperty("storage", HAConstantDef.STORAGE_PROP, true);
        if ( 0 != res.getExitValue() ){
            System.out.println("Error : Delete property failed !");
        }
        func.deleteStorageServer();
    }
}
