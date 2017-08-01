package warpi.service;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;
import warpi.model.ClosingPriceAtDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@Log4j
public class CsvProcessor {

    private static final String EXPECTED_HEADER = "Date,Open,High,Low,Close,Volume";
    private static final int INDEX_OF_DATE = 0;
    private static final int INDEX_OF_CLOSE_PRICE = 4;

    private static final FastDateFormat dateFormat = FastDateFormat.getInstance("dd-MMM-yy", TimeZone.getTimeZone("UTC"));

    public List<ClosingPriceAtDate> process(InputStream csv) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(csv));
            validateHeader(br);
            return process(br.lines());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                csv.close();
            } catch (IOException e) {
                log.error("Cannot close inputstream", e);
            }
        }
    }

    private void validateHeader(BufferedReader br) {
        try {
            String header = br.readLine();
            if (header==null) {
                throw new MalformedCsvException("The csv did not even contain header");
            }
            if ( ! EXPECTED_HEADER.equals(header)) {
                throw new MalformedCsvException(String.format("Unexpected header line: %1.100s", header));
            }
        } catch (IOException e) {
            throw new MalformedCsvException("Cannot read first line", e);
        }
    }

    private List<ClosingPriceAtDate> process(Stream<String> lines) {
        return lines.map(this::parseLine).filter(this::filterMonthEnd).collect(toList());
    }

    private ClosingPriceAtDate parseLine(String line) {
        String[] tokens = line.split(",");
        if (tokens.length != 6) {
            throw new MalformedCsvException(String.format("Unexpected number of columns: '%1.100s'", line));
        }
        try {
            Date date = dateFormat.parse(tokens[INDEX_OF_DATE]);
            Double closePrice = Double.parseDouble(tokens[INDEX_OF_CLOSE_PRICE]);
            return new ClosingPriceAtDate(date, closePrice);
        } catch (ParseException e) {
            throw new MalformedCsvException(String.format("Illegal date: %1.100s", tokens[INDEX_OF_DATE]));
        } catch (NumberFormatException n) {
            throw new MalformedCsvException(String.format("Illegal closing price: '%1.100s'", tokens[INDEX_OF_CLOSE_PRICE]));
        }
    }

    private boolean filterMonthEnd(ClosingPriceAtDate closingPriceAtDate) {
        return true;
    }
}
