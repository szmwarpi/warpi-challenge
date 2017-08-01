package warpi.service;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;
import warpi.model.ClosingPriceAtDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

@Service
@Log4j
public class CsvProcessor {

    private static final String EXPECTED_HEADER = "Date,Open,High,Low,Close,Volume";
    private static final int INDEX_OF_DATE = 0;
    private static final int INDEX_OF_CLOSE_PRICE = 4;

    private static final FastDateFormat dateFormat = FastDateFormat.getInstance("dd-MMM-yy", TimeZone.getTimeZone("UTC"));

    public List<ClosingPriceAtDate> process(InputStream csv) {
        return filterForMonthEnds(extactAllDataPoints(csv));
    }

    private List<ClosingPriceAtDate> extactAllDataPoints(InputStream csv) {
        BufferedReader br = null;
        try {

            br = new BufferedReader(new InputStreamReader(csv));

            validateHeader(br);

            List<ClosingPriceAtDate> allDataPoints = br.lines().map(this::parseLine).collect(toList());

            return allDataPoints;

        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(csv);
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

    private List<ClosingPriceAtDate> filterForMonthEnds(List<ClosingPriceAtDate> dataPoints) {

        dataPoints.sort(comparing(ClosingPriceAtDate::getDate, reverseOrder()));

        ArrayList<ClosingPriceAtDate> result = new ArrayList<>();
        Integer monthLastSeen = null;
        for (ClosingPriceAtDate dataPoint : dataPoints) {
            int month = getMonth(dataPoint);
            if (monthLastSeen == null) {
                monthLastSeen = month;
            } else if ( ! monthLastSeen.equals(month)) {
                result.add(dataPoint);
                monthLastSeen = month;
            }
        }
        return result;
    }

    private Integer getMonth(ClosingPriceAtDate dataPoint) {
        return dataPoint.getDate().getMonth();
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

}
