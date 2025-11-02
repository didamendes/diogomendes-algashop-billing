package com.diogomendes.algashop.billing.infrastructure.persistence.invoice;

import com.diogomendes.algashop.billing.application.invoice.query.InvoiceOutput;
import com.diogomendes.algashop.billing.application.invoice.query.InvoiceQueryService;
import com.diogomendes.algashop.billing.application.utility.Mapper;
import com.diogomendes.algashop.billing.domain.model.invoice.Invoice;
import com.diogomendes.algashop.billing.domain.model.invoice.InvoiceNotFoundException;
import com.diogomendes.algashop.billing.domain.model.invoice.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvoiceQueryServiceInvoiceQueryServiceImpl implements InvoiceQueryService {

    private final InvoiceRepository invoiceRepository;
    private final Mapper mapper;

    @Override
    public InvoiceOutput findByOrderId(String orderId) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(InvoiceNotFoundException::new);
        return mapper.convert(invoice, InvoiceOutput.class);
    }
}
