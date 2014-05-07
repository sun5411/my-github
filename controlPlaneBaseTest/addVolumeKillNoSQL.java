package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.oracle.colt.result.Result;
import com.oracle.nimbula.test_framework.helpers.NimbulaHelper;
import com.oracle.nimbula.qa.ha.common.HAConstantDef;

/**
 *
 * @author Sun Ning
 */
public class addVolumeKillNoSQL extends ControlPlaneBaseTest {
    HAUtil util;
    FunctionalUtils func;
    NimbulaHelper nimhelper;
    
    @BeforeClass
    public void addVolume_setup() throws InterruptedException{
        super.setup();
        util = new HAUtil();
        func = new FunctionalUtils();
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void addVolumes() throws InterruptedException{
        func.createVolumes(2);
        Assert.assertTrue(func.areVolumesOnline(), "Error : Volume is not online !");
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_NosqlFailure() throws InterruptedException{                     
        util.killNOSQLReplica();
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
