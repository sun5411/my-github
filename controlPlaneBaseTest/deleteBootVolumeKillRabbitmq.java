package com.oracle.nimbula.qa.ha.serviceFailureResiliency;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author Sun Ning
 */
public class deleteBootVolumeKillRabbitmq extends ControlPlaneBaseTest {
        
    @BeforeClass
    public void deleteVolume_setup() throws InterruptedException{
        super.setup();
        Assert.assertTrue(func.createBootVolume(20),"Storage volumes could not be created");
        while (!func.areVolumesOnline()){
            logger.info("Not all volumes are online yet");
            Thread.sleep(30000);
        }
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteVolumes() throws InterruptedException{
        Assert.assertTrue(func.deleteCreatedBootVolumes(), "Storage volumes could not be deleted");
        while (!func.areVolumesOnline()){
            logger.info("Not all volumes are delete yet");
            Thread.sleep(30000);
        }
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_bStoragemanagerFailure() throws InterruptedException{          
        super.killNDService("rabbitmq_1");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteCreatedBootVolumes();
        func.deleteStoragePool();
        func.deleteStorageServer();
    }
}
