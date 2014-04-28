package com.oracle.nimbula.functional.storage;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.oracle.colt.sshagent.SSHSession;
import com.oracle.nimbula.modules.utils.StorageTestUtils;
import com.oracle.nimbula.test_framework.BaseTestCase;
import com.oracle.nimbula.test_framework.helpers.NimbulaHelper;
import com.oracle.nimbula.test_framework.helpers.TestUtils;
import com.oracle.nimbula.test_framework.resource.types.Instance;
import com.oracle.nimbula.test_framework.resource.types.StorageVolume;

public class RebootNDSBootVolumeTest extends BaseTestCase {

	private String storageTags;
	Instance instance;
	StorageVolume storageBootVol1 = null;

	public RebootNDSBootVolumeTest() {
		customerNimbulaHelper = new NimbulaHelper(getUser(), getUserPassword());
	}

	@BeforeClass
	public void setUp() {

		// ///////////////////addStorageVolume//////////////////////////////
		storageTags = StorageTestUtils.formatStorageTags("tag1");
		String storageVolumeName = getUser() + "/bootVol1";
		storageBootVol1 = StorageTestUtils.addStorageVolume(
				customerNimbulaHelper, storageVolumeName, "7G",
				nimbulaPropertiesReader.getStorageProperty(), "--tags "
						+ storageTags,
				"--imagelist " + nimbulaPropertiesReader.getStorageImageList());

	}

	@Test()
	public void rebootNDSBootVolumeInstance_77() {

		File file = new File(System.getProperty("nimbula.root")
				+ "/functional/storage/jsons/NDSBootVol.json");
		instance = StorageTestUtils.launchLaunchPlan(customerNimbulaHelper,
				file);

		// SSH to the instance
		SSHSession session = new SSHSession(instance.getIp(), 22, "root",
				"nimbula");
		Assert.assertTrue(session.isConnected(),
				"FAILED: Could NOT SSH to the Instance");

		// Reboot the instance
		boolean rebootStatus = StorageTestUtils.rebootVMInstance(
				customerNimbulaHelper, instance);
		Assert.assertTrue(rebootStatus,
				"FAILED: Could not reboot the Instance ");

		// Check if the VM is reachable after reboot
		instance = TestUtils.getInstance(customerNimbulaHelper,
				instance.getInstanceName(), true);
		boolean status = TestUtils.verifyVMIsReachable(instance.getIp(), 100);
		Assert.assertTrue(status, "FAILED: VM is NOT reachable");
	}

	@AfterClass(alwaysRun = true)
	public void tearDownSetup() {

		// ///////////////////delete Instance// //////////////////////////////
		StorageTestUtils.deleteInstance(customerNimbulaHelper, instance);

		// ///////////////////delete StorageVolumes/////////////////////////
		StorageTestUtils.deleteStorageVolumes(customerNimbulaHelper,
				storageBootVol1);

	}
}
