package de.muenchen.eh.infrastructure.db;

import de.muenchen.eh.infrastructure.db.entity.ClaimImport;
import de.muenchen.eh.infrastructure.db.repository.ClaimImportRepository;
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
    private int requeryCount = 0;
    private int totalCount = 0;

    public List<ClaimImport> getImportEntities(final String key) {

        totalCount++;

        if (!claimImportCache.containsKey(key)) {
            claimImportCache.put(key, claimImportRepository.findByOutputDirectoryAndIsAntragImportIsNullAndIsBescheidImportIsNull(key));
            requeryCount++;
        }
        return claimImportCache.get(key);
    }

    public void put(String key, ClaimImport value) {
        claimImportCache.put(key, new ArrayList<>(Arrays.asList(value)));
    }

    public void statistic() {
        log.info("Import cache 'preload hit' rate (relative to total cache requests): {} %", hitRate(totalCount, requeryCount));
        requeryCount = 0;
        totalCount = 0;
    }

    public static double hitRate(int totalCount, int requeryCount) {
        if (totalCount == 0) {
            return 0.0;
        }
        return ((double) (totalCount - requeryCount) / totalCount) * 100;
    }
}
