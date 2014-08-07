mvn test --log-file MyLogs/setBICtoPassiveModeForAllServices -Dnimbula.root=.. -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/setBICtoPassiveModeForAllServices -Dtest=setBICtoPassiveModeForAllServices;

mvn test --log-file MyLogs/gatewayFailoverSwitch1_to_Switch2 -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/gatewayFailoverSwitch1_to_Switch2 -Dtest=vmResiliencySwithch1Reboot 
mvn test --log-file MyLogs/gatewayFailoverSwitch2_to_Switch1 -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/gatewayFailoverSwitch2_to_Switch1 -Dtest=vmResiliencySwithch1Reboot 
mvn test --log-file MyLogs/highIOSwitchReboot -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/highIOSwitchReboot -Dtest=highIOSwitchReboot 
mvn test --log-file MyLogs/runningVMPort1Failure -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/runningVMPort1Failure -Dtest=runningVMPortFailure 
mvn test --log-file MyLogs/runningVMPort2Failure -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/runningVMPort2Failure -Dtest=runningVMPortFailure 
mvn test --log-file MyLogs/launchBootVolumeVmZFSTakeover -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/launchBootVolumeVmZFSTakeover -Dtest=launchBootVolumeVmZFSTakeover 
mvn test --log-file MyLogs/bootVolumeVmZFSTakeover -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/bootVolumeVmZFSTakeover -Dtest=bootVolumeVmZFSTakeover 
mvn test --log-file MyLogs/heavyIOZFSTakeover -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/heavyIOZFSTakeover -Dtest=heavyIOZFSTakeover 
mvn test --log-file MyLogs/attachVolumeZFSTakeover -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/attachVolumeZFSTakeover -Dtest=attachVolumeZFSTakeover 
mvn test --log-file MyLogs/detachVolumeZFSTakeover -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/detachVolumeZFSTakeover -Dtest=detachVolumeZFSTakeover 
mvn test --log-file MyLogs/largeNumberVMsAttachedVolumeTakeover -Dnimbula.root=.. -Dparallel=methods -DrunOrder=alphabetical -DuseFile=true -DoutputDirectory=target/largeNumberVMsAttachedVolumeTakeover -Dtest=largeNumberVMsAttachedVolumeTakeover 

