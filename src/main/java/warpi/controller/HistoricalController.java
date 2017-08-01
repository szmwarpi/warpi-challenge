package warpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import warpi.model.ClosingPriceAtDate;
import warpi.service.CsvFetcher;
import warpi.service.CsvProcessor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping(value = "/historical/v1")
public class HistoricalController {

    private final CsvFetcher csvFetcher;

    private final CsvProcessor csvProcessor;

    @Autowired
    public HistoricalController(CsvFetcher csvFetcher, CsvProcessor csvProcessor) {
        this.csvFetcher = csvFetcher;
        this.csvProcessor = csvProcessor;
    }


    @RequestMapping(method = RequestMethod.GET,
            value = "cids/{cid}/month-end-closing-prices",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClosingPriceAtDate> monthEndClosingPrices(@PathVariable Long cid) throws Exception {

        return csvProcessor.process(csvFetcher.fetchCsvFor(cid));

    }
}
