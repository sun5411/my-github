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
public class deleteVolumeKillRabbitMQ extends ControlPlaneBaseTest {
    FunctionalUtils func;
    
    @BeforeClass
    public void deleteVolume_setup() throws InterruptedException{
        super.setup();
        func = new FunctionalUtils();
        Assert.assertTrue(func.createVolumes(2), "Error : Create volumes failed!");
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volumes are not online!");
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void deleteVolume() throws InterruptedException{
        Assert.assertTrue(func.deleteCreatedVolumes(), "Error : Delete volume failed!");
        Assert.assertFalse(func.areVolumesOnline(), "Error : Volumes are not online!");
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_RabbitMQFailure() throws InterruptedException{                     
        super.killNDService("rabbitmq_1");
    }  
    
    @AfterClass
    public void tearDown() {
        func.deleteCreatedVolumes();
        func.deleteStoragePool();
        func.deleteStorageServer();
    }
}
