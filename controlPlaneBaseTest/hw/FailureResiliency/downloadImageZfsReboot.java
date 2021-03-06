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
public class downloadImageZfsReboot extends TakeoverBaseClass {
    
    @BeforeClass
    public void addStServer_setup() throws InterruptedException, UnknownHostException{
        super.setup();
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_downloadMachineImage() throws InterruptedException{
        
        func.downloadImage();
        if (!func.isMachineImageDownloaded()){
            logger.log(Level.INFO, "Machineimage failed to download when bImage was killed");
            Thread.sleep(30000);
            Assert.assertTrue(func.downloadImage(),"Machine image failed to download - 2nd try");
        }
        Assert.assertTrue(func.isMachineImageDownloaded(),"Machine image is not downloaded");
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
