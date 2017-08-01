package warpi.service;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import warpi.model.ClosingPriceAtDate;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        InputStream inputStream = IOUtils.toInputStream("Date,Open,High,Low,Close,Volume\n");
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
    public void singleMonthEndLine() {
        List<ClosingPriceAtDate> results = testRun(
                "30-Jun-17,242.28,242.71,241.58,241.80,86820694"
        );
        assertEquals(1, results.size());
        assertEquals(new ClosingPriceAtDate(date("2017-06-30"), 241.80), results.get(0));
    }


    private static List<ClosingPriceAtDate> testRun(String ... lines) {
        InputStream inputStream = IOUtils.toInputStream(
                "Date,Open,High,Low,Close,Volume\n"
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