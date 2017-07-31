package warpi.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/historical/v1")
public class HistoricalController {

    @RequestMapping(method = RequestMethod.GET,
            value = "cids/{cid}/month-end-closing-prices",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> monthEndClosingPrices(@PathVariable Long cid) {
        return Arrays.asList(cid.toString(), "is", "there");
    }
}
