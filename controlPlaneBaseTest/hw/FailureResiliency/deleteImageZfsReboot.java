package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import java.net.UnknownHostException;
import java.util.logging.Level;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class deleteImageZfsReboot extends TakeoverBaseClass {
    
    @BeforeClass
    public void addStServer_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        Assert.assertTrue(func.downloadImage(),"Error : Download machine image failed!");
        Assert.assertTrue(func.uploadImage(),"Error : Image upload failed");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_deleteMachineImage() throws InterruptedException{
        func.deleteMachineImage();
        if (func.isMachineImageUploaded()){
            logger.log(Level.INFO, "Machineimage failed to delete when bImage was killed");
            Thread.sleep(30000);
            Assert.assertTrue(func.deleteMachineImage(),"Failed to delete machine image");
        }
        Assert.assertFalse(func.isMachineImageUploaded(),"Machine image is not deleted");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_zfsReboot() throws InterruptedException{
        Assert.assertTrue(super.rebootZFSmaster(),"Failed to reboot ZFS master");
    }   
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteMachineImage();
        func.deleteMachineImageDownloadDirectory();
    }
}
