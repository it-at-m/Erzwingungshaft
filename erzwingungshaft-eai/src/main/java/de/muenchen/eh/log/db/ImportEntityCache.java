package de.muenchen.eh.log.db;

import de.muenchen.eh.log.db.entity.ClaimImport;
import de.muenchen.eh.log.db.repository.ClaimImportRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportEntityCache {

    private final ClaimImportRepository claimImportRepository;
    private final HashMap<String, List<ClaimImport>> claimImportCache = new HashMap<>();

    public List<ClaimImport> getImportEntities(final String key) {

        if (!claimImportCache.containsKey(key)) {
            claimImportCache.put(key, claimImportRepository.findByOutputDirectoryAndIsAntragImportIsNullAndIsBescheidImportIsNull(key));
        }
        return claimImportCache.get(key);
    }

    public void put(String key, ClaimImport value) {
        claimImportCache.put(key, new ArrayList<>(Arrays.asList(value)));
    }

    public void clear() {
        claimImportCache.entrySet().removeIf(entry -> entry.getValue().stream()
                .allMatch(entity -> entity.getIsAntragImport() != null && entity.getIsBescheidImport() != null));
        log.debug("Import entity cache is cleared. '{}' remaining entities.", claimImportCache.size());
    }
}
