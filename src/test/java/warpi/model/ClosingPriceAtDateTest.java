package warpi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class ClosingPriceAtDateTest {

    @Test
    public void serializesIntoExpectedJsonFormat() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        ClosingPriceAtDate closingPriceAtDate =
                new ClosingPriceAtDate(format.parse("2017-08-01"), 3.14);

        String json = mapper.writeValueAsString(closingPriceAtDate);

        assertEquals("{\"date\":\"08-01-2017\",\"close\":3.14}", json);
    }

}