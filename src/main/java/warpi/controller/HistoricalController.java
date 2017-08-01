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

import java.util.List;

@RestController
@RequestMapping(value = "/historical/v1")
public class HistoricalController {

    private final CsvFetcher csvFetcher;

    private final CsvProcessor csvProcessor;

    private final List<Long> acceptedCids;

    @Autowired
    public HistoricalController(CsvFetcher csvFetcher, CsvProcessor csvProcessor, List<Long> acceptedCids) {
        this.csvFetcher = csvFetcher;
        this.csvProcessor = csvProcessor;
        this.acceptedCids = acceptedCids;
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "cids",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Long> getAcceptedCids() {
        return acceptedCids;
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "cids/{cid}/month-end-closing-prices",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClosingPriceAtDate> monthEndClosingPrices(@PathVariable Long cid) throws Exception {
        if ( ! acceptedCids.contains(cid)) {
            throw new Exception("This is not an accepted CID: '"+cid+"'. Use the /cids endpoint to get a list of possibilities");
        }
        return csvProcessor.process(csvFetcher.fetchCsvFor(cid));
    }
}
