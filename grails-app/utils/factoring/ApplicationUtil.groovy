package factoring

import org.grails.web.json.JSONObject

class ApplicationUtil {



    /**
     * this function take string date like "23-01-2015"("dd-MM-yyyy") as parameter
     * and return "150123"(yyMMdd)
     */
    def convertDateToDBFormat(def dateData){
        def temp = dateData.split("-")
        def tempDate= temp[0]
        def tempMonth= temp[1]
        def tempYear= temp[2]
        def mainStr=tempYear.substring(2, 4)+tempMonth+tempDate
        return mainStr
    }

    /**
     * this function take string date like "23-01-2015"("dd-MM-yyyy") as parameter
     * and return "2015-01-23"(yyyy-MM-dd)
     */
    def convertDateFormat(def dateData){
        def temp = dateData.split("-")
        def tempDate = temp[0]
        def tempMonth = temp[1]
        def tempYear = temp[2]
        def mainStr = tempYear + "-" + tempMonth + "-" + tempDate
        return mainStr
    }

    /**
     * this function take string date like "150123"(yyMMdd)  as parameter
     * and return "23-01-2015"("dd-MM-yyyy")
     */
    def convertDateToDisplayFormat(def dateData){
        int nLen = dateData.length()
        if(nLen < 6) return 'Invalid Date'

        String tempYear = dateData.substring(0,2)
        StringBuilder sbYear = new StringBuilder(tempYear)
        sbYear.insert(0, '20')
        tempYear = sbYear.toString()
        println("year : "+tempYear)

        def tempMonth = dateData.substring(2,4)
        def tempDay = dateData.substring(4,6)
        def tempDate = tempDay + "-" + tempMonth + "-" + tempYear

        return tempDate
    }

    def convertDateFromMonthAndYear(def month,def year){

        String day = "1"
        String formattedDate = year + "-" + month + "-0" + day + " 00:00:00"

        return formattedDate
    }

    def static generateRandomString() {

        // A strong password has Cap_chars, Lower_chars,
        // numeric value and symbols. So we are using all of
        // them to generate our password
        String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        String Small_chars = "abcdefghijklmnopqrstuvwxyz"
        String numbers = "0123456789"


        String values = numbers + Capital_chars + Small_chars

        // Using random method
        Random rndm_method = new Random()
        int len = 10

        char[] password = new char[len]

        for (int i = 0; i < len; i++)
        {
            password[i] = values.charAt(rndm_method.nextInt(values.length()))
        }
        return password.toString()
    }

    static String findDifferenceKeyValue(Map submitted, Map dbMap) {

        Map changedMap = [:]
        String parameters = null

        for (String key : submitted.keySet()) {
            if(!submitted.get(key).equals(dbMap.get(key))){
                changedMap.put(key,submitted.get(key))
            }
        }

        JSONObject json = new JSONObject()
        json.putAll(changedMap)

        if(json.size() > 0)
            parameters = json.toString()

        return parameters
    }

    static String mapToJsonConvert(Map submitted) {

        JSONObject json = new JSONObject()
        json.putAll(submitted)

        return json.toString()
    }

    static boolean is_Valid_Password(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;

        if (str.length() < 7) return false

        for(int i=0;i < str.length();i++) {
            ch = str.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

}
