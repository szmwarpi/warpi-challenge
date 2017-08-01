package warpi.service;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;

@Service
@Log4j
public class CsvFetcher {

    public InputStream fetchCsvFor(Long cid) {
        String urlString = "http://www.google.com/finance/historical?startdate=Jan+1%2C+1990&output=csv&cid=" + cid;
        try {
            URL url = new URL(urlString);
            //TODO: fine-tune timeouts, headers sent, error handling
            return url.openStream();
        } catch (IOException e) {
            log.error("Error while fetching " + urlString, e);
            throw new UncheckedIOException(e);
        }
    }
}
