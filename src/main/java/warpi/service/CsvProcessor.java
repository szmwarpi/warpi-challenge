package warpi.service;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import warpi.model.ClosingPriceAtDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@Log4j
public class CsvProcessor {

    public List<ClosingPriceAtDate> process(InputStream csv) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(csv));
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

    private List<ClosingPriceAtDate> process(Stream<String> lines) {
        return lines.map(this::parseLine).filter(this::filterMonthEnd).collect(toList());
    }

    private ClosingPriceAtDate parseLine(String line) {
        return null;
    }

    private boolean filterMonthEnd(ClosingPriceAtDate closingPriceAtDate) {
        return true;
    }
}
