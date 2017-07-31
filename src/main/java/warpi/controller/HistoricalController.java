package warpi.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import warpi.model.ClosingPriceAtDate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping(value = "/historical/v1")
public class HistoricalController {

    @RequestMapping(method = RequestMethod.GET,
            value = "cids/{cid}/month-end-closing-prices",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClosingPriceAtDate> monthEndClosingPrices(@PathVariable Long cid) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        ClosingPriceAtDate closingPriceAtDate =
                new ClosingPriceAtDate(format.parse("2017-08-01"), 3.14);
        return Arrays.asList(closingPriceAtDate);
    }
}
