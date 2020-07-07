import org.grails.plugins.web.taglib.ValidationTagLib

import java.text.DecimalFormat

class CoreParamsTagLib {



    static namespace = "bv"

    def statusDropDown = {attrs, body ->
        String name = attrs.name
        String selectedValue = attrs.selectedValue
        String allowEmpty = attrs.allowEmpty
        def g=new ValidationTagLib()
        String dropDown = "<select name='"+name+"'>"
        if(allowEmpty == 'true'){
            if(selectedValue==""){
                dropDown+="<option value='0' selected='selected'>"+g.message(code: 'bv.undoReconciliation.Select.label')+"</option>"
            }else{
                dropDown+="<option value='0'>"+g.message(code: 'bv.undoReconciliation.Select.label')+"</option>"
            }
        }

        if (selectedValue=='1'){
            dropDown+="<option value='1' selected='selected'>"+g.message(code: 'coreParamsTagLib.active.label')+"</option>"
        }else{
            dropDown+="<option value='1'>"+g.message(code: 'coreParamsTagLib.active.label')+"</option>"
        }

        if (selectedValue=='2'){
            dropDown+="<option value='2' selected='selected'>"+g.message(code: 'coreParamsTagLib.inactive.label')+"</option>"
        }else{
            dropDown+="<option value='2'>"+g.message(code: 'coreParamsTagLib.inactive.label')+"</option>"
        }
        dropDown += "</select>"

        out << dropDown

    }

    def statusDisplayName = {attrs, body ->
        String statusVale = attrs.statusVale

        def showVal
        def g=new ValidationTagLib()
        if(statusVale=='1'){
            showVal=g.message(code: 'coreParamsTagLib.active.label');
        }else if(statusVale=='2'){
            showVal=g.message(code: 'coreParamsTagLib.inactive.label');
        }else if(statusVale=='-2'){
            showVal=g.message(code: 'default.button.delete.label');
        }else{
            showVal=""
        }

        out << showVal

    }

    def dotCommaDecimalFormat = {attrs, body ->
        def decimalData = attrs.decimalData
        def isNegative = attrs.isNegative
        DecimalFormat formatter = new DecimalFormat("#,##0.00")
        def formattedData = formatter.format(decimalData)
        formattedData = formattedData.replace(".","-")
        formattedData = formattedData.replaceAll(",",".")
        formattedData = formattedData.replace("-",",")
        if(isNegative)
            formattedData = "-" + formattedData
        out << formattedData
    }

    def euroDotCommaDecimalFormat = {attrs, body ->
        def decimalData = attrs.decimalData
        DecimalFormat formatter = new DecimalFormat("€ #,##0.00;€ -#,##0.00")
        def formattedData = formatter.format(decimalData)
        formattedData = formattedData.replace(".",":")
        formattedData = formattedData.replaceAll(",",".")
        formattedData = formattedData.replace(":",",")
        out << formattedData
    }

    def roundingValuesToTwoDigits = {attrs, body ->
        def value = attrs.decimalData
        def formattedData =  new DecimalFormat("#.##").format(value)
        out << formattedData
    }

    def paramDropDown = {attrs, body ->
        String paramName = attrs.paramName
        String varName = attrs.varName
        String selectedValue = attrs.selectedValue

        if (varName=='BUDGET_FREQUENCE'||varName=='INVOICE_FREQUENCE'){

            ArrayList mapStatuseArr = new ArrayList()
            def g=new ValidationTagLib()
            mapStatuseArr = [
                    ['monthly', g.message(code: 'budgetFrequency.monthly.label')],
                    ['two_monthly', g.message(code: 'budgetFrequency.twoMonthly.label')],
                    ['quarterly', g.message(code: 'budgetFrequency.quarterly.label')],
                    ['twice_a_year', g.message(code: 'budgetFrequency.twiceYear.label')],
                    ['yearly', g.message(code: 'budgetFrequency.yearly.label')],]

            String dropDown = "<select id='"+paramName+"' name='"+paramName+"'>"

            for (int i = 0; i < mapStatuseArr.size(); i++) {
                if (mapStatuseArr[i][0].toString() == selectedValue) {
                    dropDown += "<option value='" + mapStatuseArr[i][0] + "' selected='selected'>" + mapStatuseArr[i][1] +  "</option>"
                } else {
                    dropDown += "<option value='" + mapStatuseArr[i][0] + "'>" + mapStatuseArr[i][1] + "</option>"
                }
            }

            dropDown += "</select>"
            out << dropDown
        }

        else{

            String DataInstance= CoreParams.findByVarName(varName)
            def firstArr=DataInstance.split("::")
            String valArr
            Integer indexPos
            String strIndex
            Integer varLength
            String strValue
            String dropdown = "<select id='"+paramName+"' name='"+paramName+"'>"
            firstArr.each {phn ->
                indexPos = phn.lastIndexOf('{');
                strIndex= phn.substring(0,indexPos)
                varLength= phn.length();
                strValue= phn.substring(indexPos+2,varLength-2)
                if (strValue==selectedValue){
                    dropdown+="<option value='"+strValue+"' selected='selected'>"+strIndex+"</option>"
                }else{
                    dropdown+="<option value='"+strValue+"'>"+strIndex+"</option>"
                }
            }
            dropdown += "</select>"
            out << dropdown
        }
    }

    def integerDropDown = {attrs, body ->
        String name = attrs.name
        int selectedValue = attrs.selectedValue? attrs.selectedValue : 0
        boolean allowEmpty = attrs.allowEmpty ? Boolean.parseBoolean(attrs.allowEmpty)  : false
        int startValue = attrs.startValue   ? Integer.parseInt(attrs.startValue) : 0
        int endValue   = attrs.endValue ? Integer.parseInt(attrs.endValue) : 0
        int interval   = attrs.interval ? Integer.parseInt(attrs.interval) : 0

        String dropDown = "<select name='"+name+"'>"
        if(allowEmpty){
            if(selectedValue==0){
                dropDown+="<option value='0' selected='selected'>Select</option>"
            }else{
                dropDown+="<option value='0'>Select</option>"
            }
        }
        def i=0
        for (i = startValue; i < endValue; i=i+interval) {
            if(i==selectedValue){
                dropDown+="<option value='"+i+"' selected='selected'>"+i+"</option>"
            }else{
                dropDown+="<option value='"+i+"'>"+i+"</option>"
            }
        }
        dropDown += "</select>"
        out << dropDown

    }

    def currencyDropDown = {attrs, body ->
        String name = attrs.name
        String selectedValue = attrs.selectedValue

        def listArr = Currencies.findAll("from Currencies where status=1")

        String dropDown = "<select name='"+name+"'>"

        if(listArr.size()){
            for(int i=0;i<listArr.size();i++){
                if (listArr[i].currCode==selectedValue){
                    dropDown+="<option value='"+listArr[i].currCode+"' selected='selected'>"+listArr[i].currency+" ["+listArr[i].currSymbol+"]"+"</option>"
                }else{
                    dropDown+="<option value='"+listArr[i].currCode+"'>"+listArr[i].currency+" ["+listArr[i].currSymbol+"]"+"</option>"
                }
            }
        }
        dropDown += "</select>"

        out << dropDown

    }

    def customQueryDropDown = {attrs, body ->
        String name = attrs.name
        String domainName = attrs.domainName
        String selectedValue = attrs.selectedValue
        boolean allowEmpty = attrs.allowEmpty ? Boolean.parseBoolean(attrs.allowEmpty)  : false
        String optionFieldName = attrs.optionFieldName
        String valueFieldName = attrs.valueFieldName
        String whereClause = attrs.whereClause

        def domain =  grailsApplication.getDomainClass(domainName).newInstance()

        def tableName = domain.getClass().getName()

        def where=""
        if(whereClause){
            where=" WHERE " + whereClause
        }
        //def listArr = domain.executeQuery("SELECT "+valueFieldName+" As valuename,"+optionFieldName+" As optionname FROM "+ tableName + where)
        def listArr = domain.executeQuery("SELECT "+valueFieldName+" As valuename,"+optionFieldName+" As optionname FROM "+ tableName + where)

        String[] strValue

        String dropDown = "<select name='"+name+"'>"
        if(allowEmpty){
            if(selectedValue==''){
                dropDown+="<option value='' selected='selected'>"+g.message(code: 'bv.undoReconciliation.Select.label')+"</option>"
            }else{
                dropDown+="<option value=''>"+g.message(code: 'bv.undoReconciliation.Select.label')+"</option>"
            }
        }
        if(listArr.size()){
            for(int i=0;i<listArr.size();i++){
                strValue=listArr[i];
                if (strValue[0]==selectedValue){
                    dropDown+="<option value='"+strValue[0]+"' selected='selected'>"+strValue[1]+"</option>"
                }else{
                    dropDown+="<option value='"+strValue[0]+"'>"+strValue[1]+"</option>"
                }
            }
        }
        dropDown += "</select>"
        out << dropDown
    }

    def getGeneratedBudgetExpanseCode() {
        def PrefixDataArr = SystemPrefix.executeQuery("SELECT prefix,prefixLen FROM bv.SystemPrefix where id=6")
        def Prefix = PrefixDataArr[0][0]
        def PrefixLength = PrefixDataArr[0][1]
        ///////////////////MAX VENDOR ID/////
        def VendorDataArr = BudgetItemExpense.executeQuery("SELECT MAX(id) FROM bv.BudgetItemExpense")

        def newSequence
        if (VendorDataArr[0]) {
            newSequence = VendorDataArr[0] + 1
        } else {
            newSequence = 1
        }
        def VendorCode
        if (PrefixLength == 2) {VendorCode = String.format("%02d", newSequence)}
        else if (PrefixLength == 3) {VendorCode = String.format("%03d", newSequence)}
        else if (PrefixLength == 4) {VendorCode = String.format("%04d", newSequence)}
        else if (PrefixLength == 5) {VendorCode = String.format("%05d", newSequence)}
        else if (PrefixLength == 6) {VendorCode = String.format("%06d", newSequence)}
        else if (PrefixLength == 7) {VendorCode = String.format("%07d", newSequence)}
        else if (PrefixLength == 8) {VendorCode = String.format("%08d", newSequence)}
        else if (PrefixLength == 9) {VendorCode = String.format("%09d", newSequence)}

        return VendorCode
    }


    def creditDebitStatus = {attrs, body ->
        String name = attrs.name
        String selectedValue = attrs.selectedValue
        String allowEmpty = attrs.allowEmpty
        def g=new ValidationTagLib()

        String dropDown = "<select style='width:110px' name='"+name+"'>"
        if(allowEmpty == 'true'){
            if(selectedValue==""){
                dropDown+="<option value='0' selected='selected'>"+g.message(code: 'bv.undoReconciliation.Select.label')+"</option>"
            }else{
                dropDown+="<option value='0'>"+g.message(code: 'bv.undoReconciliation.Select.label')+"</option>"
            }
        }
        if (selectedValue=='D'){
            dropDown+="<option value='D' selected='selected'>"+g.message(code: 'bv.saveManualEntryOfBankStatementDebit.label')+"</option>"
        }else{
            dropDown+="<option value='D'>"+g.message(code: 'bv.saveManualEntryOfBankStatementDebit.label')+"</option>"
        }
        if (selectedValue=='C'){
            dropDown+="<option value='C' selected='selected'>"+g.message(code: 'bv.saveManualEntryOfBankStatementCredit.label')+"</option>"
        }else{
            dropDown+="<option value='C'>"+g.message(code: 'bv.saveManualEntryOfBankStatementCredit.label')+"</option>"
        }
        dropDown += "</select>"
        out << dropDown

    }


}
