/*
 * Web Scraper Device
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
 *    Date         Who           What
 *    ----         ---           ----
 *    22Mar2022    thebearmay    original code  
*/


@SuppressWarnings('unused')
static String version() {return "0.0.1"}

metadata {
    definition (
        name: "Web Scraper", 
        namespace: "thebearmay", 
        author: "Jean P. May, Jr.",
        description: "Scrapes a website looking for a search string and returns a string based on offsets from the search",
        importUrl:"https://raw.githubusercontent.com/thebearmay/hubitat/main/webScraper.groovy"
    ) {
 
        capability "Actuator"
        
        attribute "successful", "STRING"
        attribute "textReturned", "STRING"
        attribute "lastURL", "STRING"
        attribute "lastSearch", "STRING"
        attribute "offsets", "STRING"
 
        command "scrape",[[name:"inputURL*", type:"STRING", description:"Input URL"],
                          [name:"searchStr*", type:"STRING", description:"String to look for"],
                          [name:"retBegOffset", type:"NUMBER", description: "Beginning offset from Found Item to Return Data"],
                          [name:"retEndOffset", type:"NUMBER", description: "Ending offset from Found Item to Return Data"]
                         ]

    }   
}

preferences {
    input("debugEnabled", "bool", title: "Enable debug logging?")
    input("pollRate", "number", title: "Poll Rate in minutes (0 = No Polling)", defaultValue:0)
 
}

@SuppressWarnings('unused')
def installed() {

}
@SuppressWarnings('unused')
def updateAttr(String aKey, aValue, String aUnit = ""){
    if(aValue.length() > 1024) aValue = aValue.substring(0,1023)
    sendEvent(name:aKey, value:aValue, unit:aUnit)
}


void updated(){
    if(debugEnabled) {
        log.debug "updated()"
        runIn(1800,"logsOff")
    } 
    if(pollRate > 0) 
        runIn(pollRate*60, "scrape")
    else
        unschedule("scrape")
}

void scrape() {
    log.error "No Parameters Passed"
}

void scrape (url, searchStr, beg=0, end=1){
    if(debugEnabled) log.debug "$url, /$searchStr/, $beg, $end"
    updateAttr("lastURL", url)
    updateAttr("lastSearch",searchStr)
    ofList = [beg, end]
    updateAttr("offsets", ofList.toString())
    updateAttr("successful","running")
    updateAttr("textReturned","null")
    dataRet = readExtFile(url).toString()
    if(debugEnabled) "${dataRet.length()} characters returned"
    found = dataRet.indexOf(searchStr)
    if(found == -1) {
        updateAttr("successful","false")
        updateAttr("textReturned","null")
        if(pollRate > 0) 
            runIn(pollRate*60, "scrape")
        if(debugEnabled) log.deubg "Not Found"
        return
    }
    updateAttr("successful", "true")
    int begin = found+beg
    int ending = found+end
    updateAttr("textReturned",dataRet.substring(begin,ending))
    if(debugEnabled) "Found at $found"
    if(pollRate > 0) 
        runIn(pollRate*60, "scrape")
    return
                   
}

String readExtFile(fName){
    def params = [
        uri: fName,
        contentType: "text/html",
        textParser: true
    ]

    try {
        httpGet(params) { resp ->
            if(resp!= null) {
               int i = 0
               String delim = ""
               i = resp.data.read() 
               while (i != -1){
                       char c =  (char) i
                       delim+=c
                       i = resp.data.read()
                       if(i < 0 || i > 255) return delim
               } 
               if(debugEnabled) log.info "Read External File result: delim"
               return delim.toString()
            }
            else {
                log.error "Null Response"
                return null
            }
        }
    } catch (exception) {
        log.error "Read Ext Error: ${exception.message}"
        return null;
    }
}

@SuppressWarnings('unused')
void logsOff(){
     device.updateSetting("debugEnabled",[value:"false",type:"bool"])
}