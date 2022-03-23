 /*
 * Hub Info
 *
 *  Licensed Virtual the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2020-12-07  thebearmay     Original version 0.1.0
 *    2021-01-30  thebearmay     Add full hub object properties
 *    2021-01-31  thebearmay     Code cleanup, release ready
 *    2021-01-31  thebearmay     Putting a config delay in at initialize to make sure version data is accurate
 *    2021-02-16  thebearmay     Add text date for restart
 *    2021-03-04  thebearmay     Added CPU and Temperature polling 
 *    2021-03-05  thebearmay     Merged CSteele additions and added the degree symbol and scale to the temperature attribute 
 *    2021-03-05  thebearmay     Merged addtions from LGKhan: Added new formatted uptime attr, also added an html attr that stores a bunch of the useful 
 *                                    info in table format so you can use on any dashboard
 *    2021-03-06  thebearmay     Merged security login from BPTWorld (from dman2306 rebooter app)
 *    2021-03-06  thebearmay     Change numeric attributes to type number
 *    2021-03-08  thebearmay     Incorporate CSteele async changes along with some code cleanup and adding tags to the html to allow CSS overrides
 *    2021-03-09  thebearmay     Code tightening as suggested by CSteele, remove state variables, etc.
 *    2021-03-11  thebearmay     Add Sensor capability for Node-Red/MakerAPI 
 *    2021-03-11  thebearmay     Security not set right at initialize, remove state.attrString if it exists (now a local variable)
 *    2021-03-19  thebearmay     Add attributes for JVM Total, Free, and Free %
 *                               Add JVM info to HTML
 *                               Fix for exceeded 1024 attr limit
 *    2021-03-20  thebearmay     Firmware 2.2.6.xxx support, CPU 5min Load
 *    2021-03-23  thebearmay     Add DB Size
 *    2021-03-24  thebearmay     Calculate CPU % from load 
 *    2021-03-28  thebearmay     jvmWork.eachline error on reboot 
 *    2021-03-30  thebearmay     Index out of bounds on reboot
 *    2021-03-31  thebearmay      jvm to HTML null error (first run)
 *    2021-04-13  thebearmay     pull in suggested additions from lgkhan - external IP and combining some HTML table elements
 *    2021-04-14  thebearmay     add units to the HTML
 *    2021-04-20  thebearmay     provide a smooth transition from 1.8.x to 1.9.x
 *    2021-04-26  thebearmay     break out polls as separate preference options
 *    2021-04-27  thebearmay     replace the homegrown JSON parser, with groovy's JsonSluper
 *    2021-04-29  thebearmay     merge pull request from nh.schottfam, clean up/add type declarations, optimize code and add local variables
 *    2021-05-03  thebearmay     add nonPolling zigbee channel attribute, i.e. set at hub startup
 *    2021-05-04  thebearmay     release 2.2.7.x changes (v2.2.0 - v2.2.2)
 *    2021-05-06  thebearmay     code cleanup from 2.2.2, now 2.2.3
 *    2021-05-09  thebearmay     return NA when zigbee channel not valid
 *    2021-05-25  thebearmay     use upTime to recalculate system start when initialize called manually
 *    2021-05-25  thebearmay     upTime display lagging by 1 poll
 *    2021-06-11  thebearmay     add units to the jvm and memory attributes
 *    2021-06-12  thebearmay     put a space between unit and values
 *    2021-06-14  thebearmay     add Max State/Event days, required trimming of the html attribute
 *    2021-06-15  thebearmay     add ZWave Version attribute
 *                               2.4.1 temporary version to stop overflow on reboot
 *    2021-06-16  thebearmay     2.4.2 overflow trap/retry
 *                               2.4.3 firmware0Version and subVersion is the radio firmware. target 1 version and subVersion is the SDK
 *                               2.4.4/5 restrict Zwave Version query to C7
 *    2021-06-17  thebearmay     2.4.8-10 - add MAC address and hub model, code cleanup, better compatibility check, zwaveVersion check override
 *    2021-06-17  thebearmay     freeMemPollEnabled was combined with the JVM/CPU polling when creating the HTML
 *    2021-06-19  thebearmay     fix the issue where on a driver update, if configure isn't a hubModel and macAddr weren't updated
 *    2021-06-29  thebearmay     2.2.8.x removes JVM data -> v2.5.0
 *    2021-06-30  thebearmay     clear the JVM attributes if >=2.2.8.0, merge pull request from nh.schottfam (stronger typing)
 *    2021-07-01  thebearmay     allow Warn level logging to be suppressed
 *    2021-07-02  thebearmay	    fix missing formatAttrib call
 *    2021-07-15  thebearmay     attribute clear fix
 *    2021-07-22  thebearmay     prep work for deleteCurrentState() with JVM attributes
 *                               use the getHubVersion() call for >=2.2.8.141 
 *    2021-07-23  thebearmay     add remUnused preference to remove all attributes that are not being polled 
 *    2021-08-03  thebearmay     put back in repoll on invalid zigbee channel
 *    2021-08-14  thebearmay     add html update from HIA
 *    2021-08-19  thebearmay     zwaveSDKVersion not in HTML
 *    2021-08-23  thebearmay     simplify unit retrieval
 *    2021-09-16  thebearmay     add localIP check into the polling cycle instead of one time check
 *    2021-09-29  thebearmay     suppress temperature event if negative
 *    2021-10-21  thebearmay     force a read against the database instead of cache when building html
 *    2021-11-02  thebearmay     add hubUpdateStatus
 *    2021-11-05  thebearmay     add hubUpdateVersion
 *    2021-11-09  thebearmay     add NTP Information
 *    2021-11-24  thebearmay     remove the hub update response attribute - release notes push it past the 1024 size limit.
 *    2021-12-01  thebearmay     add additional subnets information
 *    2021-12-07  thebearmay     allow data attribute to be suppressed if zigbee data is null, remove getMacAddress() as it has been retired from the API
 *    2021-12-08  thebearmay     fix zigbee channel bug
 *    2021-12-27  thebearmay     169.254.x.x reboot option
 *    2022-01-17  thebearmay     allow reboot to be called without Hub Monitor parameter
 *    2022-01-21  thebearmay     add Mode and HSM Status as a pollable attribute
 *    2022-03-03  thebearmay     look at attribute size each poll and enforce 1024 limit
 *    2022-03-09  thebearmay     fix lastUpdated not always updated
 *    2022-03-17  thebearmay     add zigbeeStatus
 *    2022-03-18  thebearmay     add zwaveStatus
 *    2022-03-23  thebearmay     code cleanup
*/
import java.text.SimpleDateFormat
import groovy.json.JsonSlurper

@SuppressWarnings('unused')
static String version() {return "2.6.24"}

metadata {
    definition (
        name: "Hub Information", 
        namespace: "thebearmay", 
        author: "Jean P. May, Jr.",
        importUrl:"https://raw.githubusercontent.com/thebearmay/hubitat/main/hubInfo.groovy"
    ) {
        capability "Actuator"
        capability "Configuration"
        capability "Initialize"
        capability "Sensor"
        capability "TemperatureMeasurement"
        
        attribute "latitude", "string"
        attribute "longitude", "string"
        attribute "hubVersion", "string"
        attribute "id", "string"
        attribute "name", "string"
        attribute "data", "string"
        attribute "zigbeeId", "string"
        attribute "zigbeeEui", "string"
        attribute "hardwareID", "string"
        attribute "type", "string"
        attribute "localIP", "string"
        attribute "localSrvPortTCP", "string"
        attribute "uptime", "number"
        attribute "lastUpdated", "string"
        attribute "lastHubRestart", "string"
        attribute "firmwareVersionString", "string"
        attribute "timeZone", "string"
        attribute "temperatureScale", "string"
        attribute "zipCode", "string"
        attribute "locationName", "string"
        attribute "locationId", "string"
        attribute "lastHubRestartFormatted", "string"
        attribute "freeMemory", "number"
        attribute "temperatureF", "string"
        attribute "temperatureC", "string"
        attribute "formattedUptime", "string"
        attribute "html", "string"
        attribute "jvmTotal", "number"
        attribute "jvmFree", "number"
        attribute "jvmFreePct", "number"
        attribute "cpu5Min", "number"
        attribute "cpuPct", "number"
        attribute "dbSize", "number"
        attribute "publicIP", "string"
        attribute "zigbeeChannel","string"
        attribute "maxEvtDays", "number"
        attribute "maxStateDays", "number"
        attribute "zwaveVersion", "string"
        attribute "zwaveSDKVersion", "string"        
        attribute "zwaveData", "string"
        attribute "hubModel", "string"
        attribute "hubUpdateStatus", "string"
        attribute "hubUpdateVersion", "string"
        attribute "currentMode", "string"
        attribute "currentHsmMode", "string"
        attribute "ntpServer", "string"
        attribute "ipSubnetsAllowed", "string"
        attribute "zigbeeStatus", "string"
        attribute "zwaveStatus", "string"

        command "hiaUpdate", ["string"]
        command "reboot", ["string"]
    }   
}

preferences {
    input("debugEnable", "bool", title: "Enable debug logging?")
    input("warnSuppress", "bool", title: "Suppress Warn Level Logging")
    input("tempPollEnable", "bool", title: "Enable Temperature Polling")
    input("freeMemPollEnabled", "bool", title: "Enable Free Memory Polling")
    input("cpuPollEnabled", "bool", title: "Enable CPU Load Polling")
    input("dbPollEnabled","bool", title: "Enable DB Size Polling")
    input("publicIPEnable", "bool", title: "Enable Querying the cloud \nto obtain your Public IP Address?", defaultValue: false, required: false, submitOnChange: true)
    input("evtStateDaysEnable", "bool", title:"Enable Display of Max Event/State Days Setting")
    if (tempPollEnable || freeMemPollEnabled || cpuPollEnabled || dbPollEnabled || publicIPEnable || evtStateDaysEnable)
        input("tempPollRate", "number", title: "Polling Rate (seconds)\nDefault:300", defaultValue:300, submitOnChange: true, width:4)
    input("attribEnable", "bool", title: "Enable HTML Attribute Creation?", defaultValue: false, required: false, submitOnChange: true)
    input("checkZwVersion","bool",title:"Force Update of ZWave Version Attribute", defaultValue: false, submitOnChange: true)
    input("zwLocked", "bool", title: "Never Run ZWave Version Update", defaultValue:false, submitOnChange: true)
    input("ntpCkEnable","bool", title: "Check NTP Server on Poll", defaultValue:false,submitOnChange: true)
    input("subnetEnable", "bool", title: "Check for additional Subnets on Poll",defaultValue:false,submitOnChange: true)
    input("suppressData", "bool", title: "Suppress <i>data</i> attribute if Zigbee is null", defaultValue:false, submitOnChange: true)
	input("remUnused", "bool", title: "Remove unused attributes (Requires HE >= 2.2.8.141", defaultValue: false, submitOnChange: true)
    input("allowReboot","bool", title: "Allow Hub to be rebooted", defaultValue: false, submitOnChange: true)
    input("security", "bool", title: "Hub Security Enabled", defaultValue: false, submitOnChange: true)
    if (security) { 
        input("username", "string", title: "Hub Security Username", required: false)
        input("password", "password", title: "Hub Security Password", required: false)
    }
    input("fwUpdatePollRate","number", title:"Poll rate (in seconds) for FW Update Check (Default:6000, Disable:0):", defaultValue:6000, submitOnChange:true, width:6)
}

@SuppressWarnings('unused')
def installed() {
    log.trace "installed()"
    initialize()
}

def initialize(){
    log.trace "Hub Information Driver ${version()} initialized"
    if (!security)  device.updateSetting("security",[value:"false",type:"bool"])
    
    // will additionally be checked before execution to determine if C-7 or above
    if(!zwLocked)
        device.updateSetting("checkZwVersion",[value:"true",type:"bool"])

    runIn(45,"configure")
    restartCheck() //set Restart Time using uptime and current timeatamp
}

@SuppressWarnings('unused')
def updated(){
    if(debugEnable) {
        log.debug "updated()"
        runIn(1800,logsOff)
    }
    if(tempPollEnable || freeMemPollEnabled || cpuPollEnabled || dbPollEnabled || publicIPEnable || checkZwVersion){
        unschedule("getPollValues")
        getPollValues()
    }
    if(fwUpdatePollRate == null) 
        device.updateSetting("fwUpdatePollRate",[value:6000,type:"number"])
    if(fwUpdatePollRate>0){
        unschedule("updateCheck")
        updateCheck()
    }
    if(warnSuppress == null) device.updateSetting("warnSuppress",[value:"false",type:"bool"])
    if (attribEnable)
        formatAttrib()
    else if(location.hub.firmwareVersionString < "2.2.8.141")
        sendEvent(name: "html", value: "<table></table>", isChanged: true)
		
	if(remUnused && location.hub.firmwareVersionString >= "2.2.8.141") {
		if(location.hub.firmwareVersionString >= "2.2.8.0") {
            device.deleteCurrentState("jvmFree")
            device.deleteCurrentState("jvmTotal")
            device.deleteCurrentState("jvmFreePct")
		}
		if(!tempPollEnable) {
		    device.deleteCurrentState("temperatureC")
			device.deleteCurrentState("temperatureF")
			device.deleteCurrentState("temperature")
		}
		if(!freeMemPollEnabled){
		    device.deleteCurrentState("freeMemory")
		}
		if(!cpuPollEnabled){
		    device.deleteCurrentState("cpu5Min")
			device.deleteCurrentState("cpuPct")
		}
		if(!dbPollEnabled){
		    device.deleteCurrentState("dbSize")
		}
		if(!publicIPEnable){
		    device.deleteCurrentState("publicIP")
		}
		if(!checkZwVersion){
		    device.deleteCurrentState("zwaveSDKVersion")
		    device.deleteCurrentState("zwaveVersion")
		}
		if(!attribEnable){
			device.deleteCurrentState("html")
		}
        if(!evtStateDaysEnable){
			device.deleteCurrentState("maxStateDays")
			device.deleteCurrentState("maxEvtDays")
        } 
        if(!ntpCkEnable){
			device.deleteCurrentState("ntpServer")
        }
        if(!subnetEnable){
            device.deleteCurrentState("ipSubnetsAllowed")
        }
        device.deleteCurrentState("hubUpdateResp")
	}
				
}

@SuppressWarnings('unused')
def configure() {
    if(debugEnable) log.debug "configure()"
    List locProp = ["latitude", "longitude", "timeZone", "zipCode", "temperatureScale"]
    def myHub = location.hub
    List hubProp = ["id","name","data","zigbeeId","zigbeeEui","hardwareID","type","localIP","localSrvPortTCP","firmwareVersionString","uptime"]
    for(i=0;i<hubProp.size();i++){
        if(hubProp[i] != "data")
            updateAttr(hubProp[i], myHub["${hubProp[i]}"])
        else if(location.hub.properties.data.zigbeeChannel != null || suppressData == false)
            updateAttr(hubProp[i], myHub["${hubProp[i]}"])
        else if(location.hub.firmwareVersionString >= "2.2.8.0") {
            device.deleteCurrentState("data")
            device.deleteCurrentState("zigbeeChannel")
        }
    }
    for(i=0;i<locProp.size();i++){
        updateAttr(locProp[i], location["${locProp[i]}"])
    }
    if(!suppressData || location.hub.properties.data.zigbeeChannel != null)
        updateAttr("zigbeeChannel",location.hub.properties.data.zigbeeChannel)
    if(location.hub.properties.data.zigbeeChannel != null)
        updateAttr("zigbeeStatus", "enabled")
    else
        updateAttr("zigbeeStatus", "disabled")
    updateAttr("zwaveStatus", zwaveScrape())
    
    formatUptime()
    updateAttr("hubVersion", location.hub.firmwareVersionString) //retained for backwards compatibility
    updateAttr("locationName", location.name)
    updateAttr("locationId", location.id)
    //updateAttr("macAddr", getMacAddress())
    if(device.currentValue("macAddr")){
        if(location.hub.firmwareVersionString >= "2.2.8.0") 
            device.deleteCurrentState("macAddr")
        else
            updateAttr("macAddr","NA")
    }
    updateAttr("hubModel", getModel())
    updateAttr("lastUpdated", now())
    if (tempPollEnable || freeMemPollEnabled || cpuPollEnabled || dbPollEnabled || publicIPEnable || checkZwVersion || ntpCkEnable || subnetEnable) 
        getPollValues()
    if (attribEnable) formatAttrib()
    if(fwUpdatePollRate == null) 
        device.updateSetting("fwUpdatePollRate",[value:6000,type:"number"])
    if(fwUpdatePollRate > 0 ) updateCheck()
}

void updateAttr(String aKey, aValue, String aUnit = ""){
    aValue = aValue.toString()
    if(aValue.length() > 1024) {
        log.error "Attribute value for $aKey exceeds 1024, current size = ${aValue.length()}, truncating to 1024..."
        aValue = aValue.substring(0,1023)
    }
    sendEvent(name:aKey, value:aValue, unit:aUnit)
}

void formatUptime(){
    try {
        Long ut = location.hub.uptime.toLong()
        Integer days = Math.floor(ut/(3600*24)).toInteger()
        Integer hrs = Math.floor((ut - (days * (3600*24))) /3600).toInteger()
        Integer min = Math.floor( (ut -  ((days * (3600*24)) + (hrs * 3600))) /60).toInteger()
        Integer sec = Math.floor(ut -  ((days * (3600*24)) + (hrs * 3600) + (min * 60))).toInteger()
    
        String attrval = days.toString() + "d," + hrs.toString() + "h," + min.toString() + "m," + sec.toString() + "s"
        updateAttr("formattedUptime", attrval) 
    } catch(ignore) {
        updateAttr("formattedUptime", "")
    }
}

void formatAttrib(){
    if(debugEnable) log.debug "formatAttrib"
    String attrStr = "<style>td{text-align:left;}</style><table id='hubInfoTable'>"
    
    attrStr += addToAttr("Name","name")

     if(!device.currentValue("hubModel"))
         updateAttr("hubModel",getModel())
     List combineH = ["hubModel", "hubVersion"]
     attrStr += combineAttr("Version", combineH)
    
    if(publicIPEnable) {
        List combine = ["localIP", "publicIP"]
        attrStr += combineAttr("IP Local/Public", combine)
    } else
        attrStr += addToAttr("IP Addr","localIP")
    
    if(freeMemPollEnabled)
           attrStr += addToAttr("Free Mem","freeMemory","int")
    
    if(cpuPollEnabled) {
 
        if(device.currentValue("cpu5Min")){
            List combine = ["cpu5Min", "cpuPct"]
            attrStr += combineAttr("CPU Load/Load%", combine)
        }
        if(location.hub.firmwareVersionString <= "2.2.8.0"){
            List combineA = ["jvmTotal", "jvmFree", "jvmFreePct"]
            attrStr += combineAttr("JVM Tot/Free/%", combineA)
        }
    }

    if(device.currentValue("dbSize")) attrStr +=addToAttr("DB Size","dbSize")
    if(evtStateDaysEnable){
        List combine = ["maxEvtDays", "maxStateDays"]
        attrStr += combineAttr("Max Evt/State Days", combine)
    }

    attrStr += addToAttr("Last Restart","lastHubRestartFormatted")
    attrStr += addToAttr("Uptime","formattedUptime")

    if(tempPollEnable) {
        String tempAttrib = location.temperatureScale=="C" ? "temperatureC" : "temperatureF"
        attrStr += addToAttr("Temperature",tempAttrib)
    }
    if(!suppressData)
        attrStr += addToAttr("ZB Channel","zigbeeChannel")
    
    if (device.currentValue("zwaveVersion")){
        List combine = ["zwaveVersion","zwaveSDKVersion"]
        attrStr += combineAttr("ZW Radio/SDK", combine)   
    }
    
    attrStr += "</table>"

    if (debugEnable) log.debug "after calls attr string = $attrStr"
    updateAttr("html", attrStr)
    //updateAttr("htmlLength",attrStr.length())
    if (attrStr.length() > 1024) updateAttr("html", "Max Attribute Size Exceeded: ${attrStr.length()}")
}

String combineAttr(String name, List<String> keys){
    if(enableDebug) log.debug "adding $name, $keys.length"

    String retResult = '<tr><td align="left">'
    retResult += name + '</td><td align="left">'
    
    String keyResult = ""
    for (i = 0;i < keys.size(); i++) {
        keyResult+= device.currentValue(keys[i],true)
        String attrUnit = getUnitFromState(keys[i])
        if (attrUnit != null) keyResult+=" "+attrUnit
        if (i < keys.size() - 1) keyResult+= " / "
    }
            
    retResult += keyResult+'</td></tr>'
    return retResult
}

String addToAttr(String name, String key, String convert = "none") {
    if(enableDebug) log.debug "adding $name, $key"
    String retResult = '<tr><td>'
    retResult += name + '</td><td>'

    String attrUnit = getUnitFromState(key)
    if (attrUnit == null) attrUnit =""

    def curVal = device.currentValue(key,true)
    if(curVal){
        if (convert == "int"){
            retResult += curVal.toInteger().toString()+" "+attrUnit
        } else retResult += curVal.toString() + " "+attrUnit
    }
    retResult += '</td></tr>'
    return retResult
}

String getModel(){
    try{
        String model = getHubVersion() // requires >=2.2.8.141
    } catch (ignore){
        try{
            httpGet("http://${location.hub.localIP}:8080/api/hubitat.xml") { res ->
                model = res.data.device.modelName
            return model
            }        
        } catch(ignore_again) {
            return ""
        }
    }
}

boolean isCompatible(Integer minLevel) { //check to see if the hub version meets the minimum requirement
    String model = device.currentValue("hubModel",true)
    if(!model){
        model = getModel()
        updateAttr("hubModel", model)
    }
    String[] tokens = model.split('-')
    String revision = tokens.last()
    return (Integer.parseInt(revision) >= minLevel)

}

void getPollValues(){
    // start - Modified from dman2306 Rebooter app
    String cookie=(String)null
    if(security) {
        httpPost(
            [
                uri: "http://${location.hub.localIP}:8080",
                path: "/login",
                query: [ loginRedirect: "/" ],
                body: [
                    username: username,
                    password: password,
                    submit: "Login"
                ]
            ]
        ) { resp -> cookie = ((List)((String)resp?.headers?.'Set-Cookie')?.split(';'))?.getAt(0) }
    }
    // End - Modified from dman2306 Rebooter app
    // repoll zigbee channel if invalid
	
    if (device.currentValue("zigbeeChannel") == "NA") { 
        //myHubData = parseHubData()
        updateAttr("zigbeeChannel",location.hub.properties.data.zigbeeChannel)
    }


    if(location.hub.properties.data.zigbeeChannel != null)
        updateAttr("zigbeeStatus", "enabled")
    else
        updateAttr("zigbeeStatus", "disabled")
        
 
    //verify localIP in case of change
    updateAttr("localIP", location.hub.localIP)
    
    //Hub Mode & HSM Status
    updateAttr("currentMode", location.properties.currentMode)
    updateAttr("currentHsmMode", location.hsmStatus)
    
    // Zwave Version
    if(checkZwVersion == null && isCompatible(7))
        device.updateSetting("checkZwVersion",[value:"true",type:"bool"])
    else if(checkZwVersion == null)
        device.updateSetting("checkZwVersion",[value:"false",type:"bool"])
    if(zwLocked == null) device.updateSetting("zwLocked",[value:"false",type:"bool"])

    if(checkZwVersion && isCompatible(7) && !zwLocked){
        Map paramZ = [
            uri    : "http://${location.hub.localIP}:8080",
            path   : "/hub/zwaveVersion",
            headers: ["Cookie": cookie]
        ]
        if (debugEnable) log.debug paramZ
        asynchttpGet("getZwave", paramZ)
    } else if (checkZwVersion) {
        device.updateSetting("checkZwVersion",[value:"false",type:"bool"])
        updateAttr("zwaveData",null)
    }
    //Zwave Status - enabled/disabled/unknown
    updateAttr("zwaveStatus", zwaveScrape())
    
    // get Temperature
    if(tempPollEnable) {
        Map params = [
                uri    : "http://${location.hub.localIP}:8080",
                path   : "/hub/advanced/internalTempCelsius",
                headers: ["Cookie": cookie]
        ]
        if (debugEnable) log.debug params
        asynchttpGet("getTempHandler", params)
    }

    // get Free Memory
    if(freeMemPollEnabled) {
        Map params = [
                uri    : "http://${location.hub.localIP}:8080",
                path   : "/hub/advanced/freeOSMemory",
                headers: ["Cookie": cookie]
        ]
        if (debugEnable) log.debug params
        asynchttpGet("getFreeMemHandler", params)
    }
    
    // get Free JVM & CPU
    if(cpuPollEnabled) {
        Map params
        if (location.hub.firmwareVersionString <= "2.2.5.131") {
            params = [
                    uri    : "http://${location.hub.localIP}:8080",
                    path   : "/hub/advanced/freeOSMemoryHistory",
                    headers: ["Cookie": cookie]
            ]
        } else {
            params = [
                    uri    : "http://${location.hub.localIP}:8080",
                    path   : "/hub/advanced/freeOSMemoryLast",
                    headers: ["Cookie": cookie]
            ]
        }
        if (debugEnable) log.debug params
        asynchttpGet("getJvmHandler", params)
    }

    //Get DB size
    if(dbPollEnabled) {
        Map params = [
                uri    : "http://${location.hub.localIP}:8080",
                path   : "/hub/advanced/databaseSize",
                headers: ["Cookie": cookie]
        ]

        if (debugEnable) log.debug params
        asynchttpGet("getDbHandler", params)
    }

    //get Public IP 
    if(publicIPEnable) {
        Map params =
        [
            uri:  "https://ifconfig.co/",
            headers: [ 
                   Host: "ifconfig.co",               
                   Accept: "application/json"
            ]
        ]
    
        if(debugEnable)log.debug params
        asynchttpGet("getIfHandler", params)
    }
 
    //Max State Days
    if(evtStateDaysEnable) {
        Map params =
        [
                uri    : "http://${location.hub.localIP}:8080",
                path   : "/hub/advanced/maxDeviceStateAgeDays",
                headers: ["Cookie": cookie]           
        ]
    
        if(debugEnable)log.debug params
        asynchttpGet("getStateDaysHandler", params)
     
     //Max Event Days
        params =
        [
                uri    : "http://${location.hub.localIP}:8080",
                path   : "/hub/advanced/maxEventAgeDays",
                headers: ["Cookie": cookie]           
        ]
    
        if(debugEnable)log.debug params
        asynchttpGet("getEvtDaysHandler", params)
     
     
    } 
    
    // NTP Server
    if(ntpCkEnable){
        Map params =
        [
                uri    : "http://${location.hub.localIP}:8080",
                path   : "/hub/advanced/ntpServer",
                headers: ["Cookie": cookie]           
        ]
    
        if(debugEnable)log.debug params
        asynchttpGet("getNtpServer", params)
    }
    // Additional Subnets 
    if(subnetEnable) {
        Map params =
        [
                uri    : "http://${location.hub.localIP}:8080",
                path   : "/hub/allowSubnets",
                headers: ["Cookie": cookie]           
        ]
    
        if(debugEnable)log.debug params
        asynchttpGet("getSubnets", params)    
    
    }
    //End Pollable Gets
	
    if(!suppressData || location.hub.properties.data.zigbeeChannel != null)
        updateAttr("zigbeeChannel",location.hub.properties.data.zigbeeChannel) 
		
    updateAttr("uptime", location.hub.uptime)
    formatUptime()
	
    if (attribEnable) formatAttrib()
    if (debugEnable) log.debug "tempPollRate: $tempPollRate"

    if (tempPollEnable || freeMemPollEnabled || cpuPollEnabled || dbPollEnabled || publicIPEnable || checkZwVersion || ntpCkEnable || subnetEnable) {
        if(tempPollRate == null){
            device.updateSetting("tempPollRate",[value:300,type:"number"])
            runIn(300,"getPollValues")
        }else {
            runIn(tempPollRate,"getPollValues")
        }
    }
    updateAttr("lastUpdated", now())
}

@SuppressWarnings('unused')
def getTemp(){  // this is to handle the upgrade path from >= 1.8.x
    log.info "Upgrading HubInfo polling from 1.8.x"
    unschedule("getTemp")
    getPollValues()
}

@SuppressWarnings('unused')
void getTempHandler(resp, data) {
    try {
        if(resp.getStatus() == 200 || resp.getStatus() == 207) {
            Double tempWork = new Double(resp.data.toString())
            if(tempWork > 0) {
                if(debugEnable) log.debug tempWork
                if (location.temperatureScale == "F")
                    updateAttr("temperature",celsiusToFahrenheit(tempWork),"°F")
                else
                    updateAttr("temperature",tempWork,"°C")

                updateAttr("temperatureF",celsiusToFahrenheit(tempWork)+ " °F")
                updateAttr("temperatureC",tempWork+ " °C")
            }
        }
    } catch(ignored) {
        def respStatus = resp.getStatus()
        if (!warnSuppress) log.warn "getTemp httpResp = $respStatus but returned invalid data, will retry next cycle"
    } 
}

@SuppressWarnings('unused')
void getZwave(resp, data) {
    try {
        if(resp.getStatus() == 200 || resp.getStatus() == 207) {
            String zwaveData = resp.data.toString()
            if(debugEnable) log.debug resp.data.toString()
            if(zwaveData.length() < 1024){
                updateAttr("zwaveData",zwaveData)
                parseZwave(zwaveData)
                device.updateSetting("checkZwVersion",[value:"false",type:"bool"])
            }
            else if (!warnSuppress) log.warn "Invalid data returned for Zwave, length = ${zwaveData.length()} will retry"
        }
    } catch(ignored) {
        if (!warnSuppress) log.warn "getZwave Parsing Error"    
    }
 
    
}

@SuppressWarnings('unused')
void getFreeMemHandler(resp, data) {
    try {
        if(resp.getStatus() == 200 || resp.getStatus() == 207) {
            Integer memWork = new Integer(resp.data.toString())
            if(debugEnable) log.debug memWork
            updateAttr("freeMemory",memWork, "KB")
        }
    } catch(ignored) {
        def respStatus = resp.getStatus()
        if (!warnSuppress) log.warn "getFreeMem httpResp = $respStatus but returned invalid data, will retry next cycle"    
    }
}

@SuppressWarnings('unused')
void getJvmHandler(resp, data) {
    String jvmWork
    List<String> jvmArr = []
    try {
        if(resp.getStatus() == 200 || resp.getStatus() == 207) {
            jvmWork = resp.data.toString()
        }
        if (attribEnable) runIn(5,formatAttrib) //allow for events to register before updating - thebearmay 210308
    } catch(ignored) {
        def respStatus = resp.getStatus()
        if (!warnSuppress) log.warn "getJvm httpResp = $respStatus but returned invalid data, will retry next cycle"    
    }
    if (jvmWork) {
        Integer lineCount = 0
        jvmWork.eachLine{
            lineCount++
        }
        Integer lineCount2 = 0
        jvmWork.eachLine{
            lineCount2++
            if(lineCount==lineCount2)
                jvmArr = it.split(",")
        }
        if(jvmArr.size() > 1){
            if(location.hub.firmwareVersionString <= "2.2.8.0"){
                Integer jvmTotal = jvmArr[2].toInteger()
                updateAttr("jvmTotal",jvmTotal, "KB")
                Integer jvmFree = jvmArr[3].toInteger()
                updateAttr("jvmFree",jvmFree, "KB")
                Double jvmFreePct = (jvmFree/jvmTotal)*100
                updateAttr("jvmFreePct",jvmFreePct.round(1),"%")
                if(jvmArr.size() > 4) {
                    Double cpuWork=jvmArr[4].toDouble()
                    updateAttr("cpu5Min",cpuWork.round(2))
                    cpuWork = (cpuWork/4.0D)*100.0D //Load / #Cores - if cores change will need adjusted to reflect
                    updateAttr("cpuPct",cpuWork.round(2),"%")
                }
            } else {
                Double cpuWork=jvmArr[2].toDouble()
                updateAttr("cpu5Min",cpuWork.round(2))
                cpuWork = (cpuWork/4.0D)*100.0D //Load / #Cores - if cores change will need adjusted to reflect
                updateAttr("cpuPct",cpuWork.round(2),"%")
                if(device.currentValue("jvmFree")) {
                    try { // requires >= 2.2.8.141
                        device.deleteCurrentState("jvmFree")
                        device.deleteCurrentState("jvmTotal")
                        device.deleteCurrentState("jvmFreePct")
                    } catch (ignore) {
                        updateAttr("jvmFree","\0")
                        updateAttr("jvmTotal","\0")
                        updateAttr("jvmFreePct","\0") 
                    } 
                }         
            }
                
        }
    }
}

@SuppressWarnings('unused')
void getDbHandler(resp, data) {
    try {
        if(resp.getStatus() == 200 || resp.getStatus() == 207) {
            Integer dbWork = new Integer(resp.data.toString())
            if(debugEnable) log.debug dbWork
            updateAttr("dbSize",dbWork,"MB")
        }
    } catch(ignored) {
        def respStatus = resp.getStatus()
        if (!warnSuppress) log.warn "getDb httpResp = $respStatus but returned invalid data, will retry next cycle"
    } 
}

@SuppressWarnings('unused')
void getIfHandler(resp, data){
    try{
        if (resp.getStatus() == 200){
            if (debugEnable) log.info resp.data
            def jSlurp = new JsonSlurper()
            Map ipData = (Map)jSlurp.parseText((String)resp.data)
            updateAttr("publicIP",ipData.ip)
        } else {
            if (!warnSuppress) log.warn "Status ${resp.getStatus()} while fetching Public IP"
        } 
    } catch (Exception ex){
        if (!warnSuppress) log.warn ex
    }
}   

@SuppressWarnings('unused')
void getStateDaysHandler(resp, data) {
    try {
        if(resp.getStatus() == 200 || resp.getStatus() == 207) {
            Integer stateDays = new Integer(resp.data.toString())
            if(debugEnable) log.debug "Max State Days $stateDays"

            updateAttr("maxStateDays",stateDays)
        }
    } catch(ignored) {
        def respStatus = resp.getStatus()
        if (!warnSuppress) log.warn "getStateDays httpResp = $respStatus but returned invalid data, will retry next cycle"
    } 
}

@SuppressWarnings('unused')
void getEvtDaysHandler(resp, data) {
    try {
        if(resp.getStatus() == 200 || resp.getStatus() == 207) {
            Integer evtDays = new Integer(resp.data.toString())
            if(debugEnable) log.debug "Max Event Days $evtDays"

            updateAttr("maxEvtDays",evtDays)
        }
    } catch(ignored) {
        def respStatus = resp.getStatus()
        if (!warnSuppress) log.warn "getEvtDays httpResp = $respStatus but returned invalid data, will retry next cycle"
    } 
}

@SuppressWarnings('unused')
void getNtpServer(resp, data) {
    try {
        if (resp.status == 200) {
            ntpServer = resp.data.toString()
            if(ntpServer == "No value set") ntpServer = "Hub Default(Google)"
            updateAttr("ntpServer", ntpServer)
        } else {
            if(!warnSuppress) log.warn "NTP server check returned status: ${resp.status}"
        }
    }catch (ignore) {
    }
}

@SuppressWarnings('unused')
void getSubnets(resp, data) {
    try {
        if (resp.status == 200) {
            subNets = resp.data.toString()
            if(subNets == "Not set") subNets = "Hub Default"
            updateAttr("ipSubnetsAllowed", subNets)
        } else {
            if(!warnSuppress) log.warn "Subnet check returned status: ${resp.status}"
        }
    }catch (ignore) {
    }
}

@SuppressWarnings('unused')
void parseZwave(String zString){
    Integer start = zString.indexOf('(')
    Integer end = zString.length()
    String wrkStr
    
    if(start == -1 || end < 1 || zString.indexOf("starting up") > 0 ){ //empty or invalid string - possibly non-C7
        updateAttr("zwaveData",null)
    }else {
        wrkStr = zString.substring(start,end)
        wrkStr = wrkStr.replace("(","[")
        wrkStr = wrkStr.replace(")","]")

        HashMap zMap = (HashMap)evaluate(wrkStr)
        
        updateAttr("zwaveSDKVersion","${((List)zMap.targetVersions)[0].version}.${((List)zMap.targetVersions)[0].subVersion}")
        updateAttr("zwaveVersion","${zMap?.firmware0Version}.${zMap?.firmware0SubVersion}")
    }
}

String getUnitFromState(String attrName){
   	return device.currentState(attrName)?.unit
}

void restartCheck() {
    if(debugEnable) log.debug "$rsDate"
    Long ut = now() - (location.hub.uptime.toLong()*1000)
    Date upDate = new Date(ut)
    if(debugEnable) log.debug "RS: $rsDate  UT:$ut  upTime Date: $upDate   upTime: ${location.hub.uptime}"
    
    updateAttr("lastHubRestart", ut)
    SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    updateAttr("lastHubRestartFormatted",sdf.format(upDate))
}

String zwaveScrape(){
    httpGet("http://${location.hub.localIP}:8080/hub/zwaveInfo") { res ->
          dataC = res.data.toString().substring(196)
          if(dataC.indexOf("const zwaveStatus = 'false'")>-1)
              zwaveStatus="enabled"
          else if(dataC.indexOf("const zwaveStatus = 'true'")>-1)
              zwaveStatus="disabled"
          else
              zwaveStatus="unknown"
          return zwaveStatus
    }
} 

@SuppressWarnings('unused')
void updateCheck(){
    if(fwUpdatePollRate == 0) {
        unschedule("updateCheck")
        return
    }
    params = [
            uri: "http://${location.hub.localIP}:8080",
            path:"/hub/cloud/checkForUpdate",
            timeout: 10
        ]
   asynchttpGet("updChkCallback", params)
   runIn(fwUpdatePollRate,"updateCheck")
}

@SuppressWarnings('unused')
void updChkCallback(resp, data) {
    try {
        if (resp.status == 200) {
           def jSlurp = new JsonSlurper()
           Map resMap = (Map)jSlurp.parseText((String)resp.data)
           updateAttr("hubUpdateStatus",resMap.status)
           if(location.hub.firmwareVersionString >= "2.2.8.0" && device.currentValue("hubUpdateResp"))
               device.deleteCurrentState("hubUpdateResp")
           if(resMap.version)
		        updateAttr("hubUpdateVersion",resMap.version)
           else updateAttr("hubUpdateVersion",location.hub.firmwareVersionString)
        }
    } catch(ignore) {
       updateAttr("hubUpdateStatus","Status Not Available")
    }

}

@SuppressWarnings('unused')
void hiaUpdate(htmlStr, auth) {
	if(!warnSuppresss) log.warn "HIA - HubInfo version mismatch please upgrade HIA"
	hiaUpdate(htmlStr)
}

@SuppressWarnings('unused')
void hiaUpdate(htmlStr) {
	updateAttr("html",htmlStr)
}

@SuppressWarnings('unused')
void reboot(a ) {
	reboot()
}


@SuppressWarnings('unused')
void reboot() {
    if(!allowReboot){
        log.error "Reboot was requested, but allowReboot was set to false"
        return
    }
    log.info "Hub Reboot requested"
    // start - Modified from dman2306 Rebooter app
    String cookie=(String)null
    if(security) {
        httpPost(
            [
                uri: "http://${location.hub.localIP}:8080",
                path: "/login",
                query: [ loginRedirect: "/" ],
                body: [
                    username: username,
                    password: password,
                    submit: "Login"
                ]
            ]
        ) { resp -> cookie = ((List)((String)resp?.headers?.'Set-Cookie')?.split(';'))?.getAt(0) }
    }
	httpPost(
		[
			uri: "http://${location.hub.localIP}:8080",
			path: "/hub/reboot",
			headers:[
				"Cookie": cookie
			]
		]
	) {		resp ->	} 
    // end - Modified from dman2306 Rebooter app
}

@SuppressWarnings('unused')
void logsOff(){
     device.updateSetting("debugEnable",[value:"false",type:"bool"])
}
