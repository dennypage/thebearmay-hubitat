/*
 * Lock Event History 
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
 *    Date          Who           What
 *    ----          ---           ----
 *    2021-04-29    thebearmay    Original version 0.1.0
 */

import java.text.SimpleDateFormat
static String version()	{  return '0.1.0'  }


definition (
	name: 			"Lock History", 
	namespace: 		"thebearmay", 
	author: 		"Jean P. May, Jr.",
	description: 	"Display a history of the events for locks, allows side-by-side display of multiple devices for comparisons",
	category: 		"Utility",
	importUrl: "https://raw.githubusercontent.com/thebearmay/hubitat/main/apps/lockHistory.groovy",
	oauth: 			false,
    iconUrl:        "",
    iconX2Url:      ""
) 

preferences {
   page name: "mainPage"
   page name: "lockHistory"
}

def installed() {
//	log.trace "installed()"
    state?.isInstalled = true
    initialize()
}

def updated(){
//	log.trace "updated()"
    if(!state?.isInstalled) { state?.isInstalled = true }
	if(debugEnable) runIn(1800,logsOff)
}

def initialize(){
}

void logsOff(){
     app.updateSetting("debugEnable",[value:"false",type:"bool"])
}

def mainPage(){
    dynamicPage (name: "mainPage", title: "", install: true, uninstall: true) {
      	if (app.getInstallationState() == 'COMPLETE') {   
	    	section("Main")
		    {
                input "qryDevice", "capability.lockCodes", title: "Devices of Interest:", multiple: true, required: true, submitOnChange: true
                input "qryDate", "string", title: "Pull motion data from this date forward (yyyy-MM-dd hh:mm):", required: true, submitOnChange: true
                if (qryDevice != null && qryDate != null) href "lockHistory", title: "Lock History", required: false
                input "notifyDevice", "capability.notification", title: "Notification Devices:", multiple: true, submitOnChange: true
                if(notifyDevice?.size() > 0) 
                    lockSubscribe()
                else
                    unsubscribe()
		    }
	    } else {
		    section("") {
			    paragraph title: "Click Done", "Please click Done to install app before continuing"
		    }
	    }
    }
}

def lockSubscribe(){
    unsubscribe()
    qryDevice.each {
        if(debugEnabled) log.debug "Subscribing to lock for $it"
        subscribe(it, "lock", "lockHandler")
    }
}

def lockHandler(evt){ 
    if(evt.value == "unlocked") {
        notifyDevice.each {
            it.deviceNotification(evt.descriptionText)
        }
    }
}

def lockHistory(){
    dynamicPage (name: "lockHistory", title: "", install: false, uninstall: false) {
        section("Lock History"){          
            dispTable = buildTable()
            section ("Lock Details", hideable: true, hidden: false) {  
                paragraph  "$dispTable"
                input  "codeRec", "bool", title:"Last Code Records", defaultValue: true, submitOnChange:true
                input  "unlockRec", "bool", title:"Unlock Records", defaultValue: true, submitOnChange: true
                input  "lockRec", "bool", title:"Lock Records", defaultValue: true, submitOnChange: true
                input "qryDate", "string", title: "Pull motion data from this date forward (yyyy-MM-dd hh:mm):", required: true, submitOnChange: true
                input "refreshTable", "button", title:"Refresh Table"
            }
            section (""){    
                href "mainPage", title: "Return", required: false
            }
      }
    }
}

String buildTable(){
    dispTable = "<style type='text/css'>div{overflow:auto;} .mtable {border:1px black solid;padding:0px;width:100%;}.mth {border:1px black solid; min-width:16em;} .mtd {border:1px black solid;vertical-align:text-top;}</style><div class='.mdiv'><table class='mtable'><tr>"
    evtList = {}
    evtArr = []
    for(i=0;i<qryDevice.size();i++){
        evtArr[i]=[]
        dName = qryDevice[i].label ?: qryDevice[i].name 
        dispTable+="<th class='mth'> $dName </th>"
        def evtList = [] 
        if(codeRec) evtList+=qryDevice[i].statesSince("lastCodeName",Date.parse("yyyy-MM-dd hh:mm", qryDate),[max:100])
        if(unlockRec || lockRec) evtList+=qryDevice[i].statesSince("lock",Date.parse("yyyy-MM-dd hh:mm", qryDate),[max:100])
        evtList.each {
            stateParts = parseState(it.toString())
            p4Trim = stateParts[4].trim()
            if (stateParts[0].length() < 23) stateParts[0] = stateParts[0] + "0"
            
            if (p4Trim == "unlocked" && unlockRec){
                tempStr="${stateParts[0]}\t\t   [Unlock Event]"
            } else if (p4Trim == "locked" && lockRec){
                tempStr="${stateParts[0]}\t\t   [Lock Event]"
            }else if (p4Trim != "locked" && p4Trim != "unlocked")
                tempStr="${stateParts[0]}\t [CodeName] ${stateParts[4]}"
                    
            if(tempStr != null) evtArr[i].add(tempStr)            
        }
        evtArr[i].sort()
    }
    dispTable += "</tr><tr>"
    for(i=0;i<qryDevice.size();i++){
        tempStr =""
        evtArr[i].each {
            tempStr+="$it \n"
        }                    
        dispTable += "<td class='mtd'>$tempStr</td>"
    }
    dispTable += "</tr></table></div>"
    return dispTable
}

def parseState(stateStr){ //returns array of the elements in the string [0] - Timestamp, [1] - Event ID, [2] - Event Name, [3] - Event Description, [4] - Event Value. [5] - Unit
    start = stateStr.indexOf('(')+1
    end = stateStr.length() - 1
    stateStr = stateStr.substring(start, end)
    return stateStr.split(',')
}

def appButtonHandler(btn) {
    switch(btn) {
        case "refreshTable":
            lockHistory()
            break
        default: 
            log.error "Undefined button $btn pushed"
            break
      }
}

def intialize() {

}