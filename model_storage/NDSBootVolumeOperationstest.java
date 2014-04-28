package com.oracle.nimbula.functional.storage;

import java.util.List;
import java.util.logging.Level;


import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.oracle.colt.result.Result;
import com.oracle.nimbula.modules.utils.StorageTestUtils;
import com.oracle.nimbula.test_framework.BaseTestCase;
import com.oracle.nimbula.test_framework.helpers.NimbulaHelper;
import com.oracle.nimbula.test_framework.resource.types.Instance;
import com.oracle.nimbula.test_framework.resource.types.StorageVolume;
import com.oracle.nimbula.test_framework.result.api.NimbulaResult;

public class NDSBootVolumeOperationstest extends BaseTestCase {	

	StorageVolume storageVolume = null;
	Instance instance = null;
	String storageTags;
	
	private String storageImageList = nimbulaPropertiesReader.getStorageImageList();
    
		public NDSBootVolumeOperationstest() {
		customerNimbulaHelper = new NimbulaHelper(getUser(), getUserPassword());
	}

	@BeforeClass
	public void setUp() {
		
		String storageVolumeLocal = getUser() + "/storageVolume78";
		storageTags = StorageTestUtils.formatStorageTags("tag1");
		storageVolume =StorageTestUtils.addStorageVolume(customerNimbulaHelper, storageVolumeLocal, "7G", nimbulaPropertiesReader.getStorageProperty(),"--tags "+ storageTags, "--imagelist " + storageImageList );
		checkPoint("StorageVolume : " + storageVolume.getStorageVolumeName());
		logger.log(Level.INFO, "Storage Volume: "+storageVolume.getStorageVolumeName());
			}		
		

	@Test
	public void updateStorageVolume_82() {
		String Desc = "New Description";
		NimbulaResult<StorageVolume> result = StorageTestUtils.updateStorageVolume(customerNimbulaHelper, storageVolume.getStorageVolumeName(), "1G", nimbulaPropertiesReader.getStorageProperty(), true, "--description " + "\""+Desc+"\"");
		storageVolume = result.getObject();        
		checkPoint("Storage Volume: "+storageVolume.getStorageVolumeName());
		Assert.assertTrue(storageVolume.getDescription().contains(Desc),
				"Failed to update storagevolume description:-"+result.getError() + System.getProperty("line.separator")
				+ result.getOutput());
		checkPoint("Storage Volume is updated correctly: "+storageVolume.getDescription());

	}

	@Test(dependsOnMethods={"updateStorageVolume_82"}, alwaysRun=true)
	public void launchPlanWithNullIndexforNDSBootVolume_94() {
		NimbulaResult<List<Instance>> result = customerNimbulaHelper.launchLaunchPlan(System.getProperty("nimbula.root") + "/functional/storage/jsons/NDSBootVolumeWithNullIndex_94.json", true);
		logger.log(Level.INFO, "Error is: "+result.getError());
		Assert.assertTrue(result.getExitValue() == 4, "Error code is displayed as expected");
		checkPoint("Error code is displayed correctly" + result.getExitValue());
		Assert.assertTrue((result.getError().startsWith("Validation Error"))&&(result.getError().contains("index must be of type <type 'int'>")), "Error message is not displayed as expected");
		checkPoint("Error Message is displayed correctly" + result.getError());			
	}

	@Test(dependsOnMethods={"launchPlanWithNullIndexforNDSBootVolume_94"}, alwaysRun=true)
	public void launchLaunchPlanWithDiffImagelist_93() {
		String imageListLocal = "/nimbula/public/oel6";		
		NimbulaResult<List<Instance>> result = customerNimbulaHelper.launchLaunchPlan(System.getProperty("nimbula.root") + "/functional/storage/jsons/unchanged/NDSBootVolumeDiffImageList_93.json", true);
		logger.log(Level.INFO, result.getOutput());
		Assert.assertTrue((result.getExitValue() == 0), "## Some failure during the launchPlan. Execution of the command returned with error: " + result.getError());
		instance = result.getObject().get(0);
		checkPoint(instance.getInstanceName().toString());
		boolean changeStatus = customerNimbulaHelper.waitForAttributeStateChange(
				"instance", instance.getInstanceName(),"state",
				"running", 120);
		logger.log(Level.INFO, instance.getInstanceName()
				+ " changeStatus " + changeStatus);
	    Assert.assertEquals(instance.getImageList(),imageListLocal, " Instance is not launched with the expected imagelist");

	}

	@AfterClass(alwaysRun=true)
	public void tearDownSetup() {
			
		
		StorageTestUtils.deleteInstance(customerNimbulaHelper, instance); 		
		
		StorageTestUtils.deleteStorageVolumes(customerNimbulaHelper,storageVolume);
	

	}
}
