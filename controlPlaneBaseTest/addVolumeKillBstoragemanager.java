package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class addVolumeKillBstoragemanager extends ControlPlaneBaseTest {
    HAUtil util;
    FunctionalUtils func;
    
    @BeforeClass
    public void addVolume_setup() throws InterruptedException{
        System.out.println("###### Setup ...");
        super.setup();
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void addVolume() throws InterruptedException{
        System.out.println("###### addVolume ...");
        func.createVolume();
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_bStoragemanagerFailure() throws InterruptedException{                     
        System.out.println("###### last_bStoragemanagerFailure ...");
        super.killNDService("bstoragemanager_1");
    }  
    
    @AfterClass
    public void tearDown() {        
        System.out.println("###### tearDown ...");
        func.deleteVolume();
    }
}
