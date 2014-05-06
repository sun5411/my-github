package com.oracle.nimbula.qa.ha.serviceFailureResiliency;
import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Sun Ning
 */
public class addVolumeKillBstoragemanager extends ControlPlaneBaseTest {
    FunctionalUtils func;
    @BeforeClass
    public void addVolume_setup() throws InterruptedException{
        super.setup();
        func = new FunctionalUtils();
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void addVolumes() throws InterruptedException{
        func.createVolumes(2);
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volume is not online");
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_bStoragemanagerFailure() throws InterruptedException{                     
        super.killNDService("bstoragemanager_1");
    }  
    
    @AfterClass
    public void tearDown() {        
        func.deleteCreatedVolumes();
    }
}
