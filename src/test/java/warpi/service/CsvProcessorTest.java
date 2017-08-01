package warpi.service;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import warpi.model.ClosingPriceAtDate;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CsvProcessorTest {

    @Test
    public void closesInputStream() {
        InputStream inputStream = IOUtils.toInputStream("\uFEFFDate,Open,High,Low,Close,Volume\n");
        AtomicBoolean isClosed = new AtomicBoolean(false);
        InputStream inputStreamCheckingClosed = new InputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
            @Override
            public void close() throws IOException {
                isClosed.set(true);
                inputStream.close();
            }
        };

        CsvProcessor csvProcessor = new CsvProcessor();
        csvProcessor.process(inputStreamCheckingClosed);
        assertTrue("Inputstream should be closed by the processor", isClosed.get());
    }

    @Test
    public void emptyCsvThrowsException() {
        InputStream inputStream = IOUtils.toInputStream("");
        CsvProcessor csvProcessor = new CsvProcessor();
        try {
            List<ClosingPriceAtDate> results = csvProcessor.process(inputStream);
        } catch(MalformedCsvException e) {
            return;
        }
        fail("Expected a MalformedCsvException");
    }

    @Test
    public void csvWithOnlyHeaderResultsInEmptyList() {
        List<ClosingPriceAtDate> results = testRun();
        assertEquals(0, results.size());
    }

    @Test
    public void singleMonthEndLineIsIngored() {
        List<ClosingPriceAtDate> results = testRun(
                "30-Jun-17,242.28,242.71,241.58,241.80,86820694"
        );
        assertEquals(0, results.size());
    }

    @Test
    public void twoMonthsRetrunsTheEarlier() {
        List<ClosingPriceAtDate> results = testRun(
                "30-Jun-17,242.28,242.71,241.58,241.80,86820694",
                "31-May-17,241.84,241.88,240.64,241.44,91796016"
        );
        assertEquals(1, results.size());
        assertEquals(new ClosingPriceAtDate(date("2017-05-31"), 241.44), results.get(0));
    }

    @Test
    public void multipleMonthEndLines() {
        List<ClosingPriceAtDate> results = testRun(
                "31-Jul-17,-,-,-,246.77,63400880",
                "5-Jul-17,242.63,243.01,241.70,242.77,54427596",
                "3-Jul-17,242.88,243.38,242.21,242.21,39153806",
                "30-Jun-17,242.28,242.71,241.58,241.80,86820694",
                "29-Jun-17,243.66,243.72,239.96,241.35,106949719",
                "28-Jun-17,242.50,243.72,242.23,243.49,70042599",
                "14-Jun-17,244.86,244.87,243.29,244.24,78602311",
                "31-May-17,241.84,241.88,240.64,241.44,91796016",
                "23-May-17,239.95,240.24,239.51,240.05,48341683",
                "28-Apr-17,238.90,238.93,237.93,238.08,63532845"
        );
        assertEquals(Arrays.asList(
                new ClosingPriceAtDate(date("2017-06-30"), 241.80),
                new ClosingPriceAtDate(date("2017-05-31"), 241.44),
                new ClosingPriceAtDate(date("2017-04-28"), 238.08)
                ), results);
    }

    private static List<ClosingPriceAtDate> testRun(String ... lines) {
        InputStream inputStream = IOUtils.toInputStream(
                "\uFEFFDate,Open,High,Low,Close,Volume\n"
                + String.join("\n", lines));
        CsvProcessor csvProcessor = new CsvProcessor();
        return csvProcessor.process(inputStream);
    }

    private static Date date(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date literal: " + dateString);
        }
    }
}