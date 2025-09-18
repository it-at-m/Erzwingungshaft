package de.muenchen.eh.log.db;

import de.muenchen.eh.log.db.entity.ImportEntity;
import de.muenchen.eh.log.db.repository.ImportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportEntityCache {

    private final ImportRepository importRepository;
    private final HashMap<String, List<ImportEntity>> importEntityCache = new HashMap<>();


    public List<ImportEntity> getImportEntities(final String key) {

        if (!importEntityCache.containsKey(key)) {
            importEntityCache.put(key, importRepository.findByOutputDirectoryAndIsAntragImportIsNullAndIsBescheidImportIsNull(key));
        }
        return importEntityCache.get(key);
    }

    public void put(String key, ImportEntity value) {
        importEntityCache.put(key, new ArrayList<>(Arrays.asList(value)));
    }

    public void clear() {
        importEntityCache.entrySet().removeIf(entry ->
                entry.getValue().stream()
                        .allMatch(entity -> entity.getIsAntragImport() != null && entity.getIsBescheidImport() != null)
        );
        log.debug("Import entity cache is cleared. '{}' remaining entities.", importEntityCache.size());
    }
}
