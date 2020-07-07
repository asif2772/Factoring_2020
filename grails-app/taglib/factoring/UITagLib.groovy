package factoring

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UITagLib {
    static namespace = "bv"

   /* def datePicker = { attrs, body ->
        def name = attrs.name
        def value = attrs.value
        if (value) {
            //def d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(value)

            *//*LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String text = date.format(formatter);
            LocalDate currentDate = LocalDate.parse(text, formatter);*//*

            value = new SimpleDateFormat("dd-MM-yyyy").format(value)
        } else {
            value = ""
        }

        //def r = grailsApplication.mainContext.getBean('org.grails.plugin.resource.ResourceTagLib')
        //r.require(module:"jquery-ui")

        String id = "date_picker_" + name
        StringBuilder builder = new StringBuilder();
        builder.append('<input type="text"')
                .append('required=""')
                .append('name="').append(name).append('"')
                .append('id="' + id + '" value="')
                .append(value).append('"/>')
        StringBuilder jsbuilder = new StringBuilder();

        jsbuilder.append(' <script type="text/javascript">')
                .append(' $(document).ready(function(){')
                .append(' $("#' + id + '").datepicker({dateFormat: "dd-mm-yy"});')
                .append(' })')
                .append('</script>')
        def headerWriter = getHeaderWriter()
        headerWriter << jsbuilder
        out << builder.toString()
    }*/

    def datePickerEdit = { attrs, body ->
        def name = attrs.name
        def value = attrs.value
        if (value) {
            //def d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(value)
            //value = new SimpleDateFormat("dd-MM-yyyy").format(value)
            value = new SimpleDateFormat("dd-MM-yyyy").format(value)

        } else {
            value = ""
        }

        String id = "date_picker_" + name
        StringBuilder builder = new StringBuilder();
        builder.append('<input type="text"')
                .append('name="').append(name).append('"')
                .append('id="' + id + '" value="')
                .append(value).append('"/>')

        out << builder.toString()
    }

    /*def buttonDropDown = { attrs, body ->
        def id = attrs.id


        def r = grailsApplication.mainContext.getBean('org.grails.plugin.resource.ResourceTagLib')
        r.require(module: "jquery-ui")


        StringBuilder builder = new StringBuilder();
        builder.append('<button ')
                .append('id="' + id + '">')
                .append(body).append('</button>')
        StringBuilder jsbuilder = new StringBuilder();
        jsbuilder.append(' <script type="text/javascript">')
                .append(' $(document).ready(function(){')
                .append(' $("#' + id + '").button({icons: {primary: "ui-icon-locked"},text: true);')
                .append(' });')
                .append('</script>')

        def headerWriter = getHeaderWriter()
        headerWriter << jsbuilder
        out << builder.toString()
    }*/

    /*private getHeaderWriter() {
        def gspSitemeshPage = request[GrailsPageFilter.GSP_SITEMESH_PAGE]
        def headBuffer = gspSitemeshPage.metaClass.getProperty(gspSitemeshPage, 'headBuffer')
        return headBuffer.writer
    }*/
}
