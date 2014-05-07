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
public class addVolumeKillRabbitMQ extends ControlPlaneBaseTest {
    FunctionalUtils func;
    NimbulaHelper nimhelper;
    
    @BeforeClass
    public void addVolume_setup() throws InterruptedException{
        super.setup();
        func = new FunctionalUtils();
        nimhelper = new NimbulaHelper(HAConstantDef.ROOT_USER,HAConstantDef.ROOT_PASSWORD);
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void addVolumes() throws InterruptedException{
        func.createVolumes(2);
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volume is not online");
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_RabbitMQFailure() throws InterruptedException{                     
        super.killNDService("rabbitmq_1");
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
