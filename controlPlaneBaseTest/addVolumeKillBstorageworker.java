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
public class addVolumeKillBstorageworker extends ControlPlaneBaseTest {
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
    public void last_bStorageworkerFailure() throws InterruptedException{                     
        System.out.println("###### last_bStorageworkerFailure ...");
        super.killNDService("bstorageworker_nfs_1");
    }  
    
    @AfterClass
    public void tearDown() {        
        System.out.println("###### tearDown ...");
        func.deleteVolume();
    }
}
