package uk.nhs.digital.gossmigrator.misc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.enums.DateFormatEnum;
import uk.nhs.digital.gossmigrator.model.goss.enums.GossExportFieldNames;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GossExportHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(GossExportHelper.class);

    public static Long getLong(JSONObject gossJson, GossExportFieldNames fieldName, long gossId) {
        Object value = gossJson.get(fieldName.getName());

        if (value instanceof Long) {
            return (Long) value;
        } else {
            LOGGER.warn("Goss Id:{}, Field:{}, Value present:{}. Expected Long value, but was not there. "
                    , gossId, fieldName, value.toString());
            return null;
        }
    }

    public static List<Long> getLongList(JSONObject gossJson, GossExportFieldNames fieldName, long gossId) {
        List<Long> ids = new ArrayList<>();
        Object value = gossJson.get(fieldName.getName());
        String[] idStrings = ((String)value).split(",");

        for(int i = 0; i < idStrings.length; i++){
            try{
                ids.add(Long.parseLong(idStrings[i]));
            }catch (NumberFormatException e){
                LOGGER.warn("Goss Id:{}, Field:{}, Value present:{}. Expected Long value, but was not there. "
                        , gossId, fieldName, value.toString());
            }
        }
        return ids;
    }

    public static Long getIdOrError(JSONObject gossJson, GossExportFieldNames fieldName) {
        Object value = gossJson.get(fieldName.getName());

        if (value instanceof Long) {
            return (Long) value;
        } else {
            throw new RuntimeException("Missing Id.  Cannot process");
        }
    }

    /**
     * Gets String value of a node in Goss export Json record.
     *
     * @param gossJson  Goss content Json.
     * @param fieldName Node name to parse.
     * @param gossId    Goss article id.  For logging.
     * @return Node value.
     */
    public static String getString(JSONObject gossJson, GossExportFieldNames fieldName, long gossId) {
        Object nodeValue = gossJson.get(fieldName.getName());
        if (nodeValue instanceof String) {
            String stringValue = (String) nodeValue;
            if(fieldName.isMandatory() && StringUtils.isEmpty(stringValue)){
                LOGGER.error("Goss Id:{}, Field:{}. Expected value.  Was Empty.", gossId, fieldName);
            }
            return stringValue;
        } else {
            LOGGER.error("Goss Id:{}, FieldName:{}, Value:{}. Expected String.  Got something else."
                    , gossId, fieldName, nodeValue);
        }
        return null;
    }

    /**
     * Parses a date String in Goss export format into java Date.
     *
     * @param gossJson  Raw goss export Json.
     * @param fieldName Node in Json to parse.
     * @param gossId    Goss Article Id of row. For logging.
     * @return The parsed date.
     */
    public static Date getDate(JSONObject gossJson, GossExportFieldNames fieldName, long gossId, DateFormatEnum dateFormatEnum) {
        // May, 23 2016 16:03:33 +0100 (Goss date example)
        String date = (String) gossJson.get(fieldName.getName());
        Date d = null;
        try {
            if(StringUtils.isNotEmpty(date)){
                d = DateUtils.parseDate(date, dateFormatEnum.getFormat());
            }
        } catch (ParseException e) {
            LOGGER.error("Goss Id:{}, Field:{}, Value:{}. Could not parse date.", gossId, fieldName, date);
        }
        return d;
    }

    public static String getDateString(Date date, DateFormatEnum dateFormatEnum){
        String dateString = "";

        if(date != null){
            DateFormat df = new SimpleDateFormat(dateFormatEnum.getFormat());
            dateString = df.format(date);
        }

        return dateString;
    }





    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean getBoolean(JSONObject gossJson, GossExportFieldNames fieldName, boolean defaultValue) {
        String value = (String) gossJson.get(fieldName.getName());
        if ("true".equals(value))
            return true;
        if ("false".equals(value))
            return false;
        return defaultValue;
    }

    public static boolean isImage(String file) {
        return (file.endsWith(".gif") || file.endsWith(".jpeg") || file.endsWith(".jpg")
                || file.endsWith(".png") || file.endsWith(".svg"));
    }

    public static boolean isSupportedAsset(String file) {
        return (file.endsWith(".doc") || file.endsWith(".docx") || file.endsWith(".xls")
                || file.endsWith(".xlsx") || file.endsWith(".pdf") || file.endsWith(".csv")
                || file.endsWith(".zip") || file.endsWith(".txt") || file.endsWith(".rar")
                || file.endsWith(".ppt") || file.endsWith(".pptx"));
    }

}
