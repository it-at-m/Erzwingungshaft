$basePath = "C:\stef\repository\github\Erzwingungshaft\erzwingungshaft-eai\src\main\java\de\muenchen\eh"

# Alle Java-Dateien holen, die nicht in integration/ sind
$files = Get-ChildItem -Path $basePath -Recurse -Filter "*.java" | Where-Object { 
    $_.FullName -notlike "*\integration\*" 
}

$count = 0
foreach ($f in $files) {
    $content = Get-Content $f.FullName -Raw
    $newContent = $content -replace 'import de\.muenchen\.eh\.claim\.efile\.', 'import de.muenchen.eh.integration.efile.'
    $newContent = $newContent -replace 'import de\.muenchen\.eh\.claim\.xta\.', 'import de.muenchen.eh.integration.xta.'
    
    if ($newContent -ne $content) {
        $newContent | Set-Content $f.FullName
        Write-Host "Updated: $($f.FullName)"
        $count++
    }
}

Write-Host "Total updated: $count"
