package com.diogomendes.algashop.billing.infrastructure.listener;

import com.diogomendes.algashop.billing.domain.model.invoice.InvoiceCanceledEvent;
import com.diogomendes.algashop.billing.domain.model.invoice.InvoiceIssuedEvent;
import com.diogomendes.algashop.billing.domain.model.invoice.InvoicePaidEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceEventListener {

    @EventListener
    public void listen(InvoiceIssuedEvent event) {

    }

    @EventListener
    public void listen(InvoicePaidEvent event) {

    }

    @EventListener
    public void listen(InvoiceCanceledEvent event) {

    }

}
