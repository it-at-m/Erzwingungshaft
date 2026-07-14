# Skript zum Aktualisieren aller Imports in Java-Dateien

$basePath = "C:\stef\repository\github\Erzwingungshaft\erzwingungshaft-eai\src\main\java\de\muenchen\eh"

# Alle Java-Dateien holen
$files = Get-ChildItem -Path $basePath -Recurse -Filter "*.java"

$count = 0
foreach ($f in $files) {
    $content = Get-Content $f.FullName -Raw
    $newContent = $content
    
    # Domain-Pakete
    $newContent = $newContent -replace 'import de\.muenchen\.eh\.claim\.', 'import de.muenchen.eh.domain.claim.'
    $newContent = $newContent -replace 'import de\.muenchen\.eh\.file\.', 'import de.muenchen.eh.domain.file.'
    
    # Infrastructure-Pakete
    $newContent = $newContent -replace 'import de\.muenchen\.eh\.common\.', 'import de.muenchen.eh.infrastructure.common.'
    $newContent = $newContent -replace 'import de\.muenchen\.eh\.db\.', 'import de.muenchen.eh.infrastructure.db.'
    $newContent = $newContent -replace 'import de\.muenchen\.eh\.log\.', 'import de.muenchen.eh.infrastructure.log.'
    $newContent = $newContent -replace 'import de\.muenchen\.eh\.integration\.', 'import de.muenchen.eh.infrastructure.integration.'
    
    if ($newContent -ne $content) {
        $newContent | Set-Content $f.FullName
        Write-Host "Updated: $($f.FullName)"
        $count++
    }
}

Write-Host "Total updated: $count"
