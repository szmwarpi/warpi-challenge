package warpi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.util.Date;

@Value
public class ClosingPriceAtDate {

    //TODO: we might be wasting CPU cycles with deserialization/serialization,
    //  measure if this is the case (most probably not), and refactor into string manipulation

    @JsonFormat(pattern = "MM-dd-YYYY")
    private final Date date;

    private final Double close;
}
