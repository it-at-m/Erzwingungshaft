package de.muenchen.eh.log.db;

import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.entity.*;
import de.muenchen.eh.log.db.repository.ClaimLogRepository;
import de.muenchen.eh.log.db.repository.ImportLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class EhServiceError {

    private final ClaimLogRepository claimLogRepository;
    private final ImportLogRepository importLogRepository;

    public void logError(final Exchange exchange) {

        try {
            Optional<ImportEntity> importEntity = Optional.ofNullable(exchange.getIn().getHeader(Constants.IMPORT_ENTITY, ImportEntity.class));
            importEntity.ifPresentOrElse( ie -> {
                ImportLogEntity importLogEntity = new ImportLogEntity();
                importLogEntity.setImportId(ie.getId());
                importLogEntity.setMessageTyp(MessageType.ERROR);
                importLogEntity.setMessage(getMessage(exchange));
                var stack = getStack(exchange);
                importLogEntity.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
                importLogRepository.save(importLogEntity);
                log.error(importLogEntity.toString());
            } , () -> {
                ClaimLogEntity claimLogEntity = (ClaimLogEntity) ClaimFactory.configureEntity(new ClaimLogEntity(), exchange);
                claimLogEntity.setMessageTyp(MessageType.ERROR);
                claimLogEntity.setMessage(getMessage(exchange));
                var stack = getStack(exchange);
                claimLogEntity.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
                claimLogRepository.save(claimLogEntity);
                log.error(claimLogEntity.toString());
            });
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e.getMessage());
        }

    }

    public static StackTraceElement[] getStack(Exchange exchange) {
        return exchange.getException() != null ? exchange.getException().getStackTrace() : ((Exception) exchange.getAllProperties().get(Exchange.EXCEPTION_CAUGHT)).getStackTrace();
    }

    public static String getMessage(Exchange exchange) {
        return exchange.getException() != null ? exchange.getException().getMessage() : ((Exception) exchange.getAllProperties().get(Exchange.EXCEPTION_CAUGHT)).getMessage();
    }


}
