package br.unitins.topicos2.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import br.unitins.topicos2.dto.FaixaDTO;
import br.unitins.topicos2.dto.FaixaResponseDTO;
import br.unitins.topicos2.model.Faixa;
import br.unitins.topicos2.model.Modalidade;
import br.unitins.topicos2.repository.FaixaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class FaixaServiceImpl implements FaixaService {

    @Inject
    FaixaRepository faixaRepository;

    @Inject
    Validator validator;

    @Override
    public List<FaixaResponseDTO> getAll(int page, int pageSize) {

        List<Faixa> list = faixaRepository.findAll().page(page, pageSize).list();
        return list.stream().map(e -> FaixaResponseDTO.valueOf(e)).collect(Collectors.toList());
    }

    @Override
    public FaixaResponseDTO findById(Long id) {
        Faixa faixa = faixaRepository.findById(id);
        if (faixa == null)
            throw new NotFoundException("Faixa não encontrada.");
        return FaixaResponseDTO.valueOf(faixa);
    }

    @Override
    @Transactional
    public FaixaResponseDTO create(@Valid FaixaDTO faixaDTO) throws ConstraintViolationException {
        Faixa entity = new Faixa();
        entity.setNome(faixaDTO.nome());
        entity.setDescricao(faixaDTO.descricao());
        entity.setPreco(faixaDTO.preco());
        entity.setEstoque(faixaDTO.estoque());
        entity.setModalidade(Modalidade.valueOf(faixaDTO.idModalidade()));

        faixaRepository.persist(entity);

        return FaixaResponseDTO.valueOf(entity);
    }

    @Override
    @Transactional
    public FaixaResponseDTO update(Long id, FaixaDTO faixaDTO) throws ConstraintViolationException {
        validar(faixaDTO);

        Faixa entity = faixaRepository.findById(id);

        entity.setNome(faixaDTO.nome());
        entity.setDescricao(faixaDTO.descricao());
        entity.setPreco(faixaDTO.preco());
        entity.setEstoque(faixaDTO.estoque());
        entity.setModalidade(Modalidade.valueOf(faixaDTO.idModalidade()));

        return FaixaResponseDTO.valueOf(entity);
    }

    @Override
    @Transactional
    public FaixaResponseDTO salveImage(Long id, String nomeImagem) {

        Faixa entity = faixaRepository.findById(id);
        entity.setNomeImagem(nomeImagem);

        return FaixaResponseDTO.valueOf(entity);
    }

    private void validar(FaixaDTO faixaDTO) throws ConstraintViolationException {
        Set<ConstraintViolation<FaixaDTO>> violations = validator.validate(faixaDTO);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);

    }

    @Override
    @Transactional
    public void delete(Long id) {
        faixaRepository.deleteById(id);
    }

    @Override
    public List<FaixaResponseDTO> findByNome(String nome, int page, int pageSize) {
        List<Faixa> list = faixaRepository.findByNome(nome).page(page, pageSize).list();
        return list.stream().map(e -> FaixaResponseDTO.valueOf(e)).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return faixaRepository.count();
    }

    @Override
    public long countByNome(String nome) {
        return faixaRepository.findByNome(nome).count();
    }

    @Override
    public byte[] createReportFaixas(String filterNome) {
        List<Faixa> lista = faixaRepository.findByNome(filterNome).list();
        return gerarRelatorioPDF(lista);
    }

    private byte[] gerarRelatorioPDF(List<Faixa> faixas) {
        // Crie um ByteArrayOutputStream para armazenar o PDF resultante
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Crie um documento PDF usando o iText7
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            
            Document document = new Document(pdfDocument, PageSize.A4);
            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new HeaderFooterHandler());

            // Adicione um cabeçalho ao PDF
            // Image logo = new Image(ImageDataFactory.create("caminho/para/sua/logo.png"));
            // document.add(logo);


            // Adicione um título e um subtítulo
            Paragraph titulo = new Paragraph("Relatório de Faixas")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16);
                
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm"));
            Paragraph subtitulo = new Paragraph("Gerado em: " + dataHora)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12);
            document.add(titulo);
            document.add(subtitulo);

            // Adicione a tabela com os itens
            Table tabela = new Table(new float[]{1, 2, 1})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(10);
            tabela.addHeaderCell("ID");
            tabela.addHeaderCell("Nome");
            tabela.addHeaderCell("Preço");

            for (Faixa faixa : faixas) {
                tabela.addCell(String.valueOf(faixa.getId()));
                tabela.addCell(faixa.getNome());
                tabela.addCell(String.valueOf(faixa.getPreco()));
            }

            document.add(tabela);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    class HeaderFooterHandler implements IEventHandler {
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNum = pdf.getPageNumber(page);

            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
            canvas.beginText().setFontAndSize(pdf.getDefaultFont(), 12);
            
            canvas.moveText(34, 20).showText("Página "+ pageNum);

            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss"));
            canvas.moveText(500 - 80, 0).showText(dataHora);

            canvas.endText();
                  
        }
    }

    
}