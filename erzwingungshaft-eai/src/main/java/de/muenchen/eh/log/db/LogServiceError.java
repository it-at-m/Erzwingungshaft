package de.muenchen.eh.log.db;

import de.muenchen.eh.log.Constants;
import de.muenchen.eh.log.db.entity.*;
import de.muenchen.eh.log.db.repository.ClaimLogRepository;
import de.muenchen.eh.log.db.repository.ClaimImportLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class LogServiceError {

    private final ClaimLogRepository claimLogRepository;
    private final ClaimImportLogRepository claimImportLogRepository;

    public void logError(final Exchange exchange) {

        try {
            Optional<ClaimImport> importEntity = Optional.ofNullable(exchange.getProperty(Constants.CLAIM_IMPORT, ClaimImport.class));
            importEntity.ifPresentOrElse(ie -> {
                ClaimImportLog claimImportLog = new ClaimImportLog();
                claimImportLog.setClaimImportId(ie.getId());
                claimImportLog.setMessageType(MessageType.ERROR);
                claimImportLog.setMessage(getMessage(exchange));
                var stack = getStack(exchange);
                claimImportLog.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
                claimImportLogRepository.save(claimImportLog);
                log.error(claimImportLog.toString());

                if (exchange.getProperty(Constants.CLAIM) != null) {
                    createClaimLogError(exchange);
                }

            }, () -> {
                createClaimLogError(exchange);
            });
        } catch (Exception e) {
            exchange.setException(e);
            log.error(e.getMessage());
        }

    }

    private void createClaimLogError(Exchange exchange) {
        ClaimLog claimLogEntity = (ClaimLog) ClaimFactory.configureEntity(new ClaimLog(), exchange);
        claimLogEntity.setMessageTyp(MessageType.ERROR);
        claimLogEntity.setMessage(getMessage(exchange));
        var stack = getStack(exchange);
        claimLogEntity.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");
        claimLogRepository.save(claimLogEntity);
        log.error(claimLogEntity.toString());
    }

    public static StackTraceElement[] getStack(Exchange exchange) {
        return exchange.getException() != null ? exchange.getException().getStackTrace() : ((Exception) exchange.getAllProperties().get(Exchange.EXCEPTION_CAUGHT)).getStackTrace();
    }

    public static String getMessage(Exchange exchange) {

        Exception ex = exchange.getException();
        if (ex == null) {
            ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        }
        var message = ex.getMessage() != null ? ex.getMessage() : ex.toString();
        return String.valueOf(message);
    }

}
