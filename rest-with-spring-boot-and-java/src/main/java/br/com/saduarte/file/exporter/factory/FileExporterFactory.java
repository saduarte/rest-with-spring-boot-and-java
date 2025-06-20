package br.com.saduarte.file.exporter.factory;

import br.com.saduarte.exception.BadRequestException;
import br.com.saduarte.file.exporter.MediaTypes;
import br.com.saduarte.file.exporter.contract.FileExporter;
import br.com.saduarte.file.exporter.impl.CsvExporter;
import br.com.saduarte.file.exporter.impl.PdfExporter;
import br.com.saduarte.file.exporter.impl.XlsxExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class FileExporterFactory {

    @Autowired
    private ApplicationContext context;

    public FileExporter getExporter(String acceptHeader) throws Exception{

        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)){
            return context.getBean(XlsxExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
            return context.getBean(CsvExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
            return context.getBean(PdfExporter.class);
        } else {
            throw new BadRequestException("Invalid file format");
        }
    }

}
