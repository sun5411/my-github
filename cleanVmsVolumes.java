/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.InstanceUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
/**
 *
 * @author Sun Ning
 */
public class cleanVmsVolumes extends ControlPlaneBaseTest {
    FunctionalUtils func;
    InstanceUtil vm;
    
    @BeforeClass
    public void attachVolumes_setup() throws InterruptedException {
        super.setup();
        func = new FunctionalUtils();
        vm = new InstanceUtil();
    }

    @Test(alwaysRun=true, timeOut=900000)
    public void delete_vms_volumes() throws InterruptedException {
        vm.deleteAllVMs();
        func.deleteAllSystemVolumes();
    }
    
    @AfterClass
    public void tearDown()throws InterruptedException  {
        //vm.deleteAllVMs();
        //func.deleteAllSystemVolumes();
    }        
}
