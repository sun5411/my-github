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
public class deleteVolumeKillGlusterServer extends ControlPlaneBaseTest {
    HAUtil util;
    FunctionalUtils func;
    
    @BeforeClass
    public void deleteVolume_setup() throws InterruptedException{
        System.out.println("###### Setup ...");
        super.setup();
        func.createVolume();
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void deleteVolume() throws InterruptedException{
        System.out.println("###### deleteVolume ...");
        func.deleteVolume();
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_GlusterServerFailure() throws InterruptedException{                     
        System.out.println("###### last_GlusterServerFailure ...");
        super.killNDService("gluster_server");
    }
    
    @AfterClass
    public void tearDown() {        
        System.out.println("###### tearDown ...");
    }
}
